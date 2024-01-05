import { TestBed } from '@angular/core/testing';

import { CalculationItemsService } from './calculation-items.service';

describe('CalculationItemsService', () => {
  let service: CalculationItemsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CalculationItemsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
