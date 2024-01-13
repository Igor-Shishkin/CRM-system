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
  confirmPayment(orderId: number) {
    return this.http.post<any>(`${ORDER_API}/confirm-payment?orderId=${orderId}`, { responseType: 'text' }).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
  cancelPayment(orderId: number) {
    return this.http.post<any>(`${ORDER_API}/cancel-payment?orderId=${orderId}`, { responseType: 'text' }).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
  saveChenges(order: Order) {

    order.calculations = [];
    order.projectPhotos = [];
    order.resultPrice = undefined;
    order.hasBeenPaid = undefined;
    order.isAgreementSigned = undefined;
    order.clientEmail = undefined;
    order.clientFullName = undefined;
    order.clientId = undefined;
    order.clientPhoneNumber = undefined;
    order.dateOfCreation = undefined;
    order.dateOfLastChange = undefined;

    return this.http.post<any>(`${ORDER_API}/save-order-changes`,  order , httpOptions).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
}
