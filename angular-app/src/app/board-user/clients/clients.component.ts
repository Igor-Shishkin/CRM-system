import { Component } from '@angular/core';
import { Client } from 'src/entities/Client';
import { ClientsService } from 'src/app/_services/clients.service';
import { SharedServiceService } from 'src/app/_services/shared.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.css']
})
export class ClientsComponent {
  clients!: Client[]
  isSuccessLoad = false;
  isSuccessDelete = false
  responseMessage = '';
  errorMessage = '';
  isError = false;
  isRequestSent = false;
  
  constructor(private clientService: ClientsService,
              private router: Router) {}
  
    ngOnInit(): void {
      this.refreshListOfLeads;
    }
    sentClientToBlackList( id : number) {
      this.isRequestSent = true;
      this.clientService.sentClientToBlackList(id).subscribe({
        next: (data: any) => {
          this.responseMessage = data;
          this.isSuccessDelete = true;
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
    refreshListOfLeads(){
      this.isRequestSent = false;
      this.clientService.getListOfClients().subscribe({
        next: data => {
          this.clients = data;
          this.isSuccessLoad = true;
          this.isRequestSent = false;
        },
        error: (err: any) => {
          console.error(err); 
          this.isError = true;
          this.errorMessage = 'Error loading data';
          this.isRequestSent = false;
        }
      })
    }
    reloadPage(delay: number): void {
      setTimeout(() => {
        this.isSuccessDelete = false;
        this.refreshListOfLeads;
      }, delay); 
    }
    goToClientDetail(clientId: number) {
      this.router.navigate(['/user-board/client-workplace', clientId]);
    }
  }
  