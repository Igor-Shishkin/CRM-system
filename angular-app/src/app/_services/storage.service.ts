import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { HistoryMessage } from 'src/entities/HistoryMessage';

const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean>;
  public isLoggedIn$: Observable<boolean>;
  private activeClientIdSubject: Subject<number> = new Subject<number>();
  private historySubject: Subject<HistoryMessage[]> = new Subject<HistoryMessage[]>();
  activeClientId$ = this.activeClientIdSubject.asObservable(); 
  history$ = this.historySubject.asObservable();
  private userHistory: HistoryMessage[] = [];
  clientID = -1;


  constructor() {
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.getLoggedInStatus());
    this.isLoggedIn$ = this.isLoggedInSubject.asObservable();
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

  public getUser(): any {
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
  getActiveClientId(): number {
    return this.clientID;
  }
  setActiveClientId(id: number) {
    this.clientID = id;
    this.activeClientIdSubject.next(id);
  }
  getHistory(): HistoryMessage[] {
    return this.userHistory;
  }

  setHistory(history: HistoryMessage[]) {
    this.userHistory = history;
    this.historySubject.next(history); 
  }
}
