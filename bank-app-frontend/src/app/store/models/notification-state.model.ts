import { Notification } from '../../shared/models/notification.model';

export interface NotificationState {
  notifications: Notification[];
  unreadNotifications: Notification[];
  readNotifications: Notification[];
  loading: boolean;
  error: string | null;
  selectedFilter: 'all' | 'read' | 'unread';
} 