import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ClientsService } from 'src/app/_services/clients.service';
import { SharedServiceService } from 'src/app/_services/shared.service';
import { Client } from 'src/entities/Client';

@Component({
  selector: 'app-client-workplace',
  templateUrl: './client-workplace.component.html',
  styleUrls: ['./client-workplace.component.css']
})
export class ClientWorkplaceComponent {
  client?: Client;
  isRequestSent = false;
  isSuccessLoad = false;
  responseMessage = '';
  errorMessage = '';
  isError = false;

  constructor(private clientService: ClientsService, 
      private sharedService : SharedServiceService,
      private router: Router ) {}

  ngOnInit(): void {
    this.clientService.getClientInformarion(this.sharedService.activeLid?.id || -1).subscribe({
      next: data => {
        this.client = data;
      }, error: err => {
        console.log(err);
        this.isError = true;
        this.errorMessage = 'An error occurred while loading client information';
      }
    })
  }

  sentClientToBlackList() {
    this.isRequestSent = true;
    this.clientService.sentClientToBlackList(this.client?.id || -1).subscribe({
      next: (data: any) => {
        this.responseMessage = data;
        this.reloadPage(1500);
      },
      error: (err: any) => {
        console.error(err);
        this.isError = true;
        this.errorMessage = 'Error deleting Client';
        this.isRequestSent = false;
      }
    });
  }
  reloadPage(delay: number): void {
    setTimeout(() => {
      this.router.navigateByUrl('/home');
    }, delay); 
  }
}

