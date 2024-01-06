import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { OrderService } from 'src/app/_services/order.service';
import { Order } from 'src/entities/Order';

@Component({
  selector: 'app-confirm-painment',
  templateUrl: './confirm-painment.component.html',
  styleUrls: ['./confirm-painment.component.css']
})
export class ConfirmPainmentComponent {

  isPaid = false;
  orderId = -1;
  isSuccess = false;
  successMessage = '';
  isFailed = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { order: Order },
    private orderService: OrderService,
    public dialogRef: MatDialogRef<ConfirmPainmentComponent>
  ) {
    this.isPaid = data.order.hasBeenPaid || false;
    this.orderId = data.order.orderId || -1;
  }

  confirmPayment(){
    this.orderService.confirmPayment(this.orderId).subscribe({
      next: () => {
        this.data.order.hasBeenPaid = true;
        this.isSuccess = true;
        this.successMessage = 'You have confirmed payment by client. Congratulations on the successful completion of your order!';
        this.delayHidingCloseDialoge();
      }, error: err => {
        this.isFailed = true;
        console.log(err);
      }
    })
  }
  cancelPayment(){
    this.orderService.cancelPayment(this.orderId).subscribe({
      next: () => {
        this.data.order.hasBeenPaid = false;
        this.isSuccess = true;
        this.successMessage = 'You have canceled the payment';
        this.delayHidingCloseDialoge();
      }, error: err => {
        this.isFailed = true;
        console.log(err);
      }
    })
  }
  delayHidingCloseDialoge() {
    setTimeout(() => {
      this.dialogRef.close();
    }, 2000);
  }
}