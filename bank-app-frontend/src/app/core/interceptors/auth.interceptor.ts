import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const currentUser = this.authService.currentUserValue;
    const isApiUrl = request.url.startsWith(environment.apiUrl);
    
    // Skip adding auth headers for login and register endpoints
    const isAuthEndpoint = request.url.includes('/api/auth/login') || 
                          request.url.includes('/api/auth/register');
    
    if (currentUser && currentUser.token && isApiUrl && !isAuthEndpoint) {
      console.log('Adding auth token to request:', request.url);
      request = request.clone({
        setHeaders: {
          Authorization: `Basic ${currentUser.token}`
        }
      });
    }

    return next.handle(request);
  }
} 