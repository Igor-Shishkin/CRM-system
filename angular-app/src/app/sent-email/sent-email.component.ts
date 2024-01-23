import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EmailService } from '../_services/email.service';

@Component({
  selector: 'app-sent-email',
  templateUrl: './sent-email.component.html',
  styleUrls: ['./sent-email.component.css']
})
export class SentEmailComponent {
  emailAddress = '';
  subjectOfEmail = '';
  textOfEmail = '';
  tagName = '';
  tagId = -1;
  form: any = {
    textOfEmail: null,
    subjectOfMAil: null,
  };
  isSuccessRequest = false;
  isFailedRequest = false;
  isRequestInProcess = false;
  

  constructor(
    public dialogRef: MatDialogRef<SentEmailComponent>,
    private emailService: EmailService,
    @Inject(MAT_DIALOG_DATA) public data: { email: string, messageTemplate: string, tagName: string, tagId: number }
    ) {
    this.emailAddress = data.email;
    this.form.textOfEmail = data.messageTemplate;
    this.tagId = data.tagId;
    this.tagName = data.tagName;
  }
  sentEmail() {
    this.isRequestInProcess = true;
    this.emailService.sentEmail(this.emailAddress, this.form.subjectOfMAil, this.form.textOfEmail, this.tagName, this.tagId)
    .subscribe({
      next: () => {
        this.isSuccessRequest = true;
        this.delayHidingCloseDialoge();
      }, error: err => {
        this.isFailedRequest = true;
        this.isRequestInProcess = false;
        console.error(err);
      }
    })
  }

  delayHidingCloseDialoge() { 
    setTimeout(() => {
      this.dialogRef.close({ isEmailSent: true });
    }, 2000);
  }
}
