import { Component, OnInit,  } from '@angular/core';
import { StorageService } from '../_services/storage.service';



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit{
  isAdminRole = false;
  isUserRole = false;
  private roles: string[] = [];
  isLoggedIn?: boolean;

  constructor(private storageService: StorageService) {
    this.storageService.setActiveHistoryTag('', -1);
   }

  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.roles = user.roles;

      this.isAdminRole = this.roles.includes('ROLE_ADMIN');
      this.isUserRole = this.roles.includes('ROLE_USER');
    }

    this.storageService.setActiveHistoryTag('EMPTY', -1);
  }
  setTagForAdministration() {
    this.storageService.setActiveHistoryTag('ADMINISTRATION', -1);
  }
  setTagForClient() {
    this.storageService.setActiveHistoryTag('CLIENT', -1);
  }

}
