import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmSigningContractComponent } from './confirm-signing-contract.component';

describe('ConfirmSigningContractComponent', () => {
  let component: ConfirmSigningContractComponent;
  let fixture: ComponentFixture<ConfirmSigningContractComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmSigningContractComponent]
    });
    fixture = TestBed.createComponent(ConfirmSigningContractComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
