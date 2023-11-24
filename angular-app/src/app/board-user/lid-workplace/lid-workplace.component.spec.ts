import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LidWorkplaceComponent } from './lid-workplace.component';

describe('LidWorkplaceComponent', () => {
  let component: LidWorkplaceComponent;
  let fixture: ComponentFixture<LidWorkplaceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LidWorkplaceComponent]
    });
    fixture = TestBed.createComponent(LidWorkplaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
