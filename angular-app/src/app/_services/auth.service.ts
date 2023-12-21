import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { User } from '../../entities/User';
import { HistoryMessage } from '../../entities/HistoryMessage';

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
    return this.http.delete(AUTH_API + `delete?userId=${userId}`, { responseType: 'text'});
  } 

  logout(): Observable<any> {
    return this.http.post(AUTH_API + 'signout', { }, httpOptions);
  }

  isAuthenticatedAsUser(): Observable<any> {
    return this.http.get(AUTH_API + 'check/user-role', { responseType: 'text' });
  }
  isAuthenticatedAsModerator(): Observable<any> {
    return this.http.get(AUTH_API + 'check/moderator-role', { responseType: 'text' });
  }
  isAuthenticatedAsAdmin(): Observable<any> {
    return this.http.get(AUTH_API + 'check/admin-role', { responseType: 'text' });
  }
  isAuthenticated(): Observable<boolean> {
    return this.http.get<boolean>(AUTH_API + 'check', { });
  }

}
