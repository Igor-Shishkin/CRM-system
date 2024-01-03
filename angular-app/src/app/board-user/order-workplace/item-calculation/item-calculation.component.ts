import { Component, Inject, numberAttribute } from '@angular/core';
import { FormControl, ValidatorFn, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ItemForCalculation } from 'src/entities/ItemForCalculation';

@Component({
  selector: 'app-item-calculation',
  templateUrl: './item-calculation.component.html',
  styleUrls: ['./item-calculation.component.css']
})
export class ItemCalculationComponent {
saveItems() {
throw new Error('Method not implemented.');
}

  items: ItemForCalculation[];
  resultPrice = 0;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { items: ItemForCalculation[] }
  ) {
    this.items = this.data.items;
    this.calculateResultPrice();
  }

  calculateResultPrice() {
    this.resultPrice = 0;
    for (let item of this.items) {
      if (
          ((!item.unitPrice || item.unitPrice < 0) || 
          (!item.quantity || item.quantity < 0)) && 
          item.thing && item.thing.length > 0
        ) {
        this.resultPrice = -1;
        return;
      }
      this.resultPrice += item.totalPrice || 0;
    }
    this.resultPrice = +this.resultPrice.toFixed(2);
  }
  calcilateTotalPriceForItem(item: ItemForCalculation) {
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
    this.items.push(new ItemForCalculation);
  }
  isEmptyThing(item: ItemForCalculation) {
    if (item.thing && item.thing.trim().length >0) {
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
  deleteItem(item: ItemForCalculation) {
    if (this.resultPrice != -1 && item.totalPrice) {
      this.resultPrice = this.resultPrice - item.totalPrice;
      this.resultPrice = +this.resultPrice.toFixed(2);
    }
    this.items = this.items.filter((i: ItemForCalculation) => i !== item);
  }
} 
