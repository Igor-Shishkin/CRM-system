import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AuthService } from 'src/app/_services/auth.service';
import { User } from 'src/entities/User';

@Component({
  selector: 'app-confirm-deleting',
  templateUrl: './confirm-delete-user.component.html',
  styleUrls: ['./confirm-delete-user.component.css']
})
export class ConfirmDeleteUserComponent {
  isProcess = false;
  isSuccess = false;
  isFailed = false;
  userId = -1;
  isAdminSureInDeleting = false;

  constructor (
    @Inject(MAT_DIALOG_DATA) public data: { userId: number },
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<ConfirmDeleteUserComponent>,
    private authService: AuthService
  ) { 
    this.userId = data.userId;
   }

  deleteUser() {
    this.authService.deleteUser(this.userId).subscribe({
      next: () => {
        this.isSuccess = true;
        this.delayCloserDialog();
      }, error: (err: any) => {
        this.isFailed = true
        console.error(err);
      } 
    });
  }
  delayCloserDialog() {
    setTimeout(() => {
      this.dialogRef.close();
    })
  }
  confirmDeleting() {
    this.isAdminSureInDeleting = true;
  }
}
