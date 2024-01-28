import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { NewCalculations } from 'src/entities/NewCalculations';
import { Order } from 'src/entities/Order';
import { CalculationItemsService } from './calculation-items.service';
import { ItemCalculationComponent } from '../board-user/order-workplace/item-calculation/item-calculation.component';
import { ItemForCalculation } from 'src/entities/ItemForCalculation';

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
  getCalculations(orderId: number):Observable<NewCalculations>{
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

  const orderCopy: Order = { ...order };

  orderCopy.calculations = {} as ItemForCalculation[];
  orderCopy.resultPrice = undefined;
  orderCopy.hasBeenPaid = undefined;
  orderCopy.isAgreementSigned = undefined;
  orderCopy.clientEmail = undefined;
  orderCopy.clientFullName = undefined;
  orderCopy.clientId = undefined;
  orderCopy.clientPhoneNumber = undefined;
  orderCopy.dateOfCreation = undefined;
  orderCopy.dateOfLastChange = undefined;

    return this.http.post<any>(`${ORDER_API}/save-order-changes`,  orderCopy , httpOptions).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }

  createNewOrder(clientId: number, realNeed: string, estimateBudget: number) {
    console.log(clientId)
    return this.http.post<number>(`${ORDER_API}/create-new-order`, 
    {
      clientId,
      realNeed, 
      estimateBudget
    }, httpOptions)
  }
}
