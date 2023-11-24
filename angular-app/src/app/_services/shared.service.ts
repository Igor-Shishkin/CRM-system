import { Injectable } from '@angular/core';
import { Lid } from '../Lid';

@Injectable({
  providedIn: 'root'
})
export class SharedServiceService {

  activeLid?: Lid;

  constructor() {  }
}
