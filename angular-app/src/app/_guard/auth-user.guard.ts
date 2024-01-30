import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';

export const authUserGuard: CanActivateFn = (route, state) => {
  
  const router = inject(Router);
  const authService = inject(AuthService);
  const storageService = inject(StorageService);
  const USER_KEY = 'auth-user';

  authService.isAuthenticatedAsUser().subscribe({
    next: data => {
      if (data) {
        console.log('good connection')
        storageService.setLoggedInStatus(true);
        return true;
      } else {
        console.log('You do not have permissions');
        router.navigateByUrl('/home/not-permission');
        return false;
      }
    }, error: err => {
      console.log('can not connect with server: ' + err.toString);
      router.navigateByUrl('/home/not-permission');
      return false;
    }
  })
  return true;
  
};
