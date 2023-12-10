import { Injectable } from '@angular/core';
import { Lead } from '../Lead';
import { HistoryMessage } from '../HistoryMessage';

@Injectable({
  providedIn: 'root'
})
export class SharedServiceService {

  activeLid?: Lead;
  history?: HistoryMessage[];

  constructor() {  }
}
