import { Component } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientsService } from 'src/app/_services/clients.service';
import { UserLogService } from 'src/app/_services/user-log.service';
import { StorageService } from 'src/app/_services/storage.service';
import { SaveEntryDialogComponent } from 'src/app/side-bar/save-entry-dialog/save-entry-dialog.component';
import { Client } from 'src/entities/Client';
import { LogEntry } from 'src/entities/LogEntry';
import { Order } from 'src/entities/Order';
import { CreateNewOrderComponent } from './create-new-order/create-new-order.component';
import { SentEmailComponent } from 'src/app/sent-email/sent-email.component';

@Component({
  selector: 'app-client-workplace',
  templateUrl: './client-workplace.component.html',
  styleUrls: ['./client-workplace.component.css']
})
export class ClientWorkplaceComponent {
  client: Client = new Client;
  isRequestSent = false;
  errorMessage = '';
  isError = false;
  progress = 0;
  clientId = -1;
  canEdit = false;
  isResultOfSavedShown = false;
  responceMessage = '';
  filteredOrders?: Order[];
  isEmailSent = false;
  isClientSentToBlackList = false;

  constructor(private clientService: ClientsService, 
      public dialog: MatDialog,
      private router: Router ,
      private route: ActivatedRoute,
      private storageService: StorageService,
      private historyService: UserLogService) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.clientId = +params['id'];
    });
    this.clientService.getClientInformarion(this.clientId).subscribe({
      next: data => {
        this.client = data;
        this.filteredOrders = this.client.orders?.sort((a, b) => {
          if (a.dateOfLastChange instanceof Date && b.dateOfLastChange instanceof Date) {
            return a.dateOfLastChange.getTime() - b.dateOfLastChange.getTime();
          }
          return 0;
        });
      }, error: err => {
        console.log(err);
        this.isError = true;
        this.errorMessage = 'An error occurred while loading client information';
      }
    })
  }

  sentClientToBlackList() {
    this.isRequestSent = true;
    this.clientService.sentClientToBlackList(this.client?.clientId || -1).subscribe({
      next: () => {
        this.isClientSentToBlackList = true;
        this.reloadPage(2500);
      },
      error: (err: any) => {
        console.error(err);
        this.isError = true;
        this.errorMessage = 'Error sending Client to blacklist';
        this.isRequestSent = false;
      }
    });
  }
  reloadPage(delay: number): void {
    setTimeout(() => {
      const URL = (this.client.status === 'CLIENT') ? '/user-board/clients' : '/user-board/leads'
      this.router.navigateByUrl(URL);
    }, delay); 
  }
  changeEditably(){
    this.canEdit = !this.canEdit;
  }
  calculateNumberOfOrders(){
    if (this.client.orders){
      return this.client.orders.length;
    } 
    return 0; 
  }
  editClientData(){
    if (this.client.clientId && this.client.fullName && this.client.email && 
      this.client.address && this.client.phoneNumber) {
      this.clientService.editClientData(this.client.clientId, this.client.fullName, 
        this.client.email, this.client.address, this.client.phoneNumber).subscribe({
          next: () => {
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
        this.isEmailSent = false;
      }, 4000);
  }
  goToTheOrder(orderId: number) {
    if (!this.isClientSentToBlackList) {
      this.router.navigate(['/user-board/order-workplace', orderId]);
    }
  }
  createNewMessage() {
    const message = new LogEntry;
    message.entryId = -1;
    message.tagName = 'CLIENT';
    message.tagId = this.clientId;

    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '500px'; 
    dialogConfig.data = { message: message };
    const dialogRef = this.dialog.open(SaveEntryDialogComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(() => {

      this.historyService.getLog().subscribe({
        next: data => {
          this.storageService.setHistory(data);
        }
      })
    });
  }
  createNewOrder() {

    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '600px'; 
    dialogConfig.data = { clientId: this.client.clientId };
    const dialogRef = this.dialog.open(CreateNewOrderComponent, dialogConfig);
 
    dialogRef.afterClosed().subscribe(() => {
    });
  }
  sentEmailToClient() {
    const dialogConfig = new MatDialogConfig();
    const user = this.storageService.getUser();
    const messageTemplate = 'Dear ' + this.client.fullName + 
      '\n\nI am writing about ... ' + 
      '\n\nBest regards\n' + user.username;
    dialogConfig.data = {
      email: this.client.email,
      messageTemplate: messageTemplate,
      tagName: 'CLIENT',
      tagId: this.clientId
    };
    dialogConfig.width = '600px';
    const dialogRef = this.dialog.open(SentEmailComponent, dialogConfig);

    dialogRef.afterClosed().subscribe((result) => {
      if (result && result.isEmailSent) {
        this.isEmailSent = true;
        this.performDelayedHidingAlert();
      }
    });
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
    if (order.isAgreementPrepared === true) { counter++; }
    if (order.resultPrice && order.resultPrice > 0) { counter++; }
    if (order.hasBeenPaid === true) { counter++; }
    counter = Math.floor( (counter/12) *100 );
    return `${counter}%`; 
  }
}

