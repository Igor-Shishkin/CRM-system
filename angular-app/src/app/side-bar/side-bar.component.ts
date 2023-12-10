import { Component, ViewEncapsulation } from '@angular/core';
import { SharedServiceService } from '../_services/shared.service';
import { HistoryMessage } from '../HistoryMessage';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class SideBarComponent {
 
    isDropdownActionVisible = false;
    isDropdownHistoryVisible = false;
    history?: HistoryMessage[];

  constructor(private sharedService: SharedServiceService,
      private userService: UserService) {
  }

  ngOnInit(){
    this.history = this.sharedService.history;
  }

  toggleActionDropdown() {
    this.isDropdownActionVisible = !this.isDropdownActionVisible;

  }
  toggleHistoryDropdown() {
    this.isDropdownHistoryVisible = !this.isDropdownHistoryVisible;
    this.userService.getHistory().subscribe(
      data => {
        this.sharedService.history = data;
      }, error => {
        console.error(error);
      }
    );
    
    this.history = this.sharedService.history;
    console.log(this.history?.toString)
  }

}
