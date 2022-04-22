import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee } from 'src/app/core/model/employee-model/employee';
import { JwtHelperService } from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  //the url of the authentication 
  public host = environment.apiUrl;
  private token: string | null;
  private loggedInUsername: String | null;
  private jwtHelper = new JwtHelperService();

  //dependency injection of http client inside the service 
  constructor(private http: HttpClient) {}

  //login method
   public login(employee: Employee): Observable<HttpResponse<Employee>> {
    return this.http.post<Employee>
    (`${this.host}/employee/login`, employee, {observe: 'response'});
  }

  //register method
  public register(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>
    (`${this.host}/employee/register`, employee);
  }

  //logout method
  public logOut(): void {
    this.token = null;
    this.loggedInUsername = null;
    localStorage.removeItem('employee');
    localStorage.removeItem('token');
    localStorage.removeItem('employees');
  }

   //save token method
   public saveToken(token: string ): void {
    this.token = token;
    localStorage.setItem('token', token);
  }
  
  //add employee to local cache method
  public addEmployeeToLocalCache(employee: Employee ): void {
    //Json.stringify will transform the employee from employee object to a string so that we could store it in the local storage
    //( local storage accept only string)

    localStorage.setItem('employee', JSON.stringify(employee));
  }

  //get employee from local cache
  public getEmployeeFromLocalCache(): Employee {
    return JSON.parse(localStorage.getItem('employee') || '{}') ;
  }

   //load the token from the local storage
   public loadToken(): void {
    this.token = localStorage.getItem('token');
  }

  //get the token
  public getToken(): string | null {
    return this.token;
  }

  //check if the employee is logged in or not
  public isEmployeeLoggedIn(): boolean {
    //we need to load the token 
    this.loadToken();
    //if the token is not null and not empty
    if(this.token != null && this.token !== ''){
      if(this.jwtHelper.decodeToken(this.token).sub != null || ''){
        if(!this.jwtHelper.isTokenExpired(this.token)){
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        }
      }
    }else{
      this.logOut();
      return false;
    }
    return true;
  }


}
