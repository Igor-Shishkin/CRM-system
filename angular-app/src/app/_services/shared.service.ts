import { Injectable } from '@angular/core';
import { Lead } from '../Lid';

@Injectable({
  providedIn: 'root'
})
export class SharedServiceService {

  activeLid?: Lead;

  constructor() {  }
}
