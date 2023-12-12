import { Component, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { LidsService } from 'src/app/_services/leads.service';

@Component({
  selector: 'app-add-lid',
  templateUrl: './add-lid.component.html',
  styleUrls: ['./add-lid.component.css']
})
export class AddLidComponent {
  errorMessage = '';
  isAdded = false;
  isError = false;

  constructor(private lidService: LidsService, private router: Router, private zone: NgZone){}
  
  save(fullName: HTMLInputElement, address: HTMLInputElement, email: HTMLInputElement, phoneNumber: HTMLInputElement) {
    this.lidService.addLid(fullName.value, address.value, email.value, phoneNumber.value).subscribe({
      next: data => {
        this.isAdded = true;
        this.isError = false;
        this.performDelayedNavigation();
      }, error: (err: any) => {
        this.isError = true;
        this.errorMessage = 'Error sending data. Contact support, they will answer you someday.'
        console.error(err);
      }
    })
  }
  performDelayedNavigation() {
    this.zone.run(() => {
      setTimeout(() => {
        this.router.navigateByUrl('/user/user-lid');
        console.log('GO');
      }, 2000);
    });
  }
}
