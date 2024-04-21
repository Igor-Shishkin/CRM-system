import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { LogEntry } from 'src/entities/LogEntry';
import { SaveEntryDialogComponent } from '../save-entry-dialog/save-entry-dialog.component';
import { UserLogService } from 'src/app/_services/user-log.service';
import { ConfirmDeleteEntryComponent } from './confirm-delete-entry/confirm-delete-entry.component';

@Component({
  selector: 'app-log-menu',
  templateUrl: './log-menu.component.html',
  styleUrls: ['./log-menu.component.css']
})
export class LogMenuComponent {
  message?: LogEntry;
  isSuccess = false;
  successMessage = '';
  isFailed = false;
  isProcess = false;
  isDeleted = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { message: LogEntry },
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<LogMenuComponent>,
    private historyService: UserLogService,
    private cdr: ChangeDetectorRef
  ) {
    this.message = data.message;
  }
  editMessage(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '500px'; 
    dialogConfig.data = { message: this.message };
    const dialogRef = this.dialog.open(SaveEntryDialogComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(result => {
      this.dialogRef.close();
    });
  }
  changeImportantStatus() {
    this.isProcess = true;
    this.historyService.changeImportantStatus(this.message?.entryId).subscribe({
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
    this.historyService.changeDoneStatus(this.message?.entryId).subscribe({
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
    const dialogRef = this.dialog.open(ConfirmDeleteEntryComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(result => {
      this.cdr.markForCheck();
      console.log(this.message);
      if (this.message && this.message.entryId === -1) {
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
