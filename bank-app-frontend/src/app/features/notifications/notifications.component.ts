import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { take, tap } from 'rxjs/operators';

import { Notification } from '../../shared/models/notification.model';
import { AppState } from '../../store/models/app-state.model';
import * as fromNotifications from '../../store/selectors/notification.selectors';
import * as NotificationActions from '../../store/actions/notification.actions';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications$: Observable<Notification[]>;
  allNotifications$: Observable<Notification[]>;
  unreadNotifications$: Observable<Notification[]>;
  readNotifications$: Observable<Notification[]>;
  filteredNotifications$: Observable<Notification[]>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;
  filter$: Observable<'all' | 'read' | 'unread'>;
  
  private subscription = new Subscription();

  constructor(
    private store: Store<AppState>,
    private router: Router
  ) {
    console.log('NotificationsComponent constructor');
    this.notifications$ = this.store.select(fromNotifications.selectAllNotifications)
      .pipe(tap(notifications => console.log('Component received notifications:', notifications)));
    this.allNotifications$ = this.store.select(fromNotifications.selectAllNotifications);
    this.unreadNotifications$ = this.store.select(fromNotifications.selectUnreadNotifications);
    this.readNotifications$ = this.store.select(fromNotifications.selectReadNotifications);
    this.filteredNotifications$ = this.store.select(fromNotifications.selectFilteredNotifications);
    this.loading$ = this.store.select(fromNotifications.selectNotificationsLoading);
    this.error$ = this.store.select(fromNotifications.selectNotificationsError);
    this.filter$ = this.store.select(fromNotifications.selectSelectedFilter);
  }

  ngOnInit(): void {
    console.log('NotificationsComponent ngOnInit - dispatching loadNotifications action');
    this.loadNotifications();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  loadNotifications(): void {
    console.log('NotificationsComponent - loadNotifications() called');
    this.store.dispatch(NotificationActions.loadNotifications());
    
    // For debugging: Add a test notification if the store doesn't show any
    this.allNotifications$.pipe(take(1)).subscribe(notifications => {
      if (!notifications || notifications.length === 0) {
        console.log('No notifications found - adding a test notification for debug');
        setTimeout(() => {
          this.addTestNotification();
        }, 2000); // Wait 2 seconds before adding test notification
      }
    });
  }

  // Test method to manually add a notification for debugging
  addTestNotification(): void {
    console.log('Adding test notification');
    // Create a test notification object
    const testNotification: Notification = {
      id: 999,
      recipient: 'test-user',
      subject: 'Test Notification',
      content: 'This is a test notification to verify the component is working correctly.',
      notificationType: 'CONSOLE',
      sentAt: new Date().toISOString(),
      status: 'SENT',
      read: false
    };

    // Use our new action to directly add the test notification to the store
    this.store.dispatch(NotificationActions.addTestNotification({ notification: testNotification }));
  }

  refreshNotifications(): void {
    this.loadNotifications();
  }

  markAllAsRead(): void {
    this.store.dispatch(NotificationActions.markAllAsRead());
  }

  markAsRead(id: number): void {
    this.store.dispatch(NotificationActions.markAsRead({ id }));
  }

  setFilter(filterType: 'all' | 'read' | 'unread'): void {
    this.store.dispatch(NotificationActions.setFilter({ filter: filterType }));
  }

  deleteNotification(id: number): void {
    if (confirm('Are you sure you want to delete this notification?')) {
      this.store.dispatch(NotificationActions.deleteNotification({ id }));
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