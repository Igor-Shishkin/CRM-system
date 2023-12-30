import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ItemForCalculation } from 'src/entities/Calculation';

@Component({
  selector: 'app-item-calculation',
  templateUrl: './item-calculation.component.html',
  styleUrls: ['./item-calculation.component.css']
})
export class ItemCalculationComponent {

  items: ItemForCalculation[];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { items: ItemForCalculation[] }
  ) {
    this.items = this.data.items;
    console.log(this.items);
    console.log('HEj');

  }
} 
