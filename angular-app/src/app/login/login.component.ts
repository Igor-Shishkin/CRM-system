import { Component, NgZone, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';
import { Router } from '@angular/router';
import { delay } from 'rxjs';
import { LogEntry } from '../../entities/LogEntry';
import { UserService } from '../_services/user.service';
import { HistoryService } from '../_services/history.service';

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
    private historyService: HistoryService) { }

  ngOnInit(): void {
    this.storageService.isLoggedIn()

  }

  onSubmit(): void {
    const { username, password } = this.form;

    this.authService.login(username, password).subscribe({
      next: data => {
        this.storageService.saveUser(data);
        this.storageService.setLoggedInStatus(true);
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

    this.historyService.getHistory().subscribe({
      next: data => {
        this.updateHistory(data);
        console.log('message from log');
      },
      error: err => {
        console.log(err.toString());
      }
    });
  }
  updateHistory(newHistory: LogEntry[]) {
    this.storageService.setHistory(newHistory);
  }
}
