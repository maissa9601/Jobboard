import { ApplicationConfig } from '@angular/core';
import {provideRouter} from '@angular/router';
import { routes } from './app.routes';
import { AuthService } from './service/auth.service';
import { provideHttpClient } from '@angular/common/http';
import { provideOAuthClient } from 'angular-oauth2-oidc';
import {provideToastr} from 'ngx-toastr';
import {provideAnimations} from '@angular/platform-browser/animations';
import {FilterByFieldPipe} from './service/filter.pipe';
import {MatIconModule} from '@angular/material/icon';


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    AuthService,
    provideOAuthClient(),
    FilterByFieldPipe,
    MatIconModule,
    provideToastr({
      positionClass: 'toast-center-custom',
      timeOut: 3000,
      closeButton: true,
      progressBar: true,

    }),

    provideAnimations()

  ]
};
