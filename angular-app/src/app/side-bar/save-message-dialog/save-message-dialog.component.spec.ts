import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaveMessageDialogComponent } from './save-message-dialog.component';

describe('MessageDialogComponent', () => {
  let component: SaveMessageDialogComponent;
  let fixture: ComponentFixture<SaveMessageDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SaveMessageDialogComponent]
    });
    fixture = TestBed.createComponent(SaveMessageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
