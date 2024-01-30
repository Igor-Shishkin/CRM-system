import { Component } from '@angular/core';
import { StorageService } from '../_services/storage.service';
import { AuthService } from '../_services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { HistoryMessage } from 'src/entities/HistoryMessage';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {
  private roles: string[] = [];
  isAdminRole = false;
  isUserRole = false;
  username?: string;
  isDropdownActionVisible = false;


  private isLoggedInSubscription: Subscription;
  isLoggedIn$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isLoggedIn?: boolean;

  constructor(private storageService: StorageService, private authService: AuthService, private router: Router) {
    this.isLoggedInSubscription = this.storageService.isLoggedIn$.subscribe((isLoggedIn: boolean) => {
      this.isLoggedIn = isLoggedIn;
    });
   }

  ngOnInit(): void {

    this.storageService.isLoggedIn$.subscribe((isLoggedIn: boolean) => {
      this.isLoggedIn = isLoggedIn;

      if (this.isLoggedIn) {
        const user = this.storageService.getUser();
        this.roles = user.roles;
  
        this.isAdminRole = this.roles.includes('ROLE_ADMIN');
        this.isUserRole = this.roles.includes('ROLE_USER');
  
        this.username = user.username;
      }
    });

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
    this.isAdminRole = false;
    this.isUserRole = false;
    this.storageService.clean();
    this.storageService.setHistory([new HistoryMessage()]);
    this.storageService.setLoggedInStatus(false);
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
