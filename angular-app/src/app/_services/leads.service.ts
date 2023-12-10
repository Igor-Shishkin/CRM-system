import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { Lead } from '../Lead';

const LIDS_API = 'http://localhost:8080/api/user-board'
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class LidsService {

  constructor(private http: HttpClient) { }

  getListOfClients(): Observable<any> {
    return this.http.get<Lead[]>(LIDS_API + '/clients').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  getListOfLids(): Observable<any> {
    return this.http.get<Lead[]>(LIDS_API + '/lids').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  deleteLidById(lidId : number) {
    return this.http.delete(`${LIDS_API}/delete?leadId=${lidId}`, { responseType: 'text' });
  }
  addLid(fullName : string, address : string, email : string, phoneNumber : string):
    Observable<any> {
      return this.http.post(LIDS_API,
        {
          fullName,
          address,
          email,
          phoneNumber
        },
        httpOptions );
    }
}
