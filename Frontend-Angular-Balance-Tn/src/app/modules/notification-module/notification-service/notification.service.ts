import { NotificationType } from './../../enum/notification-type.enum';
import { Injectable } from '@angular/core';
import { NotifierService } from 'angular-notifier';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private notifier: NotifierService) {}

  //we pass the notification type to not mispelled the type of the notification
  public notify(type:NotificationType, message: string){
    this.notifier.notify(type,message);
  }
}
