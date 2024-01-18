import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmDeleteMessageComponent } from './confirm-delete-message.component';

describe('ConfirmDeleteComponent', () => {
  let component: ConfirmDeleteMessageComponent;
  let fixture: ComponentFixture<ConfirmDeleteMessageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmDeleteMessageComponent]
    });
    fixture = TestBed.createComponent(ConfirmDeleteMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
