import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { UserLogService } from 'src/app/_services/user-log.service';
import { OrderService } from 'src/app/_services/order.service';
import { StorageService } from 'src/app/_services/storage.service';

@Component({
  selector: 'app-create-new-order',
  templateUrl: './create-new-order.component.html',
  styleUrls: ['./create-new-order.component.css']
})
export class CreateNewOrderComponent{
  clientId = -1;
  realNeed = '';
  estimateBudget = 500;

  isSuccess = false;
  isFail = false;
  isREquestProcess = false;

  constructor(
    public dialogRef: MatDialogRef<CreateNewOrderComponent>,
    private orderService: OrderService,
    private router: Router,
    private historyService: UserLogService,
    private storageService: StorageService,
    @Inject(MAT_DIALOG_DATA) public data: { clientId: number }
  ) {
    this.clientId = data.clientId;
  }

  createNewOrder() {
    this.isREquestProcess = true;
    this.orderService.createNewOrder(this.clientId, this.realNeed, this.estimateBudget).subscribe({
      next: data => {
        this.isSuccess = true;
        this.refreshHistoryMessages();
        this.delayGoToNewOrder(data);
      }, error: err => {
        console.error
        this.isFail = true;
        this.isREquestProcess = false;
      }
    })
  }
  refreshHistoryMessages() {
    this.historyService.getLog().subscribe({
      next: data => {
        this.storageService.setHistory(data);
      }, error: err => {
        console.log(err);
      }
    })
  }

  delayGoToNewOrder(newOrderId: number) {
    setTimeout(() => {
      this.router.navigate(['/user-board/order-workplace', newOrderId]);
      this.dialogRef.close();
    }, 1500);
  }
}