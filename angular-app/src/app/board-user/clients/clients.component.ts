import { Component, OnInit } from '@angular/core';
import { Client } from 'src/entities/Client';
import { ClientsService } from 'src/app/_services/clients.service';
import { Router } from '@angular/router';
import { StorageService } from 'src/app/_services/storage.service';

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.css']
})
export class ClientsComponent implements OnInit{
  clients!: Client[]
  isSuccessLoad = false;
  isSuccessDelete = false
  responseMessage = '';
  errorMessage = '';
  isError = false;
  isRequestSent = false;
  
  constructor(private clientService: ClientsService,
              private router: Router,
              private storageService: StorageService) {}
  
    ngOnInit(): void {
      this.refreshListOfClients();
    }

    refreshListOfClients(){
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
    goToClientDetail(clientId: number) {
      this.storageService.setActiveClientId(clientId);
      this.router.navigate(['/user-board/client-workplace', clientId]);
    }
  }
  