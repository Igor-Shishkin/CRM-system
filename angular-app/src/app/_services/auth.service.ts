import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { User } from '../../entities/User';
import { LogEntry } from '../../entities/LogEntry';

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
  deleteUser(userId: number): Observable<any> {
    return this.http.delete(AUTH_API + `delete-user?userId=${userId}`, { responseType: 'text'});
  } 

  logout(): Observable<any> {
    return this.http.post(AUTH_API + 'signout', { }, httpOptions);
  }

  isAuthenticatedAsUser(): Observable<boolean> {
    return this.http.get<boolean>(AUTH_API + 'check-authorization/user-role', { });
  }
  isAuthenticatedAsAdmin(): Observable<boolean> {
    return this.http.get<boolean>(AUTH_API + 'check-authorization/admin-role', { });
  }
  isAuthenticated(): Observable<boolean> {
    return this.http.get<boolean>(AUTH_API + 'check-authorization', { });
  }

}
