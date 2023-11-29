import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { User } from '../User';

const AUTH_API = 'http://localhost:8080/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'signin',
      {
        username,
        password,
      },
      httpOptions
    );
  }

  register(username: string, email: string, role: string[], password: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'signup',
      {
        username,
        email,
        role,
        password,
      },
      httpOptions
    );
  }

  getAllUsers(): Observable<any> {
    return this.http.get<User[]>(AUTH_API + 'users').pipe(
      catchError((error:any) => {
        console.error(error);
        throw error;
      })
    );
  }

  deleteUser(userId: number): Observable<any> {
    return this.http.delete(AUTH_API + `delete?userId=${userId}`, { responseType: 'text'});
  } 
  getUserPhoto(): Observable<Blob> {
    return this.http.get(AUTH_API + 'photo', { responseType: 'blob'})
  }

  getImageUrl(): Observable<string> {
    return this.http.get<string>(AUTH_API + 'photo');
    // Adjust the endpoint '/image/url' according to your backend API
  }

  uploadPhoto(formData: FormData): Observable<any> {
    return this.http.post(AUTH_API + 'photo',  formData );
  }

  logout(): Observable<any> {
    return this.http.post(AUTH_API + 'signout', { }, httpOptions);
  }
}

// getPublicContent(): Observable<any> {
//   return this.http.get(API_URL + 'all', { responseType: 'text' });
// }