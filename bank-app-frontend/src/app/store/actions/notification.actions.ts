import { createAction, props } from '@ngrx/store';
import { Notification } from '../../shared/models/notification.model';

// Load Notifications
export const loadNotifications = createAction(
  '[Notification] Load Notifications'
);

export const loadNotificationsSuccess = createAction(
  '[Notification] Load Notifications Success',
  props<{ notifications: Notification[] }>()
);

export const loadNotificationsFailure = createAction(
  '[Notification] Load Notifications Failure',
  props<{ error: string }>()
);

// Mark as Read
export const markAsRead = createAction(
  '[Notification] Mark As Read',
  props<{ id: number }>()
);

export const markAsReadSuccess = createAction(
  '[Notification] Mark As Read Success',
  props<{ id: number }>()
);

export const markAsReadFailure = createAction(
  '[Notification] Mark As Read Failure',
  props<{ error: string }>()
);

// Mark All as Read
export const markAllAsRead = createAction(
  '[Notification] Mark All As Read'
);

export const markAllAsReadSuccess = createAction(
  '[Notification] Mark All As Read Success'
);

export const markAllAsReadFailure = createAction(
  '[Notification] Mark All As Read Failure',
  props<{ error: string }>()
);

// Delete Notification
export const deleteNotification = createAction(
  '[Notification] Delete Notification',
  props<{ id: number }>()
);

export const deleteNotificationSuccess = createAction(
  '[Notification] Delete Notification Success',
  props<{ id: number }>()
);

export const deleteNotificationFailure = createAction(
  '[Notification] Delete Notification Failure',
  props<{ error: string }>()
);

// Set Filter
export const setFilter = createAction(
  '[Notification] Set Filter',
  props<{ filter: 'all' | 'read' | 'unread' }>()
);

// Add Test Notification (For debugging only)
export const addTestNotification = createAction(
  '[Notification] Add Test Notification',
  props<{ notification: Notification }>()
); 