import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaveEntryDialogComponent } from './save-entry-dialog.component';

describe('MessageDialogComponent', () => {
  let component: SaveEntryDialogComponent;
  let fixture: ComponentFixture<SaveEntryDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SaveEntryDialogComponent]
    });
    fixture = TestBed.createComponent(SaveEntryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
