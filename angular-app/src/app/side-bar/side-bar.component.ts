import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HistoryMessage } from '../../entities/HistoryMessage';
import { StorageService } from '../_services/storage.service';
import { BehaviorSubject, Subscription, combineLatest } from 'rxjs';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { HistoryService } from '../_services/history.service';
import { MessageMenuComponent } from './message-menu/message-menu.component';
import { HistoryTag } from 'src/entities/HistoryTag';
import { SaveMessageDialogComponent } from './save-message-dialog/save-message-dialog.component';
import { HistoryFilterParameters } from 'src/entities/HistoryFilterParameters';

// import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class SideBarComponent implements OnInit{

filteredHistory?: HistoryMessage[];

history: HistoryMessage[] = [];
history$: BehaviorSubject<HistoryMessage[]> = new BehaviorSubject<HistoryMessage[]>([]);
private historySubscription: Subscription;

activeHistoryTag = new HistoryTag;
private activeHistoryTagSubscription: Subscription;

content?: string;
isLoadFailing = false;

filterParameters: HistoryFilterParameters = new HistoryFilterParameters;

isLoggedIn$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
isLoggedIn?: boolean;
private isLoggedInSubscription: Subscription;

  constructor(
      private storageService: StorageService,
      public dialog: MatDialog,
      private historyService: HistoryService) {
        this.historySubscription = this.storageService.history$.subscribe((userHistory: HistoryMessage[]) => {
          this.history = userHistory;
          this.delayFilterHistory();
        });
        this.isLoggedInSubscription = this.storageService.isLoggedIn$.subscribe((isLoggedIn: boolean) => {
          this.isLoggedIn = isLoggedIn;
        });
        this.activeHistoryTagSubscription = this.storageService.activeHistoryTag$.subscribe((historyTag: HistoryTag) => {
          this.activeHistoryTag = historyTag;

          this.filterParameters.byCategory = (this.activeHistoryTag.tagName !== 'EMPTY');
          this.filterParameters.byId = (this.activeHistoryTag.entityId ! > 0);

          this.filterMethodHistory();
          this.sortFilteredHistory();
        });
  }

  ngOnInit(){

    this.storageService.history$.subscribe((history: HistoryMessage[]) => {
      this.history = history;
      this.filteredHistory = history;
    });

    if (this.history.length == 0) {
      this.refreshHistory();
    }
    this.filterMethodHistory();
    this.sortFilteredHistory(); 
  }

  filterMethodHistory() {
    this.filteredHistory = this.history;
    if (this.filterParameters.byId && this.activeHistoryTag.entityId && this.activeHistoryTag.entityId > 0) {
      this.filterHistoryByActiveId();
    } else if (this.filterParameters.byCategory && this.activeHistoryTag.tagName && this.activeHistoryTag.tagName.length>0) {
      this.filterHistoryByCategories();
    };
    if (this.filterParameters.byImportant) {
      this.filterHistoryByImportant();
    };
    if (this.filterParameters.byUndone) {
      this.filterHistoryByUndone();
    };
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
    message.tagName = this.activeHistoryTag.tagName;
    if (this.activeHistoryTag.entityId != -1) {
      message.tagId = this.activeHistoryTag.entityId;
    }

    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '500px'; 
    dialogConfig.data = { message: message };
    const dialogRef = this.dialog.open(SaveMessageDialogComponent, dialogConfig);
 
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
        this.history = data;
        console.log(this.filterParameters)
        this.isLoadFailing = false;
        this.filterMethodHistory();
        this.sortFilteredHistory();
      },
      error: err => {
        this.isLoadFailing = true;
        console.log(err.toString());
      }
    });
  }
  filterHistoryByCategories() {
    if (this.filteredHistory) 
    this.filteredHistory = this.filteredHistory
      .filter(message => message.tagName === this.activeHistoryTag.tagName);
  }
  filterHistoryByActiveId() {
    if (this.filteredHistory) 
    this.filteredHistory = this.filteredHistory
      .filter(message => message.tagId === this.activeHistoryTag.entityId);
  }
  filterHistoryByImportant() {
    if (this.filteredHistory) 
    this.filteredHistory = this.filteredHistory
      .filter(message => message.isImportant);
  }
  filterHistoryByUndone() {
    if (this.filteredHistory) 
    this.filteredHistory = this.filteredHistory
      .filter(message => !message.isDone);
  }
  sortFilteredHistory() {
    if (this.filteredHistory) {
      this.filteredHistory = this.filteredHistory
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
    )
    }
  }
  delayFilterHistory() {
    setTimeout(() => {
      this.filterMethodHistory();
      this.sortFilteredHistory();
    }, 100)
  }
  cancelFilters() {
    this.filterParameters.byCategory = false;
    this.filterParameters.byId = false;
    this.filterParameters.byImportant = false;
    this.filterParameters.byUndone = false;

    this.filteredHistory = this.history;
  }
}
