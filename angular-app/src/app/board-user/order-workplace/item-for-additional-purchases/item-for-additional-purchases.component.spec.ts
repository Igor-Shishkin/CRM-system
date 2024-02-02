import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemForAdditionalPurchasesComponent } from './item-for-additional-purchases.component';

describe('ItemCalculationComponent', () => {
  let component: ItemForAdditionalPurchasesComponent;
  let fixture: ComponentFixture<ItemForAdditionalPurchasesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ItemForAdditionalPurchasesComponent]
    });
    fixture = TestBed.createComponent(ItemForAdditionalPurchasesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
