import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UserService } from '../_services/user.service';
import { StorageService } from '../_services/storage.service';



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit{


  isLoggedIn?: boolean;


  constructor(private userService: UserService, private storageService: StorageService) { }


  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
  }

}
