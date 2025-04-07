import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { AuthService } from './service/auth.service';

import { provideHttpClient } from '@angular/common/http';
import { provideOAuthClient } from 'angular-oauth2-oidc';
import {AuthGuard} from './service/auth.guard';
import {AdminGuard} from './service/admin.guard';
import {provideToastr} from 'ngx-toastr';
import {provideAnimations} from '@angular/platform-browser/animations';
import {FilterPipe} from './service/filter.pipe';
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    AuthService,
    provideOAuthClient(),
    AuthGuard,
    AdminGuard,
    FilterPipe,
    provideToastr({
      positionClass: 'toast-center-custom',
      timeOut: 5000,
      closeButton: true,
      progressBar: true,

    }),
    provideAnimations()

  ]
};
