import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';
import { AuthService } from '../_services/auth.service';
import { User } from '../../entities/User';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ConfirmDeleteUserComponent } from './confirm-delete-user/confirm-delete-user.component';

@Component({
  selector: 'app-board-admin',
  templateUrl: './board-admin.component.html',
  styleUrls: ['./board-admin.component.css']
})
export class BoardAdminComponent implements OnInit {
  users?: User[];
  isLoaded = false;
  responseMessage = '';
  errorMessage = '';
  isError = false;

  constructor(private userService: UserService,
    private authService: AuthService,
    public dialog: MatDialog) { }

  ngOnInit(): void {
    this.getListOfUsers();
  }
  deleteUser(userId: number) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = { userId: userId };
    dialogConfig.width = '450px'; 
    const dialogRef = this.dialog.open(ConfirmDeleteUserComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(() => {
      this.getListOfUsers()
    });
  }
  getListOfUsers() {
    this.userService.getAllUsers().subscribe({
      next: data => {
        this.users = data;
        this.isLoaded = true;
      },
      error: err => {
        error: (err: any) => {
          console.error(err); 
        }
      }
    });
  }
}
