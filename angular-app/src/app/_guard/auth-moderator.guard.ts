import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';

export const authModeratorGuard: CanActivateFn = (route, state) => {
  
  const router = inject(Router);
  const authService = inject(AuthService);
  const storageService = inject(StorageService);
  const USER_KEY = 'auth-user';

  authService.isAuthenticatedAsModerator().subscribe({
    next: data => {
      if (data) {
        storageService.setLoggedInStatus(true);
        return true;
      } else {
        console.log('You do not have permissions');
        window.sessionStorage.removeItem(USER_KEY);
        storageService.setLoggedInStatus(false);
        router.navigateByUrl('/home/not-permission');
        return false;
      }
    }, error: err => {
      console.log('can not connect with server: ' + err.toString);
      storageService.setLoggedInStatus(false);
      router.navigateByUrl('/home/not-permission');
      window.sessionStorage.removeItem(USER_KEY);
      return false;
    }
  })
  return true;

};
