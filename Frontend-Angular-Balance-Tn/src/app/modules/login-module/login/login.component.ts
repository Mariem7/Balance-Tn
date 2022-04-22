import { NotificationType } from 'src/app/modules/enum/notification-type.enum';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Employee } from './../../../core/model/employee-model/employee';
import { NotificationService } from './../../notification-module/notification-service/notification.service';
import { AuthenticationService } from './../../services-module/service-authentication-registration/authentication.service';
import { Router } from '@angular/router';
import { Component, OnDestroy, OnInit } from '@angular/core';
import * as AOS from 'aos';
import { Subscription } from 'rxjs';
import { HeaderType } from '../../enum/header-type.enum';
import { NgForm } from '@angular/forms';
import { NgModel } from '@angular/forms';
import { FormControl } from '@angular/forms';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  fieldTextType: boolean;
  public showLoading : boolean = false;
  private subscriptions: Subscription [] =[];
  
  constructor(private router: Router, private authenticationService: AuthenticationService,
     private notificationService: NotificationService) { }

  ngOnInit(): void {
    AOS.init();
    //if the employee is already logged in, we will redirect him to 
    if(this.authenticationService.isEmployeeLoggedIn()){

    }else{
      this.router.navigateByUrl('/login');
    }
  }
  //method for showing password field
  toggleFieldTextType() {
    this.fieldTextType = !this.fieldTextType;
  }

  //method to login
  public onLogin(employee: Employee) : void{
    this.showLoading= true;
    console.log(employee);
    this.subscriptions.push(
      this.authenticationService.login(employee).subscribe(
      (response: HttpResponse<Employee>) => {
        const token = response.headers.get(HeaderType.JWT_TOKEN);
        this.authenticationService.saveToken(token || '');
        this.authenticationService.addEmployeeToLocalCache(response.body!);
        this.router.navigateByUrl('/admin');
        this.showLoading =false;
      },
      (errorResponse: HttpErrorResponse)=>{
        console.log(errorResponse);
        this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message);
        this.showLoading =false;
      }
      )
    );
    
  }



  private sendErrorNotification(notificationType: NotificationType, message: string): void {
    if(message){
      this.notificationService.notify(notificationType,message);
    }else{
      this.notificationService.notify(notificationType, 'AN ERROR OCCURED. PLEASE TRY AGAIN');
    }
  }



  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }







}
