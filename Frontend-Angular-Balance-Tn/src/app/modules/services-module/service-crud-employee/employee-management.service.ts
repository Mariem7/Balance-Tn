import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee } from 'src/app/core/model/employee-model/employee';
import { CustomHttpResponse } from 'src/app/core/model/custom-http-response-model/custom-http-response';

@Injectable({
  providedIn: 'root'
})
export class EmployeeManagementService {

  //the url of the authentication 
  public host = environment.apiUrl;

  //dependency injection of http client inside the service 
  constructor(private http: HttpClient) {}

  //get employees method
  public getEmployees(): Observable<Employee[] | HttpErrorResponse>{
    return this.http.get<Employee[]>(`${this.host}/employee/find-all`);
  }

  //add employee method
  //we use the formData because in postman we passed the data in form-data section (then to use @Requestparam in spring)
  public addEmployee(formData: FormData): Observable<Employee | HttpErrorResponse>{
    return this.http.post<Employee>(`${this.host}/employee/add`, formData);
  }

  //update employee method
  public updateEmployee(formData: FormData): Observable<Employee | HttpErrorResponse>{
    return this.http.post<Employee>(`${this.host}/employee/update`, formData);
  }

  //reset password method
  public resetPassword(username: string): Observable<CustomHttpResponse | HttpErrorResponse>{
    return this.http.get<CustomHttpResponse>(`${this.host}/employee/reset-password/${username}`);
  }

  //update profile image
  public updateProfileImage(formData: FormData): Observable<HttpEvent<any> | HttpErrorResponse>{
    return this.http.post<Employee>(`${this.host}/employee/update-profile-image`, formData,
     {reportProgress: true, observe: 'events'});
  }

  //delete employee method
  public deleteEmployee(employeeId: number): Observable<CustomHttpResponse | HttpErrorResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/employee/delete/${employeeId}`);
  }

  //add employee to local cache method
  public addEmployeeToLocalCache(employees: Employee[]): void{
    localStorage.setItem('employees' ,JSON.stringify(employees));
  }

  //get employees from local storage method
  public getEmployeeFromLocalCache(): Employee[] | null {
    //if we have employees in the local storage 
    if(localStorage.getItem('employees')){
      return JSON.parse(localStorage.getItem('employees') || '{}');
    }
    return null;
  }

   //create employee form data method
   public createEmployeeFormData(loggedInUsername: string, employee: Employee, profileImage: File): FormData {
    const formData = new FormData();
    formData.append('currentUsername',loggedInUsername);
    formData.append('firstName',employee.firstName);
    formData.append('lastName',employee.lastName);
    formData.append('username',employee.username);
    formData.append('function',employee.function);
    formData.append('phoneNumber',JSON.stringify(employee.phoneNumber));
    formData.append('nicNumber',JSON.stringify(employee.nicNumber));
    formData.append('role',employee.role);
    formData.append('isEnabled',JSON.stringify(employee.isEnabled));
    formData.append('isNotlocked',JSON.stringify(employee.isNotlocked));
    return formData;

  }

}
