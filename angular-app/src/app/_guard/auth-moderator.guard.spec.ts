import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { authModeratorGuard } from './auth-moderator.guard';

describe('authModeratorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => authModeratorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
