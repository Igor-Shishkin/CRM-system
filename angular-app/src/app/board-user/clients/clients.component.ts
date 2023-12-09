import { Component } from '@angular/core';
import { Lid } from 'src/app/Lid';
import { LidsService } from 'src/app/_services/lids.service';
import { SharedServiceService } from 'src/app/_services/shared.service';

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.css']
})
export class ClientsComponent {
  clients!: Lid[]
  isLoaded = false;
  responseMessage = '';
  errorMessage = '';
  isError = false;
  
  constructor(private lidService: LidsService,
              private sharedService: SharedServiceService) {}
  
    ngOnInit(): void {
      this.lidService.getListOfClients().subscribe({
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
      this.lidService.deleteLidById(id).subscribe({
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
  