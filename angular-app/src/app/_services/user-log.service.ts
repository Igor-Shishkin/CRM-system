import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, tap } from 'rxjs';
import { LogEntry } from 'src/entities/LogEntry';
import { LogTag } from 'src/entities/LogTag';

const LOG_URL = 'http://localhost:8080/api/log';

@Injectable({
  providedIn: 'root'
})
export class UserLogService {

  constructor(private http: HttpClient) { }

  getLog(): Observable<LogEntry[]> {
    return this.http.get<any[]>(LOG_URL + '/get-user-log').pipe(
      catchError((error: any) => {
        console.error(error);
        throw error;
      })
    );
  }



  getTagsForNewLogEntry(): Observable<any> {
    return this.http.get<LogTag[]>(LOG_URL + '/tags').pipe(
      catchError((error: any) => {
        console.error('Loading HistoryTags error: ' + error);
        throw error;
      })
    );
  }



  saveNewLogEntry(entry: LogEntry) {
    return this.http.post<LogEntry>(LOG_URL, entry).pipe(
      tap((response) => {
        console.log('Request sent successfully:', response);
      }),
      catchError((error: any) => {
        console.error('Sending Log Entry error: ' + error);
        throw error;
      })
    );
  }



  deleteEntry(entryId: number): any {
    return this.http.delete<any>(LOG_URL + `?entryId=${entryId}`).pipe(
      catchError((error: any) => {
        console.error('Deleting log entry error: ' + error);
        throw error;
      })
    );
  }



  changeImportantStatus(entryId: number | undefined) {
    return this.http.patch<LogEntry>(LOG_URL + `/change-important-status?entryId=${entryId}`, null).pipe(
      catchError((error: any) => {
        console.error('Change important status log entry error: ' + error);
        throw error;
      })
    );
  }



  changeDoneStatus(entryId: number | undefined) {
    return this.http.patch<any>(LOG_URL + `/change-done-status?entryId=${entryId}`, null).pipe(
      catchError((error: any) => {
        console.error('Change important status HistoryMessage error: ' + error);
        throw error;
      })
    );
  }
}
