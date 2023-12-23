import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { Client } from 'src/entities/Client';

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
    return this.http.get<Client[]>(CLIENTS_API + '/clients').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  getListOfLeads(): Observable<any> {
    return this.http.get<Client[]>(CLIENTS_API + '/lids').pipe(
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
  getClientInformarion(clientId: number): Observable<Client> {
    return this.http.get<Client>(`${CLIENTS_API}/client-info?clientId=${clientId}`).pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  editClientData(clientId: number, fullName: string, email: string, 
    address: string, phoneNumber: string): Observable<string> {
    return this.http.put<string>(
      CLIENTS_API + '/edit-client-data',
      {
        clientId,
        fullName,
        email,
        address,
        phoneNumber,
      },
      httpOptions
    );
  }
}
