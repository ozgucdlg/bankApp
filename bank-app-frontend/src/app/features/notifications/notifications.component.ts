import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../core/services/notification.service';
import { Notification } from '../../shared/models/notification.model';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  private subscription: Subscription;

  constructor(public notificationService: NotificationService) {
    this.subscription = new Subscription();
  }

  ngOnInit() {
    // Subscribe to notifications
    this.subscription.add(
      this.notificationService.getNotifications().subscribe(
        notifications => this.notifications = notifications.sort((a, b) => 
          new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()
        )
      )
    );

    // Start polling for new notifications
    this.notificationService.startPolling();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  refreshNotifications(): void {
    this.notificationService.refreshNotifications();
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SENT':
        return 'text-success';
      case 'FAILED':
        return 'text-danger';
      default:
        return 'text-warning';
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
} 