import { Component } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AuthService } from 'src/app/_services/auth.service';
import { UserService } from 'src/app/_services/user.service';
import { User } from 'src/entities/User';
import { ConfirmDeleteUserComponent } from '../confirm-delete-user/confirm-delete-user.component';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent {
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
