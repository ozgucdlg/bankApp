import { createReducer, on } from '@ngrx/store';
import { NotificationState } from '../models/notification-state.model';
import * as NotificationActions from '../actions/notification.actions';

export const initialState: NotificationState = {
  notifications: [],
  unreadNotifications: [],
  readNotifications: [],
  loading: false,
  error: null,
  selectedFilter: 'all'
};

export const notificationReducer = createReducer(
  initialState,
  
  // Load notifications
  on(NotificationActions.loadNotifications, state => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(NotificationActions.loadNotificationsSuccess, (state, { notifications }) => {
    const unreadNotifications = notifications.filter(n => !n.read);
    const readNotifications = notifications.filter(n => n.read);
    
    return {
      ...state,
      notifications,
      unreadNotifications,
      readNotifications,
      loading: false
    };
  }),
  
  on(NotificationActions.loadNotificationsFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),
  
  // Mark as read
  on(NotificationActions.markAsRead, state => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(NotificationActions.markAsReadSuccess, (state, { id }) => {
    const updatedNotifications = state.notifications.map(n => 
      n.id === id ? { ...n, read: true } : n
    );
    
    return {
      ...state,
      notifications: updatedNotifications,
      unreadNotifications: updatedNotifications.filter(n => !n.read),
      readNotifications: updatedNotifications.filter(n => n.read),
      loading: false
    };
  }),
  
  on(NotificationActions.markAsReadFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),
  
  // Mark all as read
  on(NotificationActions.markAllAsRead, state => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(NotificationActions.markAllAsReadSuccess, state => {
    const updatedNotifications = state.notifications.map(n => ({ ...n, read: true }));
    
    return {
      ...state,
      notifications: updatedNotifications,
      unreadNotifications: [],
      readNotifications: updatedNotifications,
      loading: false
    };
  }),
  
  on(NotificationActions.markAllAsReadFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),
  
  // Delete notification
  on(NotificationActions.deleteNotification, state => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(NotificationActions.deleteNotificationSuccess, (state, { id }) => {
    const updatedNotifications = state.notifications.filter(n => n.id !== id);
    
    return {
      ...state,
      notifications: updatedNotifications,
      unreadNotifications: updatedNotifications.filter(n => !n.read),
      readNotifications: updatedNotifications.filter(n => n.read),
      loading: false
    };
  }),
  
  on(NotificationActions.deleteNotificationFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),
  
  // Set filter
  on(NotificationActions.setFilter, (state, { filter }) => ({
    ...state,
    selectedFilter: filter
  })),
  
  // Add test notification
  on(NotificationActions.addTestNotification, (state, { notification }) => {
    const updatedNotifications = [...state.notifications, notification];
    const unreadNotifications = updatedNotifications.filter(n => !n.read);
    const readNotifications = updatedNotifications.filter(n => n.read);
    
    return {
      ...state,
      notifications: updatedNotifications,
      unreadNotifications,
      readNotifications,
      loading: false,
      error: null
    };
  })
); 