import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HistoryMessage } from '../../entities/HistoryMessage';
import { StorageService } from '../_services/storage.service';
import { BehaviorSubject, Subscription, combineLatest } from 'rxjs';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { HistoryService } from '../_services/history.service';
import { MessageMenuComponent } from './message-menu/message-menu.component';
import { HistoryTag } from 'src/entities/HistoryTag';
import { MessageDialogComponent } from './message-dialog/message-dialog.component';

// import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class SideBarComponent implements OnInit{

history$: BehaviorSubject<HistoryMessage[]> = new BehaviorSubject<HistoryMessage[]>([]);
history: HistoryMessage[] = [];
activeHistoryTag = new HistoryTag;
filteredHistory?: HistoryMessage[];
private historySubscription: Subscription;
private activeHistoryTagSubscription: Subscription;
content?: string;
isLoadFailing = false;

isLoggedIn$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
isLoggedIn?: boolean;
private isLoggedInSubscription: Subscription;

  constructor(
      private storageService: StorageService,
      public dialog: MatDialog,
      private historyService: HistoryService) {
        this.historySubscription = this.storageService.history$.subscribe((userHistory: HistoryMessage[]) => {
          this.history = userHistory;
        });
        this.isLoggedInSubscription = this.storageService.isLoggedIn$.subscribe((isLoggedIn: boolean) => {
          this.isLoggedIn = isLoggedIn;
        });
        this.activeHistoryTagSubscription = this.storageService.activeHistoryTag$.subscribe((historyTag: HistoryTag) => {
          this.activeHistoryTag = historyTag;
        });
  }

  ngOnInit(){

    this.storageService.history$.subscribe((history: HistoryMessage[]) => {
      this.history = history;
      this.filteredHistory = this.history
        .sort((a, b) => {
          if (a.dateOfCreation && b.dateOfCreation) {

            const dateA = new Date(a.dateOfCreation);
            const dateB = new Date(b.dateOfCreation);

            if (!isNaN(dateA.getTime()) && !isNaN(dateB.getTime())) {
              return dateB.getTime() - dateA.getTime();
            }
          }
          return 0;
        }
      )});

    if (this.history.length == 0) {
      this.refreshHistory();
    }
  }


  openDialog(messageToEdit: HistoryMessage): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = { message: messageToEdit };
    dialogConfig.width = '350px'; 
    const dialogRef = this.dialog.open(MessageMenuComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(result => {
      this.refreshHistory();
    });
  }
  editMessage(selectedMessage: HistoryMessage): void {
    this.openDialog(selectedMessage);
  }
  
  createNewMessage() {
    const message = new HistoryMessage;
    message.messageId = -1;
    message.tagName = this.history[0].tagName;
    if (this.activeHistoryTag.entityId != -1) {
      message.tagId = this.activeHistoryTag.entityId;
    }

    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '500px'; 
    dialogConfig.data = { message: message };
    const dialogRef = this.dialog.open(MessageDialogComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(() => {
      this.refreshHistory();
    });
  }

  showAllHistory(){
    this.filteredHistory = this.history;
  }
  showActiveClientHistory(){
    console.log(this.activeHistoryTag)
    this.filteredHistory = this.history?.filter(message => message.tagId === this.activeHistoryTag.entityId);
    console.log(this.activeHistoryTag.entityId);
  }
  refreshHistory(){
    this.historyService.getHistory().subscribe({
      next: data => {
        this.updateHistory(data);
        this.isLoadFailing = false;
      },
      error: err => {
        this.isLoadFailing = true;
        console.log(err.toString());
      }
    });
  }
  updateHistory(newHistory: HistoryMessage[]) {
    this.storageService.setHistory(newHistory);
  }
}
