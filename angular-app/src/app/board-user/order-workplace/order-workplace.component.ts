import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { timer } from 'rxjs';
import { OrderService } from 'src/app/_services/order.service';
import { Order } from 'src/entities/Order';
import { ItemCalculationComponent } from './item-calculation/item-calculation.component';
import { ConfirmSigningContractComponent } from './confirm-signing-contract/confirm-signing-contract.component';
import { ConfirmPainmentComponent } from './confirm-painment/confirm-painment.component';
import { StorageService } from 'src/app/_services/storage.service';
import { HistoryService } from 'src/app/_services/history.service';

@Component({
  selector: 'app-order-workplace',
  templateUrl: './order-workplace.component.html',
  styleUrls: ['./order-workplace.component.css']
})
export class OrderWorkplaceComponent implements OnInit{
  orderId = -1;
  order: Order = new Order;
  counter = 1;
  orderProgress = '';
  orderInstance: any;
  unableToSignAgreement = false;
  unableToPainment = false;
  
  constructor(
    private router: Router ,
    private route: ActivatedRoute,
    private orderService: OrderService,
    public dialog: MatDialog,
    private storageService: StorageService,
    private historyService: HistoryService
  ) {}
  
    ngOnInit(): void {
      this.route.params.subscribe(params => {
        this.orderId = +params['id'];
      });
      this.orderService.getOrder(this.orderId).subscribe({
        next: data => {
          this.order = data;
          this.calculateOrderProgress();
          if (this.order.resultPrice) {
            this.order.resultPrice = +this.order.resultPrice.toFixed(2);
          }
        }, error: err => {
          console.log(err)
        }
      })
    }
    backTotheClient() {
      this.router.navigate(['/user-board/client-workplace', this.order.clientId]);
    }
    calculateOrderProgress() {
      this.counter = 1;
    if (this.order.wasMeetingInOffice === true) { this.counter++; }
    if (this.order.isCalculationPromised === true) { this.counter++; }
    if (this.order.isCalculationShown !== 'NOT_SHOWN') { this.counter++; }
    if (this.order.isProjectShown !== 'NOT_SHOWN') { this.counter++; }
    if (this.order.isProjectApproved === true) { this.counter++; }
    if (this.order.estimateBudged !== 0) { this.counter++; }
    if (this.order.isMeasurementsTaken === true) { this.counter++; }
    if (this.order.isMeasurementOffered === true) { this.counter++; }
    if (this.order.isAgreementPrepared === true) { this.counter++; }
    if (this.order.resultPrice && this.order.resultPrice > 0) { this.counter++; }
    if (this.order.hasBeenPaid === true) { this.counter++; }
      this.counter = Math.floor( (this.counter/12) *100 );
      this.orderProgress = `${this.counter}%`; 
    }
    showOrderInfo() {
      console.log(this.order)
    }
    delayCalculateProgress() {
      setTimeout(() => {
        this.calculateOrderProgress();
      }, 500);
    }
    openCalculationDialog(): void {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.width = '870px'; 
      dialogConfig.data = { items: this.order.calculations, orderId: this.order.orderId };
      const dialogRef = this.dialog.open(ItemCalculationComponent, dialogConfig);
  
      dialogRef.afterClosed().subscribe(result => {
        this.orderService.getNewCalculations(this.order.orderId || -1).subscribe({
          next: data => {
            this.order.calculations = data.items;
            this.order.resultPrice = data.resultPrice;
            if (this.order.resultPrice) {
              this.order.resultPrice = +this.order.resultPrice.toFixed(2);
            } else { this.order.resultPrice = -1 }
            this.calculateOrderProgress();
          }, error: err => {
            console.log(err)
          }
        })
        this.unableToSignAgreement = false;
      });
    }
    openConfirmSingingDialog(): void {
      if (this.order.calculations && this.order.calculations.length>0 &&
        this.order.resultPrice && this.order.resultPrice>0) 
      {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.data = { 
          order: this.order
        };
        const dialogRef = this.dialog.open(ConfirmSigningContractComponent, dialogConfig);
          
        dialogRef.afterClosed().subscribe(result => {
          this.refreshHistoryMessages();
        });
      } else {
        this.unableToSignAgreement = true;
      }
    }
    openConfirmPaidDialog(): void {
      if (this.order.isAgreementSigned) 
      {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.data = { 
          order: this.order
        };
        const dialogRef = this.dialog.open(ConfirmPainmentComponent, dialogConfig);
          
        dialogRef.afterClosed().subscribe(result => {
          this.unableToSignAgreement = false;
        });
      } else {
        this.unableToPainment = true;
      }
    }
    refreshHistoryMessages() {
      this.historyService.getHistory().subscribe({
        next: data => {
          this.storageService.setHistory(data);
        }, error: err => {
          console.log(err);
        }
      })
    }
  }