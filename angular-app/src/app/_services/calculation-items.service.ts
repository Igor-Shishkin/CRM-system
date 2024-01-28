import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError } from 'rxjs';
import { ItemForCalculation } from 'src/entities/ItemForCalculation';

const ITEMS_API = 'http://localhost:8080/api/user-board/items-for-calculation'
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class CalculationItemsService {

  constructor(private http: HttpClient) { }

  saveItemsForCalculation(items: ItemForCalculation[], orderId : number) {
    console.log(items)
    return this.http.post(ITEMS_API + '/save-items',
    items, // Send the array directly
    {
      params: {
        orderId: orderId.toString() // Send orderId as a query parameter
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
