import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
  name: 'appDate'
})
export class AppDatePipe implements PipeTransform {
  transform(value: any, args?: any): any {
    const datePipe = new DatePipe('en-US');
    return datePipe.transform(value, 'EEE MMM dd yyyy HH:mm:ss');
  }
}