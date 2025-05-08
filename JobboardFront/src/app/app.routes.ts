import { Routes } from '@angular/router';
import { SignupComponent } from './pages/auth/signup/signup.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { AdminComponent } from './pages/dash/admin/admin.component';
import {ResetComponent} from './pages/auth/reset/reset.component';
import {AuthGuard} from './service/auth.guard';
import {AdminGuard} from './service/admin.guard';
import {ActivationComponent} from './pages/auth/activation/activation.component';
import {AuthCallbackComponent} from './pages/auth/auth-callback/auth-callback.component';
import {OffersListComponent} from './pages/offres/offres-list/offres-list.component';
import {JobDetailsComponent} from './pages/offres/job-details/job-details.component';
import {HomeComponent} from './pages/home/home.component';
import {ProfileComponent} from './pages/dash/profile/profile.component';
import {CompleteProfileComponent} from './pages/dash/complete-profile/complete-profile.component';
import {CandidatComponent} from './pages/dash/candidat/candidat.component';




export const routes: Routes = [

  { path: 'signup', component: SignupComponent },
  { path: 'home', component: HomeComponent },
  {path: 'callback', component: AuthCallbackComponent },
  {path: 'offers', component: OffersListComponent },
  { path: 'offer/:id', component: JobDetailsComponent },
  { path: 'login', component: LoginComponent },
  { path: 'reset', component: ResetComponent },
  { path: 'activate', component: ActivationComponent },
  { path: 'admin', component: AdminComponent , canActivate: [AuthGuard, AdminGuard]},
  { path: 'complete', component: CompleteProfileComponent },
  { path: 'dashboard', component: ProfileComponent},
  { path: 'candidat', component: CandidatComponent},
  { path: '**', redirectTo: '' }
];
/*, canActivate: [AuthGuard, AdminGuard]*/
