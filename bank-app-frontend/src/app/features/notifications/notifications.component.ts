import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { Notification } from '../../shared/models/notification.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  private subscription: Subscription = new Subscription();

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadNotifications();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  loadNotifications(): void {
    this.subscription.add(
      this.notificationService.getNotifications().subscribe(notifications => {
        this.notifications = notifications;
      })
    );
  }

  refreshNotifications(): void {
    this.notificationService.refreshNotifications();
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe(() => {
      this.loadNotifications();
    });
  }

  deleteNotification(id: number): void {
    if (confirm('Are you sure you want to delete this notification?')) {
      this.notificationService.deleteNotification(id).subscribe(() => {
        this.loadNotifications();
      });
    }
  }

  formatDate(date: string | null | undefined): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  goBackToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
} 