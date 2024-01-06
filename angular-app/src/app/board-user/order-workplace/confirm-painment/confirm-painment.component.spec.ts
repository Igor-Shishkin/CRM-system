import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmPainmentComponent } from './confirm-painment.component';

describe('ConfirmPainmentComponent', () => {
  let component: ConfirmPainmentComponent;
  let fixture: ComponentFixture<ConfirmPainmentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmPainmentComponent]
    });
    fixture = TestBed.createComponent(ConfirmPainmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
