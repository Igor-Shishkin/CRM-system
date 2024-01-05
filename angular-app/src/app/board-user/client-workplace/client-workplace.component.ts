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
  client: Client = new Client;
  isRequestSent = false;
  isSuccessLoad = false;
  responseMessage = '';
  errorMessage = '';
  isError = false;
  progress = 0;
  clientId = -1;
  canEdit = false;
  isResultOfSavedShown = false;
  responceMessage = '';
  filteredOrders?: Order[];

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
        this.filteredOrders = this.client.orders?.sort((a, b) => {
          if (a.dateOfLastChange && b.dateOfLastChange) {
            return a.dateOfLastChange.getTime()-a.dateOfLastChange.getTime();
          }
          return 0;
        })
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
    if (order.wasMeetingInOffice === true) { counter++; }
    if (order.isCalculationPromised === true) { counter++; }
    if (order.isCalculationShown !== 'NOT_SHOWN') { counter++; }
    if (order.isProjectShown !== 'NOT_SHOWN') { counter++; }
    if (order.isProjectApproved === true) { counter++; }
    if (order.estimateBudged !== 0) { counter++; }
    if (order.isMeasurementsTaken === true) { counter++; }
    if (order.isMeasurementOffered === true) { counter++; }
    if (order.hasAgreementPrepared === true) { counter++; }
    if (order.resultPrice && order.resultPrice > 0) { counter++; }
    if (order.hasBeenPaid === true) { counter++; }
    counter = Math.floor( (counter/12) *100 );
    return `${counter}%`; 
  }
  changeEditably(){
    this.canEdit = !this.canEdit;
  }
  calculateNumberOfOrdders(){
    if (this.client.orders){
      return this.client.orders.length;
    } 
    return 0;
  }
  editClientData(){
    if (this.client.id && this.client.fullName && this.client.email && 
      this.client.address && this.client.phoneNumber) {
      this.clientService.editClientData(this.client.id, this.client.fullName, 
        this.client.email, this.client.address, this.client.phoneNumber).subscribe({
          next: data => {
            this.isResultOfSavedShown = true;
            this.responceMessage = "Changes are sucsessfully saved";
            this.performDelayedHidingAlert();
          }, error: err => {
            this.isResultOfSavedShown = true;
            this.responceMessage = 'Unfortunately, an error occurred while saving data. Please try again later.';
            this.performDelayedHidingAlert();
          }
        })
    }
  }
  performDelayedHidingAlert() {
      setTimeout(() => {
        this.isResultOfSavedShown = false;
        this.responceMessage = '';
      }, 4000);
  }
  goToTheOrder(orderId: number) {
    this.router.navigate(['/user-board/order-workplace', orderId]);
  }
}

