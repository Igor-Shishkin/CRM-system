import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlackListOfClientsComponent } from './black-list-of-clients.component';

describe('BlackListOfClientsComponent', () => {
  let component: BlackListOfClientsComponent;
  let fixture: ComponentFixture<BlackListOfClientsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BlackListOfClientsComponent]
    });
    fixture = TestBed.createComponent(BlackListOfClientsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
