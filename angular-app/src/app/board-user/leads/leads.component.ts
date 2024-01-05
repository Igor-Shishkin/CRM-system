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
  isSuccessDelete = false
  responseMessage = '';
  errorMessage = '';
  isError = false;
  isRequestSent = false;

  constructor(private clientService: ClientsService,
              private router: Router,
              private storageService: StorageService) {}
  
    ngOnInit(): void {
      this.refreshListOfLeads();
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
    sentClientToBlackList(id: number) {
      this.isRequestSent = true;
      this.clientService.sentClientToBlackList(id).subscribe({
        next: (data: any) => { // Specify the type of 'data' as string
          this.responseMessage = data;
          this.isSuccessDelete = true;
          this.reloadPage(1500);
        },
        error: (err: any) => {
          console.error(err);
          this.isError = true;
          this.errorMessage = 'Error deleting Lead';
          this.isRequestSent = false;
        }
      });
    } 
    reloadPage(delay: number): void {
      setTimeout(() => { 
        this.isSuccessDelete = false;
        this.refreshListOfLeads();
      }, delay); 
    }
    goToClientDetail(clientId: number) {
      this.storageService.setActiveClientId(clientId);
      this.router.navigate(['/user-board/client-workplace', clientId]);
    }
}
