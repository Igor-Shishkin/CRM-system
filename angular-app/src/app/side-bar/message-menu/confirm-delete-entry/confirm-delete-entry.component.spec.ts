import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmDeleteEntryComponent } from './confirm-delete-entry.component';

describe('ConfirmDeleteComponent', () => {
  let component: ConfirmDeleteEntryComponent;
  let fixture: ComponentFixture<ConfirmDeleteEntryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmDeleteEntryComponent]
    });
    fixture = TestBed.createComponent(ConfirmDeleteEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
