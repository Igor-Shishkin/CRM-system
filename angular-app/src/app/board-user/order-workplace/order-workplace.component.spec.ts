import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderWorkplaceComponent } from './order-workplace.component';

describe('OrderWorkplaceComponent', () => {
  let component: OrderWorkplaceComponent;
  let fixture: ComponentFixture<OrderWorkplaceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrderWorkplaceComponent]
    });
    fixture = TestBed.createComponent(OrderWorkplaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
