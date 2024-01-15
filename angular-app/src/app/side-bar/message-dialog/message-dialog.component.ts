import { DatePipe } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HistoryService } from 'src/app/_services/history.service';
import { HistoryMessage } from 'src/entities/HistoryMessage';
import { HistoryTag } from 'src/entities/HistoryTag';

@Component({
  selector: 'app-message-dialog',
  templateUrl: './message-dialog.component.html',
  styleUrls: ['./message-dialog.component.css']
})

export class MessageDialogComponent implements OnInit{
  message?: HistoryMessage;
  historyTags?: HistoryTag[];
  filteredHistoryTags?: HistoryTag[];
  initiallyTagName = '';
  initiallyEntityName?: string;
  selectedDate: Date = new Date();
  isSuccess = false;
  isFailed = false;
  deadlineDate = new Date();

  constructor(
    public dialogRef: MatDialogRef<MessageDialogComponent>,
    private historyService: HistoryService,
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: { message: HistoryMessage }
  ) {
    this.message = this.data.message;
  }

 
  ngOnInit(): void {
    this.historyService.getTagsForNewHistoryMessage().subscribe({
      next: data => {
        this.historyTags = data;
        if (this.message) {
          this.initiallyEntityName = this.historyTags
            ?.filter(tag => tag.entityId == this.message?.tagId)
            .filter(tag => tag.tagName == this.message?.tagName)
            ?.map(tag => tag.entityName)[0];
          this.filteredHistoryTags = this.historyTags?.filter(tag => tag.tagName == this.message?.tagName);
          if (this.message.deadline && this.message.deadline.length>0) {
            const stringDate = this.message.deadline;
            this.deadlineDate = new Date(stringDate);
          }
        }
      }, error(err) {
        console.log('loading history tags error: ' + err);
      }
    });
    if (this.message && this.message.tagName) {
      this.initiallyTagName = this.message?.tagName;
    }
  }

  chooseTagNames(event: Event) {
    const target = event.target as HTMLSelectElement;
    const category = target.value;
    if (this.message) {
      this.message.tagName = category;
      this.message.tagId = -1;
    }
    this.initiallyTagName = ' ';
    this.filteredHistoryTags = this.historyTags?.filter(tag => tag.tagName === category);
  }

  chooseEntity(event: Event) {
    const target = event.target as HTMLSelectElement;
    const entityId = target.value;
    this.initiallyEntityName = '';
    if (this.message) {
      this.message.tagId = parseInt(entityId, 10);
    }
  }

  updateDate(event: MatDatepickerInputEvent<Date>) {
    if (event.value !== null) {
      
      console.log(event.value)
      const pattern = 'yyyy-MM-dd\'T\'HH:mm:ss';
      const formattedDate = this.datePipe.transform(event.value, pattern) || '';
      console.log(formattedDate)
      this.message!.deadline = formattedDate.toString();
    }
  }

  saveMessage() {
    if (this.message) {
      this.historyService.saveNewHistoryMessage(this.message).subscribe({
        next: () => {
          this.isSuccess = true;
          this.delayHidingCloseDialoge()
        }, error:(err) => {
          this.isFailed = true;
          console.log(err);
        }
      })
    };
  }
  delayHidingCloseDialoge() {
    setTimeout(() => {
      this.dialogRef.close();
    }, 2000);
  }
}
