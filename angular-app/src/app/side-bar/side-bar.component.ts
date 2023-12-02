import { Component, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class SideBarComponent {

    isDropdownActionVisible = false;
    isDropdownHistoryVisible = false

  toggleActionDropdown() {
    this.isDropdownActionVisible = !this.isDropdownActionVisible;
  }
  toggleHistoryDropdown() {
    this.isDropdownHistoryVisible = !this.isDropdownHistoryVisible;
  }

}
