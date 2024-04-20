import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { LogEntry } from 'src/entities/LogEntry';
import { LogTag } from 'src/entities/LogTag';

const USER_KEY = 'auth-user';
const ACTIVE_TAG = 'acive-history-tag';


@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean>;
  public isLoggedIn$: Observable<boolean>;
  private historySubject: Subject<LogEntry[]> = new Subject<LogEntry[]>();
  history$ = this.historySubject.asObservable();
  private userHistory: LogEntry[] = [];

  private activeHistoryTagSubject: BehaviorSubject<LogTag>;
  activeHistoryTag$: Observable<LogTag>;
  

  constructor() {
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.getLoggedInStatus());
    this.isLoggedIn$ = this.isLoggedInSubject.asObservable();
    
    this.activeHistoryTagSubject = new BehaviorSubject<LogTag>(this.getActiveHistoryTag());
    this.activeHistoryTag$ = this.activeHistoryTagSubject.asObservable();
  }

  private getLoggedInStatusFromStorage(): boolean {
    return this.isLoggedIn();
  }

  clean(): void {
    window.sessionStorage.clear();
    window.localStorage.clear();
  }

  public saveUser(user: any): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUser() {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return null;
  }

  public isLoggedIn(): boolean {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return true;
    }
    return false;
  }
  getLoggedInStatus(): boolean {
    return localStorage.getItem('isLoggedIn') === 'true';
  }

  setLoggedInStatus(isLoggedIn: boolean): void {
    localStorage.setItem('isLoggedIn', isLoggedIn ? 'true' : 'false');
    this.isLoggedInSubject.next(isLoggedIn);
  }
  setActiveHistoryTag(tagName: string, entityId: number) {
    const historyTag = new LogTag();
    historyTag.tagName = tagName;
    historyTag.entityId = entityId;

    window.sessionStorage.removeItem(ACTIVE_TAG);
    if (historyTag.tagName && historyTag.entityId) {
      window.sessionStorage.setItem(ACTIVE_TAG, JSON.stringify(historyTag));
      this.activeHistoryTagSubject.next(historyTag);
    }
  }
  getActiveHistoryTag(): LogTag {
    const historyTagString = window.sessionStorage.getItem(ACTIVE_TAG);
    if (historyTagString) {
      try {
        return JSON.parse(historyTagString) as LogTag;
      } catch (error) {
        console.error('Error parsing HistoryTag:', error);
      }
    }
    return {} as LogTag;
  }
  getHistory(): LogEntry[] {
    return this.userHistory;
  }

  setHistory(history: LogEntry[]) {
    this.userHistory = history;
    this.historySubject.next(history); 
  }
}
