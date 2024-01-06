import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { NewCalculations } from 'src/entities/NewCalculations';
import { Order } from 'src/entities/Order';

const ORDER_API = 'http://localhost:8080/api/user-board/order'
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(private http: HttpClient) { }

  getOrder(orderId: number):Observable<Order>{
    return this.http.get<Order>(`${ORDER_API}?orderId=${orderId}`).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
  getNewCalculations(orderId: number):Observable<NewCalculations>{
    return this.http.get<NewCalculations>(`${ORDER_API}/new-calculations?orderId=${orderId}`).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
  signAgreement(orderId: number) {
    return this.http.post<any>(`${ORDER_API}/sign-agreement?orderId=${orderId}`, { responseType: 'text' }).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
  cancelAgreement(orderId: number) {
    return this.http.post<any>(`${ORDER_API}/cancel-agreement?orderId=${orderId}`, { responseType: 'text' }).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }


  saveAgreementStatus(isAgreementSigned: boolean, orderId: number) {
    return this.http.post(ORDER_API + '/sign-agreement',
    {
      isAgreementSigned: isAgreementSigned,
      orderId: orderId
    }, httpOptions
  ).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
  savePainmentStatus(isPaid: boolean, orderId: number) {
    return this.http.post(ORDER_API + '/painment-status',
    {
      isPaid: isPaid,
      orderId: orderId
    }, httpOptions
  ).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
}
