import { Component, Inject, numberAttribute } from '@angular/core';
import { FormControl, ValidatorFn, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { timer } from 'rxjs';
import { AdditionalPurchasesService } from 'src/app/_services/calculation-items.service';
import { ItemForAdditionalPurchases } from 'src/entities/ItemForAdditionalPurchases';

@Component({
  selector: 'app-item-for-additional-purchases',
  templateUrl: './item-for-additional-purchases.component.html',
  styleUrls: ['./item-for-additional-purchases.component.css']
})
export class ItemForAdditionalPurchasesComponent {


  items: ItemForAdditionalPurchases[];
  resultPrice = 0;
  orderId: number;
  isItemsSaved = false;
  isSavingWasFail = false;
  errorMessage = '';
  itemsForSaving: ItemForAdditionalPurchases[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { items: ItemForAdditionalPurchases[], orderId: number },
    private additionalPurchasesService: AdditionalPurchasesService,
    public dialogRef: MatDialogRef<ItemForAdditionalPurchasesComponent>
  ) {
    this.items = this.data.items;
    this.orderId = this.data.orderId;
    this.calculateResultPrice();
  }

  calculateResultPrice() {
    this.resultPrice = 0;
    for (let item of this.items) {
      if (
          ((!item.unitPrice || item.unitPrice < 0) || 
          (!item.quantity || item.quantity < 0)) && 
          item.itemName && item.itemName.length > 0
        ) {
        this.resultPrice = -1;
        return;
      }
      this.resultPrice += item.totalPrice || 0;
    }
    this.resultPrice = +this.resultPrice.toFixed(2);
  }
  calcilateTotalPriceForItem(item: ItemForAdditionalPurchases) {
    if (item.unitPrice && item.quantity) {
      item.totalPrice = item.unitPrice * item.quantity * 1.1;
      item.totalPrice = +item.totalPrice.toFixed(2);
      this.calculateResultPrice();
    } else {
      this.resultPrice = -1;
    }
    this.isEmptyThing(item);
  }
  isNonPositiveNumber(price: number) {
    return price <= 0;
  }
  isPositiveInteger(quantity: number) {
    return quantity > 0 && Number.isInteger(quantity);
  }
  addItem() {
    this.items.push(new ItemForAdditionalPurchases);
  }
  isEmptyThing(item: ItemForAdditionalPurchases) {
    if (item.itemName && item.itemName.trim().length >0) {
      if (
        (!item.totalPrice || item.totalPrice < 0) && 
        (!item.quantity || item.quantity < 0)
      ) {
        this.resultPrice = -1;
      }
      return false;
    } else {
      return true;
    }
  }
  deleteItem(item: ItemForAdditionalPurchases) {
    if (this.resultPrice != -1 && item.totalPrice) {
      this.resultPrice = this.resultPrice - item.totalPrice;
      this.resultPrice = +this.resultPrice.toFixed(2);
    }
    this.items = this.items.filter((i: ItemForAdditionalPurchases) => i !== item);
  }
  saveItems() {

    if (this.checkItems()) {
      this.additionalPurchasesService.saveItemsForPurchases(this.itemsForSaving, this.orderId).subscribe({
        next: () => {
          this.isItemsSaved = true;
          this.delayHidingCloseDialoge();
        }, error: () => {
          this.isSavingWasFail = true;
          this.delayHidingFailingMessages();
        }
      })
    } 
  }
  checkItems() {
      for (let item of this.items) {
        if (
          item.itemName && item.itemName.trim().length > 0
        ) {
          this.itemsForSaving.push(item);
        } else {
          this.itemsForSaving = [];
          this.isSavingWasFail = true;
          this.errorMessage = 'Please, fill out the form correctly'
          this.delayHidingFailingMessages();
          return false;
        }
      }
      return true;
  }
  delayHidingFailingMessages() {
    setTimeout(() => {
      this.isSavingWasFail = false;
      this.errorMessage = '';
    }, 2000);
  }
  delayHidingCloseDialoge() {
    setTimeout(() => {
      this.dialogRef.close();
    }, 2000);
  }
} 
