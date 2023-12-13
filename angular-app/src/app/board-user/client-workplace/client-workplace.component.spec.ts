import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientWorkplaceComponent } from './client-workplace.component';

describe('ClientWorkplaceComponent', () => {
  let component: ClientWorkplaceComponent;
  let fixture: ComponentFixture<ClientWorkplaceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientWorkplaceComponent]
    });
    fixture = TestBed.createComponent(ClientWorkplaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
