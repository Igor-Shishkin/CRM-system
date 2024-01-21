import { Component, NgZone } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/_services/auth.service';
import { ClientsService } from 'src/app/_services/clients.service';
import { StorageService } from 'src/app/_services/storage.service';

@Component({
  selector: 'app-add-lead',
  templateUrl: './add-lead.component.html',
  styleUrls: ['./add-lead.component.css']
})
export class AddLeadComponent{
  errorMessage = '';
  isAdded = false;
  isError = false;
  isRequestProcess = false;

  constructor(
    private clientsService: ClientsService, 
    private router: Router, private zone: NgZone,
    private storageService: StorageService
    ) {}

  save(fullName: HTMLInputElement, address: HTMLInputElement, email: HTMLInputElement, phoneNumber: HTMLInputElement) {
    this.isRequestProcess = true;
    this.clientsService.addLead(fullName.value, address.value, email.value, phoneNumber.value).subscribe({
      next: data => { 
        this.isAdded = true;
        this.isError = false;
        this.performDelayedNavigation(data);
      }, error: (err: any) => {
        this.isError = true;
        this.errorMessage = 'Error sending data. Contact support, they will answer you someday.'
        console.error(err);
        this.isRequestProcess = false;
      }
    })
  }
  performDelayedNavigation(clientId: number) {
      setTimeout(() => {
        this.storageService.setActiveHistoryTag('CLIENT', clientId);
        this.router.navigate(['/user-board/client-workplace', clientId]);
      }, 2000);
  }
}
 