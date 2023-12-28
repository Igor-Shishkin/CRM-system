import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemCalculationComponent } from './item-calculation.component';

describe('ItemCalculationComponent', () => {
  let component: ItemCalculationComponent;
  let fixture: ComponentFixture<ItemCalculationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ItemCalculationComponent]
    });
    fixture = TestBed.createComponent(ItemCalculationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
