import { Component } from '@angular/core';
import { Lead } from 'src/entities/Lead';
import { ClientsService } from 'src/app/_services/clients.service';
import { SharedServiceService } from 'src/app/_services/shared.service';

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.css']
})
export class ClientsComponent {
  clients!: Lead[]
  isSuccessLoad = false;
  isSuccessDelete = false
  responseMessage = '';
  errorMessage = '';
  isError = false;
  
  constructor(private clientService: ClientsService,
              private sharedService: SharedServiceService) {}
  
    ngOnInit(): void {
      this.refreshListOfLeads;
    }
    sentClientToBlackList( id : number)
    {
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
        }
      });
    }
    refreshListOfLeads(){
      this.clientService.getListOfClients().subscribe({
        next: data => {
          this.clients = data;
          this.isSuccessLoad = true;
        },
        error: (err: any) => {
          console.error(err); 
          this.isError = true;
          this.errorMessage = 'Error loading data';
        }
      })
    }
    updateActiveClient(id : Number) {
      this.sharedService.activeLid = this.clients.find((lid) => lid.id === id);
    }
    reloadPage(delay: number): void {
      setTimeout(() => {
        this.isSuccessDelete = false;
        this.refreshListOfLeads;
      }, delay); 
    }
  }
  