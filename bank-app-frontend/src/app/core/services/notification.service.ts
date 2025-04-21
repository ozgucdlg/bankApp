import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, timer } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
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
    // Load notifications when service is initialized
    this.refreshNotifications();
    this.startPolling();
  }

  private loadNotifications() {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      const recipient = currentUser.username;
      console.log('Loading notifications for:', recipient);
      
      // Use the direct endpoint with query parameters
      this.http.get<Notification[]>(`${environment.apiUrl}/api/notifications/get-by-username?username=${recipient}`)
        .pipe(
          catchError(error => {
            console.error('Error loading notifications:', error);
            return of([]);
          })
        )
        .subscribe(notifications => {
          console.log('Notifications received:', notifications);
          this.notifications.next(notifications || []);
        });
    } else {
      this.notifications.next([]);
    }
  }

  getNotifications(): Observable<Notification[]> {
    return this.notifications$;
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

  // Method to manually refresh notifications
  refreshNotifications() {
    this.loadNotifications();
  }

  // Start polling for new notifications
  startPolling(interval: number = 10000) { // Default 10 seconds
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
  }

  stopPolling() {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/api/notifications/${id}/read`, {}).pipe(
      switchMap(() => {
        this.refreshNotifications();
        return of(undefined);
      })
    );
  }

  markAllAsRead(): Observable<void> {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      return this.http.put<void>(
        `${environment.apiUrl}/api/notifications/read-all`, 
        null,
        { params: new HttpParams().set('recipient', currentUser.username) }
      ).pipe(
        switchMap(() => {
          this.refreshNotifications();
          return of(undefined);
        })
      );
    }
    throw new Error('User not authenticated');
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/api/notifications/${id}`).pipe(
      switchMap(() => {
        this.refreshNotifications();
        return of(undefined);
      })
    );
  }
} 