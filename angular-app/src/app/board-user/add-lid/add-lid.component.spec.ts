import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddLidComponent } from './add-lid.component';

describe('AddLidComponent', () => {
  let component: AddLidComponent;
  let fixture: ComponentFixture<AddLidComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddLidComponent]
    });
    fixture = TestBed.createComponent(AddLidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
