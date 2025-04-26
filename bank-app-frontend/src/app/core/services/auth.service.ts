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
    // Initialize from localStorage
    const storedUser = localStorage.getItem('currentUser');
    console.log('AuthService - stored user:', storedUser);
    
    this.currentUserSubject = new BehaviorSubject<User | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    const user = this.currentUserSubject.value;
    console.log('AuthService.currentUserValue called, returning:', user);
    return user;
  }

  login(username: string, password: string): Observable<User> {
    return this.http.post<any>(`${environment.apiUrl}/api/auth/login`, { username, password })
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
    return this.http.post<User>(`${environment.apiUrl}/api/auth/register`, user);
  }

  logout(): void {
    // Clear all storage first
    localStorage.removeItem('currentUser');
    
    // Clear current user
    this.currentUserSubject.next(null);
    
    // Make the API call to logout
    this.http.post(`${environment.apiUrl}/api/auth/logout`, {})
      .subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: () => {
          this.router.navigate(['/login']);
        }
      });
  }
} 