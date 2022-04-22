import { AuthorizationJwtInterceptor } from './core/interceptor/authorization-jwt.interceptor';
import { NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS} from '@angular/common/http';
import { AuthenticationService } from './modules/services-module/service-authentication-registration/authentication.service';
import { EmployeeManagementService } from './modules/services-module/service-crud-employee/employee-management.service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './modules/home-page-module/home-page.component';
import { NavbarComponent } from './modules/home-page-module/navbar/navbar.component';
import { FooterComponent } from './modules/home-page-module/footer/footer.component';
import { DashboardAdminComponent } from './modules/dashboard-admin-module/dashboard-admin/dashboard-admin.component';
import { AuthenticationGuard } from './core/guard/authentication.guard';
import { NotificationModule } from './modules/notification-module/notification.module';
import { NotificationService } from './modules/notification-module/notification-service/notification.service';
import { DashboardEmployeeComponent } from './modules/dashboard-employee-module/dashboard-employee/dashboard-employee.component';
import { FormsModule, ReactiveFormsModule, NgModel } from '@angular/forms';
@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    NavbarComponent,
    FooterComponent,
    DashboardAdminComponent,
    DashboardEmployeeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NotificationModule
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    EmployeeManagementService, 
    {provide: HTTP_INTERCEPTORS, useClass: AuthorizationJwtInterceptor, multi: true}, 
    NotificationService,
    Title],

  bootstrap: [AppComponent]
})
export class AppModule { }
