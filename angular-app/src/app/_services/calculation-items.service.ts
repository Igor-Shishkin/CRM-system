import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError } from 'rxjs';
import { ItemForAdditionalPurchases } from 'src/entities/ItemForAdditionalPurchases';

const ITEMS_API = 'http://localhost:8080/api/user-board/items-for-addition-purchases'
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AdditionalPurchasesService {

  constructor(private http: HttpClient) { }

  saveItemsForPurchases(items: ItemForAdditionalPurchases[], orderId : number) {
    console.log(items)
    return this.http.post(ITEMS_API + '/save-items',
    items,
    {
      params: {
        orderId: orderId.toString() 
      },
      headers: httpOptions.headers
    }
  ).pipe(
      catchError((error: any) => {
        console.log(error);
        throw error;
      })
    )
  }
}
