import { TestBed } from '@angular/core/testing';

import { AdditionalPurchasesService } from './calculation-items.service';

describe('CalculationItemsService', () => {
  let service: AdditionalPurchasesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdditionalPurchasesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
