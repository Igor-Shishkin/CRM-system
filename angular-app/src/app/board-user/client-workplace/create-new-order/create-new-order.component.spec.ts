import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateNewOrderComponent } from './create-new-order.component';

describe('CreateNewOrderComponent', () => {
  let component: CreateNewOrderComponent;
  let fixture: ComponentFixture<CreateNewOrderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateNewOrderComponent]
    });
    fixture = TestBed.createComponent(CreateNewOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
