import { Component, NgZone, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';
import { Router } from '@angular/router';
import { delay } from 'rxjs';
import { SharedServiceService } from '../_services/shared.service';
import { HistoryMessage } from '../HistoryMessage';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  form: any = {
    username: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(private authService: AuthService, 
    private storageService: StorageService,
    private router: Router,
    private zone: NgZone,
    private sharedService: SharedServiceService,
    private userService: UserService) { }

  ngOnInit(): void {
    if (this.storageService.isLoggedIn()) {
      this.isLoggedIn = true;
      this.roles = this.storageService.getUser().roles;
    }
  }

  onSubmit(): void {
    const { username, password } = this.form;

    this.authService.login(username, password).subscribe({
      next: data => {
        this.storageService.saveUser(data);

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.storageService.getUser().roles;
        this.getHistoryForUser();
        this.performDelayedNavigation();
      },
      error: err => {
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    });
  }
  performDelayedNavigation() {
    this.zone.run(() => {
      setTimeout(() => {
        this.router.navigateByUrl('/home');
        console.log('GO TO HOME');
      }, 2000);
    });
  }

  reloadPage(): void {
    window.location.reload();
  }
  getHistoryForUser(){
    this.userService.getHistory().subscribe({
      next: data => {
        this.sharedService.history = data;
      }, error: err => {
        console.log(err.toString);
      }
    });
  }
}
