import { Routes } from '@angular/router';

import { SignupComponent } from './pages/auth/signup/signup.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { AdminComponent } from './pages/dash/admin/admin.component';


import {ResetComponent} from './pages/auth/reset/reset.component';
import {AuthGuard} from './service/auth.guard';
import {AdminGuard} from './service/admin.guard';
import {ActivationComponent} from './pages/auth/activation/activation.component';
import {CandidatComponent} from './pages/dash/candidat/candidat.component';

export const routes: Routes = [

  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  { path: 'reset', component: ResetComponent },
  { path: 'activate', component: ActivationComponent },
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'candidat', component: CandidatComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];
