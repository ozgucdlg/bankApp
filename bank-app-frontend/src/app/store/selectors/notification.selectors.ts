import { createFeatureSelector, createSelector } from '@ngrx/store';
import { NotificationState } from '../models/notification-state.model';

export const selectNotificationState = createFeatureSelector<NotificationState>('notifications');

export const selectAllNotifications = createSelector(
  selectNotificationState,
  (state: NotificationState) => state.notifications
);

export const selectUnreadNotifications = createSelector(
  selectNotificationState,
  (state: NotificationState) => state.unreadNotifications
);

export const selectReadNotifications = createSelector(
  selectNotificationState,
  (state: NotificationState) => state.readNotifications
);

export const selectNotificationsLoading = createSelector(
  selectNotificationState,
  (state: NotificationState) => state.loading
);

export const selectNotificationsError = createSelector(
  selectNotificationState,
  (state: NotificationState) => state.error
);

export const selectSelectedFilter = createSelector(
  selectNotificationState,
  (state: NotificationState) => state.selectedFilter
);

export const selectFilteredNotifications = createSelector(
  selectNotificationState,
  (state: NotificationState) => {
    switch (state.selectedFilter) {
      case 'read':
        return state.readNotifications;
      case 'unread':
        return state.unreadNotifications;
      case 'all':
      default:
        return state.notifications;
    }
  }
);

export const selectUnreadCount = createSelector(
  selectUnreadNotifications,
  (unreadNotifications) => unreadNotifications.length
); 