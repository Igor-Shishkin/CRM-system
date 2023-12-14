import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientsService } from 'src/app/_services/clients.service';
import { SharedServiceService } from 'src/app/_services/shared.service';
import { Client } from 'src/entities/Client';
import { Order } from 'src/entities/Order';

@Component({
  selector: 'app-client-workplace',
  templateUrl: './client-workplace.component.html',
  styleUrls: ['./client-workplace.component.css']
})
export class ClientWorkplaceComponent {
  client?: Client;
  isRequestSent = false;
  isSuccessLoad = false;
  responseMessage = '';
  errorMessage = '';
  isError = false;
  progress = 0;
  clientId = -1;

  constructor(private clientService: ClientsService, 
      private router: Router ,
      private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.clientId = +params['id'];
    });
    this.clientService.getClientInformarion(this.clientId).subscribe({
      next: data => {
        this.client = data;
      }, error: err => {
        console.log(err);
        this.isError = true;
        this.errorMessage = 'An error occurred while loading client information';
      }
    })
  }

  sentClientToBlackList() {
    this.isRequestSent = true;
    this.clientService.sentClientToBlackList(this.client?.id || -1).subscribe({
      next: (data: any) => {
        this.responseMessage = data;
        this.reloadPage(1500);
      },
      error: (err: any) => {
        console.error(err);
        this.isError = true;
        this.errorMessage = 'Error deleting Client';
        this.isRequestSent = false;
      }
    });
  }
  reloadPage(delay: number): void {
    setTimeout(() => {
      this.router.navigateByUrl('/home');
    }, delay); 
  }

  calculateOrderProgress(order: Order): string{
    let counter = 1;
    if (order.isCalculationPromised) { counter++; }
    if (order.isCalculationShown != 'NOT_SHOWN') { counter++; }
    if (order.isProjectShown != 'NOT_SHOWN') { counter++; }
    if (order.isProjectApproved) { counter++; }
    if (order.estimateBudged != 0) { counter++; }
    if (order.measurementsTaken) { counter++; }
    if (order.measurementOffered) { counter++; }
    if (order.hasAgreementPrepared) { counter++; }
    counter = Math.floor( (counter/9) *100 );
    return `${counter}%`;
    
  }
}

