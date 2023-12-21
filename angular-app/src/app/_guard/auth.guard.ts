import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';

export const authGuard: CanActivateFn = (route, state) => {

  const router = inject(Router);
  const authService = inject(AuthService);
  const storageService = inject(StorageService);
  const USER_KEY = 'auth-user';

  console.log('auth-guard');

  authService.isAuthenticated().subscribe({
    next: data => {
      if (data) {
        storageService.setLoggedInStatus(true);
        return true;
      } else {
        console.log('You do not have permissions');
        window.sessionStorage.removeItem(USER_KEY);
        storageService.setLoggedInStatus(false);
        router.navigateByUrl('/login');
        return false;
      }
    }, error: err => {
      console.log('can not connect with server: ' + err.toString);
      storageService.setLoggedInStatus(false);
      router.navigateByUrl('/login');
      window.sessionStorage.removeItem(USER_KEY);
      return false;
    }
  })
  return true;

};
