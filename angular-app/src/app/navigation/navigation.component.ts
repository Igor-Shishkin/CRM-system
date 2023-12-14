import { Component } from '@angular/core';
import { StorageService } from '../_services/storage.service';
import { AuthService } from '../_services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {
  private roles: string[] = [];
  isLoggedIn = this.storageService.isLoggedIn();
  showAdminBoard = false;
  showModeratorBoard = false;
  username?: string;
  isDropdownActionVisible = false;
  private isLoggedInSubscription: Subscription;

  constructor(private storageService: StorageService, private authService: AuthService, private router: Router) {
    this.isLoggedInSubscription = this.storageService.isLoggedIn$.subscribe((isLoggedIn: boolean) => {
      this.isLoggedIn = isLoggedIn;
    });
   }

  ngOnInit(): void {

    this.storageService.isLoggedIn$.subscribe((isLoggedIn: boolean) => {
      this.isLoggedIn = isLoggedIn;
    });

    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.roles = user.roles;

      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');

      this.username = user.username;
    }
  }
  ngOnDestroy(): void {
    this.isLoggedInSubscription.unsubscribe();
  }

  login(){
    this.storageService.clean();
    window.sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.isLoggedIn = false;
    this.showAdminBoard = false;
    this.showModeratorBoard = false;
    this.storageService.clean();
    window.sessionStorage.clear();
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
    this.authService.logout().subscribe({
      next: res => {
        console.log(res);
      },
      error: err => {
        console.log(err);
      }
    });
  }

}
