import { Component } from '@angular/core';
import { LidsService } from 'src/app/_services/lids.service';

@Component({
  selector: 'app-add-lid',
  templateUrl: './add-lid.component.html',
  styleUrls: ['./add-lid.component.css']
})
export class AddLidComponent {
  message = '';

  constructor(private lidService: LidsService){}
  
  save(fullName: HTMLInputElement, address: HTMLInputElement, email: HTMLInputElement, phoneNumber: HTMLInputElement) {
    this.lidService.addLid(fullName.value, address.value, email.value, phoneNumber.value).subscribe({
      next: data => {
        this.message = data;
      }, error: (err: any) => {
        console.error(err);
      }
    })
  }
}
