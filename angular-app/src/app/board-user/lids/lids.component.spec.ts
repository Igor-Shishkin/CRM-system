import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LidsComponent } from './lids.component';

describe('LidsComponent', () => {
  let component: LidsComponent;
  let fixture: ComponentFixture<LidsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LidsComponent]
    });
    fixture = TestBed.createComponent(LidsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
