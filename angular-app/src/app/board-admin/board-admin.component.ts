import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';
import { AuthService } from '../_services/auth.service';
import { User } from '../User';

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
    private authService: AuthService) { }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: data => {
        this.users = data;
        this.isLoaded = true;
      },
      error: err => {
        error: (err: any) => {
          console.error(err); // Log the error for debugging
          // Handle error display or any other actions as needed
        }
      }
    });
  }
  deleteUser(userId: number) {
    this.authService.deleteUser(userId).subscribe({
      next: data => {
        this.responseMessage = data;
        window.location.reload();
      }, error: (err: any) => {
        console.error(err);
      } 
    });
  }
}
