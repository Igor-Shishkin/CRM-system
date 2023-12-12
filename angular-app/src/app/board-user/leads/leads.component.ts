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
  isLoaded = false;
  responseMessage = '';
  errorMessage = '';
  isError = false;
  
  constructor(private clientService: ClientsService,
              private sharedService: SharedServiceService) {}
  
    ngOnInit(): void {
      this.clientService.getListOfLids().subscribe({
        next: data => {
          this.leads = data;
          this.isLoaded = true;
        },
        error: (err: any) => {
          console.error(err); 
        }
      })
    }
    deleteLid( id : number)
    {
      this.clientService.deleteLidById(id).subscribe({
        next: (data: string) => {
          this.responseMessage = data;
        },
        error: (err: any) => {
          console.error(err);
          this.isError = true;
          if (err instanceof Object) {
            this.errorMessage = JSON.stringify(err);
          } else {
            this.errorMessage = err.toString();
          }
        }
      });
    }
    updateActiveLid(id : Number) {
      this.sharedService.activeLid = this.leads.find((lid) => lid.id === id);
    } 
}
