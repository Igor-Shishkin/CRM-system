import { AfterViewChecked, AfterViewInit, Component, ElementRef, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { SharedServiceService } from '../_services/shared.service';
import { HistoryMessage } from '../../entities/HistoryMessage';
import { UserService } from '../_services/user.service';
import { StorageService } from '../_services/storage.service';
import { BehaviorSubject, Subscription, combineLatest } from 'rxjs';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MessageDialogComponent } from './message-dialog/message-dialog.component';
import { HistoryService } from '../_services/history.service';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../_services/auth.service';

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
filteredHistory?: HistoryMessage[];
activeClientId = -1;
private historySubscription: Subscription;
private activeClientIdSubscription: Subscription;
content?: string;

  constructor(
      private storageService: StorageService,
      public dialog: MatDialog,
      private historyService: HistoryService,) {
        this.historySubscription = this.storageService.history$.subscribe((userHistory: HistoryMessage[]) => {
          this.history = userHistory;
        });
        this.activeClientIdSubscription = this.storageService.activeClientId$.subscribe((activeClientId: number) => {
          this.activeClientId = activeClientId;
        });
  }

  ngOnInit(){

    this.storageService.history$.subscribe((history: HistoryMessage[]) => {
      this.history = history;
      this.filteredHistory = this.history.sort((a, b) => {
        if (a.dateOfCreation && b.dateOfCreation) {

          const dateA = new Date(a.dateOfCreation);
          const dateB = new Date(b.dateOfCreation);
          
          if (!isNaN(dateA.getTime()) && !isNaN(dateB.getTime())) {
            return dateB.getTime() - dateA.getTime();
          }
        }
        return 0;
      })});

    if (this.history.length == 0) {
      this.refreshHistory();
    }
  }


  openDialog(messageToEdit: HistoryMessage): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '400px'; 
    dialogConfig.data = { message: messageToEdit };
    const dialogRef = this.dialog.open(MessageDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      this.refreshHistory();
    });
  }
  editMessage(selectedMessage: HistoryMessage): void {
    this.openDialog(selectedMessage);
  }
  createNewMessage() {
    const message = new HistoryMessage;
    message.tagName = this.history[0].tagName;
    if (this.activeClientId != -1) {
      message.tagId = this.activeClientId;
    }
    this.openDialog(message);
  }

  showAllHistory(){
    this.filteredHistory = this.history;
  }
  showActiveClientHistory(){
    this.filteredHistory = this.history?.filter(message => message.tagId === this.activeClientId);
    console.log(this.activeClientId);
  }
  refreshHistory(){
    this.historyService.getHistory().subscribe({
      next: data => {
        this.updateHistory(data);
        console.log('message from log');
      },
      error: err => {
        console.log(err.toString());
      }
    });
  }
  updateHistory(newHistory: HistoryMessage[]) {
    this.storageService.setHistory(newHistory);
  }
}
