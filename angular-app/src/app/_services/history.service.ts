import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map } from 'rxjs';
import { HistoryMessage } from 'src/entities/HistoryMessage';
import { HistoryTag } from 'src/entities/HistoryTag';

const HISTORY_URL = 'http://localhost:8080/api/history-message/';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  constructor(private http: HttpClient) { }

  getHistory(): Observable<HistoryMessage[]> {
    return this.http.get<any[]>(HISTORY_URL + 'get-history').pipe(
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
  getTagsForNewHistoryMessage(): Observable<any> {
    return this.http.get<HistoryTag[]>(HISTORY_URL + 'tags').pipe(
      catchError((error: any) => {
        console.error('Loading HistoryTags error: ' + error);
        throw error;
      })
    );
  }
}
