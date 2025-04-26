import { Injectable, Inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, mergeMap, catchError, switchMap, tap } from 'rxjs/operators';
import { NotificationService } from '../../core/services/notification.service';
import * as NotificationActions from '../actions/notification.actions';

@Injectable({
  providedIn: 'root'
})
export class NotificationEffects {
  
  loadNotifications$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.loadNotifications),
    tap(() => console.log('Effect: Loading notifications')),
    mergeMap(() => this.notificationService.getNotifications()
      .pipe(
        tap(notifications => console.log('Effect: Notifications received', notifications)),
        map(notifications => NotificationActions.loadNotificationsSuccess({ notifications })),
        catchError(error => {
          console.error('Effect: Error loading notifications', error);
          return of(NotificationActions.loadNotificationsFailure({ error: error.message }));
        })
      )
    )
  ));
  
  markAsRead$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.markAsRead),
    mergeMap(({ id }) => this.notificationService.markAsRead(id)
      .pipe(
        map(() => NotificationActions.markAsReadSuccess({ id })),
        catchError(error => of(NotificationActions.markAsReadFailure({ error: error.message })))
      )
    )
  ));
  
  markAllAsRead$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.markAllAsRead),
    mergeMap(() => this.notificationService.markAllAsRead()
      .pipe(
        map(() => NotificationActions.markAllAsReadSuccess()),
        catchError(error => of(NotificationActions.markAllAsReadFailure({ error: error.message })))
      )
    )
  ));
  
  deleteNotification$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.deleteNotification),
    mergeMap(({ id }) => this.notificationService.deleteNotification(id)
      .pipe(
        map(() => NotificationActions.deleteNotificationSuccess({ id })),
        catchError(error => of(NotificationActions.deleteNotificationFailure({ error: error.message })))
      )
    )
  ));
  
  // After successful operations, refresh notifications
  refreshAfterSuccess$ = createEffect(() => this.actions$.pipe(
    ofType(
      NotificationActions.markAsReadSuccess,
      NotificationActions.markAllAsReadSuccess,
      NotificationActions.deleteNotificationSuccess
    ),
    map(() => NotificationActions.loadNotifications())
  ));

  constructor(
    @Inject(Actions) private actions$: Actions,
    private notificationService: NotificationService
  ) {}
} 