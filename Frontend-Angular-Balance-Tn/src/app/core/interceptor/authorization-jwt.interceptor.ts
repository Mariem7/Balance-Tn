import { AuthenticationService } from './../../modules/services-module/service-authentication-registration/authentication.service';
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthorizationJwtInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {}

  intercept(httpRequest: HttpRequest<any>, httpHandler: HttpHandler): Observable<HttpEvent<any>> {
    if(httpRequest.url.includes(`${this.authenticationService.host}/employee/login`)){
      //we will pass the request without adding header of JWT
      return httpHandler.handle(httpRequest);
    }

    if(httpRequest.url.includes(`${this.authenticationService.host}/employee/register`)){
      //we will pass the request without adding header of JWT
      return httpHandler.handle(httpRequest);
    }

    if(httpRequest.url.includes(`${this.authenticationService.host}/employee/reset-password`)){
      //we will pass the request without adding header of JWT
      return httpHandler.handle(httpRequest);
    }

    //here we will send the token with any other request
    this.authenticationService.loadToken();
    const token = this.authenticationService.getToken();
    //the request is immutable so we need to clone it then we will pass the token on it and after that
    //we will send the cloned request (which it is modified by adding the token to it) and send it to the handler

    const request = httpRequest.clone({ setHeaders :{Authorization: `Bearer ${token}`}})
    return httpHandler.handle(request);

  }
}
