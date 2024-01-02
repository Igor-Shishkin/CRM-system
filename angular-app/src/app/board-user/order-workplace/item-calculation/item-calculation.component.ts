import { Component, Inject, numberAttribute } from '@angular/core';
import { FormControl, ValidatorFn, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ItemForCalculation } from 'src/entities/Calculation';

@Component({
  selector: 'app-item-calculation',
  templateUrl: './item-calculation.component.html',
  styleUrls: ['./item-calculation.component.css']
})
export class ItemCalculationComponent {

  items: ItemForCalculation[];
  resultPrice = 0;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { items: ItemForCalculation[] }
  ) {
    this.items = this.data.items;
  }

  positiveIntegerFormControl = new FormControl('', [
    Validators.required,
    Validators.pattern(/^[1-9][0-9]*$/)
  ]);
  positiveNumberFormControl = new FormControl('', [
    Validators.required,
    Validators.min(0.1) // Adjust the minimum value as needed
  ]);
  

  calculateResultPrice() {
    this.resultPrice = 0;
    for (let item of this.items) {
      this.resultPrice += item.totalPrice || 0;
    }
    this.resultPrice = +this.resultPrice.toFixed(2);
  }
  calcilateTotalPriceForItem(item: ItemForCalculation) {
    if (item.unitPrice && item.quantity) {
      item.totalPrice = item.unitPrice * item.quantity * 1.1;
      item.totalPrice = +item.totalPrice.toFixed(2);
      this.calculateResultPrice();
    }
  }
  isNegativeNumber(price: number) {
    return price < 0;
  }
  isPositiveInteger(quantity: number) {
    return quantity >= 0 && Number.isInteger(quantity);
  }
  addItem() {
    this.items.push(new ItemForCalculation);
  }
  isEmptyThing(thing: string) {
    return thing.length == 0;
  }
} 
