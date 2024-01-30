import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';
import { AuthService } from '../_services/auth.service';
import { User } from '../../entities/User';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ConfirmDeleteUserComponent } from './confirm-delete-user/confirm-delete-user.component';
import { StorageService } from '../_services/storage.service';

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
    public dialog: MatDialog,
    private storageService: StorageService) { }

  ngOnInit(): void {
    this.storageService.setActiveHistoryTag('ADMINISTRATION', -1);
  }

}
