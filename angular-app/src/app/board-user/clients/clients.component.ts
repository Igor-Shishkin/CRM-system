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
  isLoaded = false;
  responseMessage = '';
  errorMessage = '';
  isError = false;
  
  constructor(private clientService: ClientsService,
              private sharedService: SharedServiceService) {}
  
    ngOnInit(): void {
      this.clientService.getListOfClients().subscribe({
        next: data => {
          this.clients = data;
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
          this.errorMessage = err;
        }
      });
    }
    updateActiveLid(id : Number) {
      this.sharedService.activeLid = this.clients.find((lid) => lid.id === id);
    }
    
  }
  