import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { HistoryMessage } from 'src/entities/HistoryMessage';
import { MessageDialogComponent } from '../message-dialog/message-dialog.component';
import { HistoryService } from 'src/app/_services/history.service';

@Component({
  selector: 'app-message-menu',
  templateUrl: './message-menu.component.html',
  styleUrls: ['./message-menu.component.css']
})
export class MessageMenuComponent {
  message?: HistoryMessage;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { message: HistoryMessage },
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<MessageMenuComponent>,
    private historyService: HistoryService
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
    console.log('.ts: ' + this.message?.messageId);
    this.historyService.changeImportantStatus(this.message?.messageId).subscribe({
      next: data => {
        this.message!.isImportant = !this.message?.isImportant;
      }, error: err => {
        console.log(err);
      }
    })
  }
  changeDoneStatus() {
    console.log('.ts: ' + this.message?.messageId);
    this.historyService.changeDoneStatus(this.message?.messageId).subscribe({
      next: data => {
        this.message!.isDone = !this.message?.isDone;
      }, error: err => {
        console.log(err);
      }
    })
  }

}
