import { Component, NgModule, OnInit } from '@angular/core';
import { ClientsService } from 'src/app/_services/clients.service';
import { SharedServiceService } from 'src/app/_services/shared.service';
import { Lead } from 'src/entities/Lead';


@Component({
  selector: 'app-leads',
  templateUrl: './leads.component.html',
  styleUrls: ['./leads.component.css']
})

export class LeadsComponent implements OnInit{
  leads!: Lead[]
  isSuccessLoad = false;
  isSuccessDelete = false
  responseMessage = '';
  errorMessage = '';
  isError = false;
  
  constructor(private clientService: ClientsService,
              private sharedService: SharedServiceService) {}
  
    ngOnInit(): void {
      this.refreshListOfLeads();
    }
    refreshListOfLeads(){
      this.clientService.getListOfLids().subscribe({
        next: data => {
          this.leads = data;
          this.isSuccessLoad = true;
        },
        error: (err: any) => {
          console.error(err); 
          this.isError = true;
          this.errorMessage = 'Error loading data';
        }
      })
    }
    sentClientToBlackList(id: number) {
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
        }
      });
    }
    updateActiveClient(id : Number) {
      this.sharedService.activeLid = this.leads.find((lid) => lid.id === id);
    } 
    reloadPage(delay: number): void {
      setTimeout(() => {
        this.isSuccessDelete = false;
        this.refreshListOfLeads();
      }, delay); 
    }
}
