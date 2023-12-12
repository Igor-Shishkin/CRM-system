import { Injectable } from '@angular/core';
import { Lead } from '../../entities/Lead';
import { HistoryMessage } from '../../entities/HistoryMessage';

@Injectable({
  providedIn: 'root'
})
export class SharedServiceService {

  activeLid?: Lead;
  history?: HistoryMessage[];

  constructor() {  }
}
