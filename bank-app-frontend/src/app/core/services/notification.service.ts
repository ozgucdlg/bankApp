import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, timer } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Notification } from '../../shared/models/notification.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notifications.asObservable();
  private pollingSubscription: any;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    console.log('NotificationService initialized');
    // We won't auto-refresh here anymore, NgRx will handle this
    // this.refreshNotifications();
    // this.startPolling();
  }

  // This is now a direct API call for NgRx to use
  getNotifications(): Observable<Notification[]> {
    console.log('NotificationService.getNotifications() called');
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      console.log('No current user, returning empty array');
      return of([]);
    }
    
    const recipient = currentUser.username;
    console.log('Getting notifications for user:', recipient);
    
    // Direct API call that returns the Observable for NgRx
    return this.http.get<Notification[]>(`${environment.apiUrl}/api/notifications/get-by-username?username=${recipient}`)
      .pipe(
        tap(notifications => {
          console.log('API returned notifications:', notifications);
          // Also update the BehaviorSubject for any legacy code
          this.notifications.next(notifications || []);
        }),
        catchError(error => {
          console.error('Error fetching notifications:', error);
          return of([]);
        })
      );
  }

  getUnreadNotificationsCount(): Observable<number> {
    return this.notifications$.pipe(
      switchMap(notifications => {
        const unreadCount = notifications.filter(n => !n.read).length;
        return of(unreadCount);
      })
    );
  }

  sendNotification(recipient: string, subject: string, content: string): Observable<any> {
    const params = new HttpParams()
      .set('recipient', recipient)
      .set('subject', subject)
      .set('content', content);
    
    return this.http.post<any>(
      `${environment.apiUrl}/api/notifications/send`, 
      null,
      { params }
    );
  }

  // Legacy method kept for backward compatibility
  refreshNotifications() {
    console.log('Refreshing notifications (legacy method)');
    this.loadNotifications();
  }

  private loadNotifications() {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      const recipient = currentUser.username;
      console.log('Loading notifications for:', recipient);
      
      this.http.get<Notification[]>(`${environment.apiUrl}/api/notifications/get-by-username?username=${recipient}`)
        .pipe(
          catchError(error => {
            console.error('Error loading notifications:', error);
            return of([]);
          })
        )
        .subscribe(notifications => {
          console.log('Notifications received (legacy):', notifications);
          this.notifications.next(notifications || []);
        });
    } else {
      this.notifications.next([]);
    }
  }

  // Start polling for new notifications - disabled for NgRx but kept for compatibility
  startPolling(interval: number = 10000) { // Default 10 seconds
    console.log('Starting notification polling - disabled in NgRx version');
    /*
    // Stop any existing polling
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
    
    // Start new polling
    this.pollingSubscription = timer(0, interval)
      .pipe(
        switchMap(() => {
          const user = this.authService.currentUserValue;
          if (user) {
            this.loadNotifications();
          }
          return of(null);
        })
      )
      .subscribe();
    */
  }

  stopPolling() {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  markAsRead(id: number): Observable<void> {
    console.log('Marking notification as read:', id);
    return this.http.put<void>(`${environment.apiUrl}/api/notifications/${id}/read`, {});
  }

  markAllAsRead(): Observable<void> {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      console.error('Cannot mark all as read - user not authenticated');
      throw new Error('User not authenticated');
    }
    
    console.log('Marking all notifications as read for:', currentUser.username);
    return this.http.put<void>(
      `${environment.apiUrl}/api/notifications/read-all`, 
      null,
      { params: new HttpParams().set('recipient', currentUser.username) }
    );
  }

  deleteNotification(id: number): Observable<void> {
    console.log('Deleting notification:', id);
    return this.http.delete<void>(`${environment.apiUrl}/api/notifications/${id}`);
  }
} 