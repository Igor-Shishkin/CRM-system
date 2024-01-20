import { Component, Input, NgModule, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ClientsService } from 'src/app/_services/clients.service';
import { StorageService } from 'src/app/_services/storage.service';
import { Client } from 'src/entities/Client';


@Component({
  selector: 'app-leads',
  templateUrl: './leads.component.html',
  styleUrls: ['./leads.component.css'],
})

export class LeadsComponent implements OnInit{
  leads!: Client[]
  isSuccessLoad = false;
  errorMessage = '';
  isError = false;
  isRequestSent = false;

  constructor(private clientService: ClientsService,
              private router: Router,
              private storageService: StorageService) {}
  
    ngOnInit(): void {
      this.refreshListOfLeads();
      this.storageService.setActiveHistoryTag('CLIENT', -1);
    }
    refreshListOfLeads(){
      this.isRequestSent = true;
      this.clientService.getListOfLeads().subscribe({
        next: data => {
          this.leads = data;
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
      if (!this.isRequestSent) {
        this.storageService.setActiveHistoryTag('CLIENT', clientId);
        this.router.navigate(['/user-board/client-workplace', clientId]);
      }
    }
}
