import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, tap } from 'rxjs';
import { LogEntry } from 'src/entities/LogEntry';
import { LogTag } from 'src/entities/LogTag';

const HISTORY_URL = 'http://localhost:8080/api/log';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  constructor(private http: HttpClient) { }

  getHistory(): Observable<LogEntry[]> {
    return this.http.get<any[]>(HISTORY_URL + '/get-user-log').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }
  getTagsForNewHistoryMessage(): Observable<any> {
    return this.http.get<LogTag[]>(HISTORY_URL + '/tags').pipe(
      catchError((error: any) => {
        console.error('Loading HistoryTags error: ' + error);
        throw error;
      })
    );
  }
  saveNewHistoryMessage(message: LogEntry) {
    return this.http.post<LogEntry>(HISTORY_URL, message).pipe(
      tap((response) => {
        console.log('Request sent successfully:', response);
      }),
      catchError((error: any) => {
        console.error('Sending HistoryMessage error: ' + error);
        throw error;
      })
    );
  }
  deleteMessage(messageId: number): any {
    return this.http.delete<any>(HISTORY_URL + `?messageId=${messageId}`).pipe(
      catchError((error: any) => {
        console.error('Deleting HistoryMessage error: ' + error);
        throw error;
      })
    );
  }
  changeImportantStatus(messageId: number | undefined) {
    return this.http.put<LogEntry>(HISTORY_URL + `/change-important-status?messageId=${messageId}`, null).pipe(
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
