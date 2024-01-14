import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, tap } from 'rxjs';
import { HistoryMessage } from 'src/entities/HistoryMessage';
import { HistoryTag } from 'src/entities/HistoryTag';

const HISTORY_URL = 'http://localhost:8080/api/history-message';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  constructor(private http: HttpClient) { }

  getHistory(): Observable<HistoryMessage[]> {
    return this.http.get<any[]>(HISTORY_URL + '/get-history').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  getTagsForNewHistoryMessage(): Observable<any> {
    return this.http.get<HistoryTag[]>(HISTORY_URL + '/tags').pipe(
      catchError((error: any) => {
        console.error('Loading HistoryTags error: ' + error);
        throw error;
      })
    );
  }
  saveNewHistoryMessage(message: HistoryMessage) {
    return this.http.post<HistoryMessage>(HISTORY_URL, message).pipe(
      tap((response) => {
        console.log('Request sent successfully:', response);
      }),
      catchError((error: any) => {
        console.error('Sending HistoryMessage error: ' + error);
        throw error;
      })
    );
  }
  deleteMessage(messageId: number) {
    this.http.delete(HISTORY_URL + `?messageId=${messageId}`).pipe(
      catchError((error: any) => {
        console.error('Deleting HistoryMessage error: ' + error);
        throw error;
      })
    );
  }
  changeImportantStatus(messageId: number | undefined) {
    return this.http.put<HistoryMessage>(HISTORY_URL + `/change-important-status?messageId=${messageId}`, null).pipe(
      catchError((error: any) => {
        console.error('Change important status HistoryMessage error: ' + error);
        throw error;
      })
    );
  }
  changeDoneStatus(messageId: number | undefined) {
    return this.http.put<any>(HISTORY_URL + `/change-done-status?messageId=${messageId}`, null).pipe(
      catchError((error: any) => {
        console.error('Change important status HistoryMessage error: ' + error);
        throw error;
      })
    );
  }
}
