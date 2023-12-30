import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { timer } from 'rxjs';
import { OrderService } from 'src/app/_services/order.service';
import { Order } from 'src/entities/Order';
import { ItemCalculationComponent } from './item-calculation/item-calculation.component';

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
  
  constructor(
    private router: Router ,
    private route: ActivatedRoute,
    private orderService: OrderService,
    public dialog: MatDialog
  ) {}
  
    ngOnInit(): void {
      this.route.params.subscribe(params => {
        this.orderId = +params['id'];
      });
      this.orderService.getOrder(this.orderId).subscribe({
        next: data => {
          this.order = data;
          this.calculateOrderProgress();
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
    if (this.order.hasAgreementPrepared === true) { this.counter++; }
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
      dialogConfig.width = '700px'; 
      dialogConfig.data = { items: this.order.calculations };
      const dialogRef = this.dialog.open(ItemCalculationComponent, dialogConfig);
  
      dialogRef.afterClosed().subscribe(result => {
        
      });
    }
  }