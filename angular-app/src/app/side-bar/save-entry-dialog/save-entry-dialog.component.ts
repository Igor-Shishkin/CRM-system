import { DatePipe } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HistoryService } from 'src/app/_services/history.service';
import { LogEntry } from 'src/entities/LogEntry';
import { LogTag } from 'src/entities/LogTag';

@Component({
  selector: 'app-entry-dialog',
  templateUrl: './save-entry-dialog.component.html',
  styleUrls: ['./save-entry-dialog.component.css']
})

export class SaveEntryDialogComponent implements OnInit{
  entry?: LogEntry;
  historyTags?: LogTag[];
  filteredHistoryTags?: LogTag[];
  initiallyTagName = '';
  initiallyEntityName?: string;
  selectedDate: Date = new Date();
  isSuccess = false;
  isFailed = false;
  deadlineDate = new Date();

  constructor(
    public dialogRef: MatDialogRef<SaveEntryDialogComponent>,
    private historyService: HistoryService,
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: { message: LogEntry }
  ) {
    this.entry = this.data.message;
  }

 
  ngOnInit(): void {
    this.historyService.getTagsForNewHistoryMessage().subscribe({
      next: data => {
        this.historyTags = data;
        if (this.entry) {
          this.initiallyEntityName = this.historyTags
            ?.filter(tag => tag.entityId == this.entry?.tagId)
            .filter(tag => tag.tagName == this.entry?.tagName)
            ?.map(tag => tag.entityName)[0];
          this.filteredHistoryTags = this.historyTags?.filter(tag => tag.tagName == this.entry?.tagName);
          if (this.entry.deadline && this.entry.deadline.length>0) {
            const stringDate = this.entry.deadline;
            this.deadlineDate = new Date(stringDate);
          }
        }
      }, error(err) {
        console.log('loading history tags error: ' + err);
      }
    });
    if (this.entry && this.entry.tagName) {
      this.initiallyTagName = this.entry?.tagName;
    }
  }

  chooseTagNames(event: Event) {
    const target = event.target as HTMLSelectElement;
    const category = target.value;
    if (this.entry) {
      this.entry.tagName = category;
      this.entry.tagId = -1;
    }
    this.initiallyTagName = ' ';
    this.filteredHistoryTags = this.historyTags?.filter(tag => tag.tagName === category);
  }

  chooseEntity(event: Event) {
    const target = event.target as HTMLSelectElement;
    const entityId = target.value;
    this.initiallyEntityName = '';
    if (this.entry) {
      this.entry.tagId = parseInt(entityId, 10);
    }
  }

  updateDate(event: MatDatepickerInputEvent<Date>) {
    if (event.value !== null) {
      
      console.log(event.value)
      const pattern = 'yyyy-MM-dd\'T\'HH:mm:ss';
      const formattedDate = this.datePipe.transform(event.value, pattern) || '';
      console.log(formattedDate)
      this.entry!.deadline = formattedDate.toString();
    }
  }

  saveMessage() {
    if (this.entry) {
      this.historyService.saveNewHistoryMessage(this.entry).subscribe({
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
