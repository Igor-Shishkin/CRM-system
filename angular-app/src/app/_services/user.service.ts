import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map } from 'rxjs';
import { HistoryMessage } from '../../entities/HistoryMessage';
import { User } from '../../entities/User';

const API_URL = 'http://localhost:8080/api/user/';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  getHistory(): Observable<HistoryMessage[]> {
    return this.http.get<any[]>(API_URL + 'history').pipe(
      map((data: any[]) => {
        return data.map((item: any) => {
          return {
            ...item,
            dateOfCreation: new Date(item.dateOfCreation.replace(' ', 'T'))
          } as HistoryMessage;
        });
      }),
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  getUserPhoto(): Observable<Blob> {
    return this.http.get(API_URL + 'photo', { responseType: 'blob'})
  }
  uploadPhoto(formData: FormData): Observable<any> {
    return this.http.post(API_URL + 'photo',  formData );
  }
  getAllUsers(): Observable<any> {
    return this.http.get<User[]>(API_URL + 'users').pipe(
      catchError((error:any) => {
        console.error(error);
        throw error;
      })
    );
  }
}
