import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UserLogService } from 'src/app/_services/user-log.service';
import { LogEntry } from 'src/entities/LogEntry';

@Component({
  selector: 'app-confirm-delete-entry',
  templateUrl: './confirm-delete-entry.component.html',
  styleUrls: ['./confirm-delete-entry.component.css']
})
export class ConfirmDeleteEntryComponent {
  message: LogEntry;
  isFailed = false;
  isProcess = false;

  constructor (
    @Inject(MAT_DIALOG_DATA) public data: { message: LogEntry },
    private historyService: UserLogService,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<ConfirmDeleteEntryComponent>
  ) {
    this.message = data.message;
  }
  deleteMessage() {
    this.isProcess = true;
    this.historyService.deleteEntry(this.message?.entryId || -1).subscribe({
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
    this.message.entryId = -1;
    this.message.text = 'DELETED';
    this.message.additionalInformation = '';
    this.message.deadline = '';
    this.message.isDone = false;
    this.message.isImportant = false;
  }
}
