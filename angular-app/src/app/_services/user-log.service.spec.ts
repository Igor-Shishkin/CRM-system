import { TestBed } from '@angular/core/testing';

import { UserLogService } from './user-log.service';

describe('UserLogService', () => {
  let service: UserLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserLogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
