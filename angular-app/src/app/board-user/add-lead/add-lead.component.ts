import { Component, NgZone } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/_services/auth.service';
import { ClientsService } from 'src/app/_services/clients.service';

@Component({
  selector: 'app-add-lead',
  templateUrl: './add-lead.component.html',
  styleUrls: ['./add-lead.component.css']
})
export class AddLeadComponent{
  errorMessage = '';
  isAdded = false;
  isError = false;

  constructor(
    private clientsService: ClientsService, 
    private router: Router, private zone: NgZone,
    private authService: AuthService
    ){}

    // canActivate(
    //   next: ActivatedRouteSnapshot,
    //   state?: RouterStateSnapshot 
    // ): boolean {
    //   if (this.authService.isAuthenticatedAsUser()) {
    //     console.log('authorization confirmed')
    //     return true;
    //   } else {
    //     console.log('Please login')
    //     this.router.navigate(['/login']);
    //     return false;
    //   }
    // }

  save(fullName: HTMLInputElement, address: HTMLInputElement, email: HTMLInputElement, phoneNumber: HTMLInputElement) {
    this.clientsService.addLead(fullName.value, address.value, email.value, phoneNumber.value).subscribe({
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
 