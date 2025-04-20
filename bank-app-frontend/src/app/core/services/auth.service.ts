import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { User } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.currentUserSubject = new BehaviorSubject<User | null>(
      JSON.parse(localStorage.getItem('currentUser') || 'null')
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string): Observable<User> {
    return this.http.post<any>(`${environment.apiUrl}/auth/login`, { username, password })
      .pipe(map(response => {
        // Create the user object from the response
        const user: User = {
          id: response.id,
          username: response.username,
          email: response.email,
          accountId: response.account?.id,
          role: response.role || 'USER',
          token: btoa(`${username}:${password}`) // Basic auth token
        };
        
        // Store user details and basic auth token in local storage
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      }));
  }

  register(user: Partial<User>): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/auth/register`, user);
  }

  logout(): void {
    console.log('Starting logout process');
    
    // Clear all storage first
    localStorage.clear();
    sessionStorage.clear();
    
    // Clear current user
    this.currentUserSubject.next(null);
    
    // Make the API call to logout - using absolute URL to avoid any path issues
    const logoutUrl = `${environment.apiUrl}/auth/logout`;
    console.log('Attempting to call logout endpoint:', logoutUrl);
    
    fetch(logoutUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    .then(response => {
      console.log('Logout API response:', response.status, response.statusText);
      return response.text();
    })
    .then(data => {
      console.log('Logout API data:', data);
      // Force a full page reload and navigation
      window.location.href = '/login';
    })
    .catch(error => {
      console.error('Error during logout:', error);
      // Force a full page reload and navigation even if API call fails
      window.location.href = '/login';
    });
  }
} 