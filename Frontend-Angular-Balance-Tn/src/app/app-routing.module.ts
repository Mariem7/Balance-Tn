import { DashboardEmployeeComponent } from './modules/dashboard-employee-module/dashboard-employee/dashboard-employee.component';
import { DashboardAdminComponent } from './modules/dashboard-admin-module/dashboard-admin/dashboard-admin.component';
import { LoginComponent } from './modules/login-module/login/login.component';
import { HomePageComponent } from './modules/home-page-module/home-page.component';
import { AppComponent } from './app.component';
import { RegisterComponent } from './modules/register-module/register/register.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  
  {
    path:'home',
    component:HomePageComponent,
    data: {title:'Balance-tn'}
  },
  //lazy loading
  {
    path: 'login',
    loadChildren: () => import('./modules/login-module/login.module').then(m => m.LoginModule),
    data: {title: 'Login'}
  },
  {
    path: 'register',
    loadChildren: () => import('./modules/register-module/register.module').then(m => m.RegisterModule),
    data: {title: 'Registration'}
  },
  {
    path: 'dashboard-admin',
    component: DashboardAdminComponent,
    data: {title: 'Dashboard Admin'}
  },
  {
    path: 'dashboard-employee',
    component: DashboardEmployeeComponent,
    data: {title: 'Dashboard Employee'}
  },
  {
    path: '', 
    redirectTo: '/home',
    pathMatch: 'full'
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
