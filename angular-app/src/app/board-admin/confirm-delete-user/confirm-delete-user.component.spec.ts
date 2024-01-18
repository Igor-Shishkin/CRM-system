import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmDeleteUserComponent } from './confirm-delete-user.component';

describe('ConfirmDeletingComponent', () => {
  let component: ConfirmDeleteUserComponent;
  let fixture: ComponentFixture<ConfirmDeleteUserComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmDeleteUserComponent]
    });
    fixture = TestBed.createComponent(ConfirmDeleteUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
