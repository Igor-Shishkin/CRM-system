import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { Lid } from '../Lid';

const LIDS_API = 'http://localhost:8080/api/client'
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class LidsService {

  constructor(private http: HttpClient) { }

  getListOfClients(): Observable<any> {
    return this.http.get<Lid[]>(LIDS_API).pipe(
      catchError((error: any) => {
        console.error(error); // Log the detailed error information here
        throw error; // Rethrow the error for further handling in the component
      })
    );
  }
  deleteLidById(id : number) {
    return this.http.get(LIDS_API + id, {responseType: 'text' })
  }
  addLid(name : string, surname : string, email : string, phoneNumber : string):
    Observable<any> {
      return this.http.post(LIDS_API, 
        {
          name,
          surname,
          email,
          phoneNumber
        },
        httpOptions );
    }
}
  
// export interface Lid {
//   id : number;
//   name : string;
//   surname : string;
//   email : string;
//   phoneNumber : string;
//   isClient : boolean;
//   orders: Order[];
// }
