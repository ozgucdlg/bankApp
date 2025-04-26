import { ActionReducerMap } from '@ngrx/store';
import { AppState } from './models/app-state.model';
import { notificationReducer } from './reducers/notification.reducer';
import { NotificationEffects } from './effects/notification.effects';

export const reducers: ActionReducerMap<AppState> = {
  notifications: notificationReducer
};

export const effects = [
  NotificationEffects
];

export * from './actions/notification.actions';
export * from './selectors/notification.selectors'; 