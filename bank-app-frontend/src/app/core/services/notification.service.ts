import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notification } from '../../shared/models/notification.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notifications.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    // Load notifications when service is initialized
    this.loadNotifications();
  }

  private loadNotifications() {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.http.get<Notification[]>(`${environment.apiUrl}/notifications/recipient/${currentUser.username}`)
        .subscribe({
          next: (notifications) => this.notifications.next(notifications),
          error: (error) => console.error('Error loading notifications:', error)
        });
    }
  }

  getNotifications(): Observable<Notification[]> {
    return this.notifications$;
  }

  sendNotification(recipient: string, subject: string, content: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/notifications/send`, { recipient, subject, content });
  }

  // Method to manually refresh notifications
  refreshNotifications() {
    this.loadNotifications();
  }

  // Start polling for new notifications
  startPolling(interval: number = 30000) { // Default 30 seconds
    setInterval(() => this.loadNotifications(), interval);
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/api/notifications/${id}/read`, {});
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/api/notifications/${id}`);
  }
} 