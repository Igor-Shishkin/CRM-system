import { Component, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { ClientsService } from 'src/app/_services/clients.service';

@Component({
  selector: 'app-add-lead',
  templateUrl: './add-lead.component.html',
  styleUrls: ['./add-lead.component.css']
})
export class AddLeadComponent {
  errorMessage = '';
  isAdded = false;
  isError = false;

  constructor(private clientsService: ClientsService, private router: Router, private zone: NgZone){}
  
  save(fullName: HTMLInputElement, address: HTMLInputElement, email: HTMLInputElement, phoneNumber: HTMLInputElement) {
    this.clientsService.addLid(fullName.value, address.value, email.value, phoneNumber.value).subscribe({
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
 