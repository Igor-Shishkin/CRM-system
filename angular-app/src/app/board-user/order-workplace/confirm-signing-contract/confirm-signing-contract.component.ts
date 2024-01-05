import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { OrderService } from 'src/app/_services/order.service';

@Component({
  selector: 'app-confirm-signing-contract',
  templateUrl: './confirm-signing-contract.component.html',
  styleUrls: ['./confirm-signing-contract.component.css']
})
export class ConfirmSigningContractComponent {

  isAgreementSigned = false;
  orderId = -1;
  isSuccess = false;
  successMessage = '';
  isFailed = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { isAgreementSigned: boolean, orderId: number },
    private orderService: OrderService,
    public dialogRef: MatDialogRef<ConfirmSigningContractComponent>
  ) {
    this.isAgreementSigned = data.isAgreementSigned;
    this.orderId = data.orderId;
    console.log(this.isAgreementSigned)
  }

  signAgreement(){
    this.orderService.saveAgreementStatus(true, this.orderId).subscribe({
      next: () => {
        this.isSuccess = true;
        this.successMessage = 'You have signed a contract, congratulations!';
        this.delayHidingCloseDialoge();
      }, error: err => {
        this.isFailed = true;
        console.log(err);
      }
    })
  }
  cancelAgreement(){
    this.orderService.saveAgreementStatus(false, this.orderId).subscribe({
      next: () => {
        this.successMessage = 'You have canceled the agreement';
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
