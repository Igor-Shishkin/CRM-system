import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/_services/auth.service';
import { StorageService } from 'src/app/_services/storage.service';

@Component({
  selector: 'app-not-permission',
  templateUrl: './not-permission.component.html',
  styleUrls: ['./not-permission.component.css']
})
export class NotPermissionComponent implements AfterViewInit{

  constructor(private authService: AuthService,
          private storageService: StorageService,
          private router: Router) {}

ngAfterViewInit() {

    console.log('in onInit')

  const USER_KEY = 'auth-user';

  this.authService.isAuthenticated().subscribe({
    next: data => {
      if (data) {
        console.log('true')
        this.storageService.setLoggedInStatus(true);
        this.performDelayedNavigationToHome();
        return true;
      } else {
        console.log('You do not have permissions');
        window.sessionStorage.removeItem(USER_KEY);
        this.storageService.setLoggedInStatus(false);
        this.performDelayedNavigationToLogin();
        return false;
      }
    }, error: err => {
      console.log('can not connect with server: ' + err.toString);
      this.storageService.setLoggedInStatus(false);
      window.sessionStorage.removeItem(USER_KEY);
      this.performDelayedNavigationToLogin();
      return false;
    }
  })
  }

  performDelayedNavigationToHome() {
      setTimeout(() => {
        this.router.navigateByUrl('/home');
        console.log('GO TO HOME');
      }, 2000);
  }
  performDelayedNavigationToLogin() {
    setTimeout(() => {
      this.router.navigateByUrl('/login');
      console.log('GO TO LOGIN');
    }, 2000);
  }


}
