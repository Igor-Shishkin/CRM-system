import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { Lead } from 'src/entities/Lead';

const CLIENTS_API = 'http://localhost:8080/api/user-board'
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ClientsService {

  constructor(private http: HttpClient) { }

  getListOfClients(): Observable<any> {
    return this.http.get<Lead[]>(CLIENTS_API + '/clients').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  getListOfLids(): Observable<any> {
    return this.http.get<Lead[]>(CLIENTS_API + '/lids').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  sentClientToBlackList(clientId : number) {
    return this.http.put(`${CLIENTS_API}/to-black-list?leadId=${clientId}`, { responseType: 'text' });
  }
  addLid(fullName : string, address : string, email : string, phoneNumber : string):
    Observable<any> {
      return this.http.post(CLIENTS_API,
        {
          fullName,
          address,
          email,
          phoneNumber
        },
        httpOptions );
    }
}
