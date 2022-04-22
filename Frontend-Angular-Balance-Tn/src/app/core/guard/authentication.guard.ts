import { NotificationService } from './../../modules/notification-module/notification-service/notification.service';
import { AuthenticationService } from './../../modules/services-module/service-authentication-registration/authentication.service';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { NotificationType } from 'src/app/modules/enum/notification-type.enum';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {
 
  constructor(private authenticationService: AuthenticationService,private router: Router,
    private notificationService: NotificationService){}
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean{
    return this.isEmployeeLoggedIn();
  }

  private isEmployeeLoggedIn(): boolean{
    if(this.authenticationService.isEmployeeLoggedIn()){
      return true;
    }//if the user is not loggedIn then we will return him to the login page
    this.router.navigate(['/login']);
    //send notification to employee
    this.notificationService.notify(NotificationType.ERROR,`You need to log in to access this page`.toUpperCase());
    return false;
  }
  
}
