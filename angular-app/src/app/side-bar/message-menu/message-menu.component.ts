import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { HistoryMessage } from 'src/entities/HistoryMessage';
import { MessageDialogComponent } from '../message-dialog/message-dialog.component';
import { HistoryService } from 'src/app/_services/history.service';
import { ConfirmDeleteComponent } from './confirm-delete/confirm-delete.component';

@Component({
  selector: 'app-message-menu',
  templateUrl: './message-menu.component.html',
  styleUrls: ['./message-menu.component.css']
})
export class MessageMenuComponent {
  message?: HistoryMessage;
  isSuccess = false;
  successMessage = '';
  isFailed = false;
  isProcess = false;
  isDeleted = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { message: HistoryMessage },
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<MessageMenuComponent>,
    private historyService: HistoryService,
    private cdr: ChangeDetectorRef
  ) {
    this.message = data.message;
  }
  editMessage(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '500px'; 
    dialogConfig.data = { message: this.message };
    const dialogRef = this.dialog.open(MessageDialogComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(result => {
      this.dialogRef.close();
    });
  }
  changeImportantStatus() {
    this.isProcess = true;
    this.historyService.changeImportantStatus(this.message?.messageId).subscribe({
      next: () => {
        this.message!.isImportant = !this.message?.isImportant;
        this.isSuccess = true;
        if (this.message?.isImportant) {
          this.successMessage = 'The message is marked as IMPORTANT'
        } else {
          this.successMessage = 'The message is not important any more'
        }
        this.isProcess = false;
      }, error: err => {
        this.isFailed = true;
        console.log(err);
        this.isProcess = false;
      }
    })
    this.delayHidderAlerts();
  }
  changeDoneStatus() {
    this.isProcess = true;
    this.historyService.changeDoneStatus(this.message?.messageId).subscribe({
      next: () => {
        this.message!.isDone = !this.message?.isDone;
        this.isSuccess = true;
        if (this.message?.isDone) {
          this.successMessage = 'The message is marked as DONE'
        } else {
          this.successMessage = 'The message is`t done'
        }
        this.isProcess = false;
      }, error: err => {
        this.isFailed = true;
        console.log(err);
        this.isProcess = false;
      }
    })
    this.delayHidderAlerts();
  }
  deleteMessage() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = { message: this.message };
    dialogConfig.width = '350px'; 
    const dialogRef = this.dialog.open(ConfirmDeleteComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(result => {
      this.cdr.markForCheck();
      console.log(this.message);
      if (this.message && this.message.messageId === -1) {
        this.isDeleted = true;
        this.delayCloserDialogBox();
      }
    });
  }
  delayHidderAlerts() {
    setTimeout(() => {
      this.isSuccess = false;
      this.isFailed = false;
      this.successMessage = '';
    }, 5000)
  }
  delayCloserDialogBox(){
    setTimeout(() => {
      this.dialogRef.close();
    }, 2500)
  }

}
