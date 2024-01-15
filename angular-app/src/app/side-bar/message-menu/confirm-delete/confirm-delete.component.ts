import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { HistoryService } from 'src/app/_services/history.service';
import { HistoryMessage } from 'src/entities/HistoryMessage';

@Component({
  selector: 'app-confirm-delete',
  templateUrl: './confirm-delete.component.html',
  styleUrls: ['./confirm-delete.component.css']
})
export class ConfirmDeleteComponent {
  message: HistoryMessage;
  isFailed = false;
  isProcess = false;

  constructor (
    @Inject(MAT_DIALOG_DATA) public data: { message: HistoryMessage },
    private historyService: HistoryService,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<ConfirmDeleteComponent>
  ) {
    this.message = data.message;
    console.log(this.message);
  }
  deleteMessage() {
    this.isProcess = true;
    this.historyService.deleteMessage(this.message?.messageId || -1).subscribe({
      next: () => {
        this.cleanMessage();
        this.dialogRef.close();
      }, error: (err: any) => {
        this.isProcess = false;
        console.error(err);
        this.isFailed = true;
      }
    });
  }
  cleanMessage() {
    this.message.messageId = -1;
    this.message.messageText = 'DELETED';
    this.message.note = '';
    this.message.deadline = '';
    this.message.isDone = false;
    this.message.isImportant = false;
  }
}
