import { TestBed } from '@angular/core/testing';

import { LidsService } from './lids.service';

describe('LidsService', () => {
  let service: LidsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LidsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
