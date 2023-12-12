import { Injectable } from '@angular/core';
import { Client } from '../../entities/Client';
import { HistoryMessage } from '../../entities/HistoryMessage';

@Injectable({
  providedIn: 'root'
})
export class SharedServiceService {

  activeLid?: Client;
  history?: HistoryMessage[];

  constructor() {  }
}
