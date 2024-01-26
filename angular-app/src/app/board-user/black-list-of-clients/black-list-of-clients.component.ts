import { Component } from '@angular/core';
import { ClientsService } from 'src/app/_services/clients.service';
import { Client } from 'src/entities/Client';

@Component({
  selector: 'app-black-list-of-clients',
  templateUrl: './black-list-of-clients.component.html',
  styleUrls: ['./black-list-of-clients.component.css']
})
export class BlackListOfClientsComponent {
  clients!: Client[]
  isSuccessRequest = false;
  isFailedRequest = false;
  isRequestSent = false;
  isSuccessLoad = true;
  
  
  constructor(private clientService: ClientsService) {}
  
    ngOnInit(): void {
      this.refreshListOfClients();
    }

    refreshListOfClients(){
      this.clientService.getListOfClientsFromBlackList().subscribe({
        next: data => {
          this.clients = data;
          this.isSuccessLoad = true;
        },
        error: (err: any) => {
          console.error(err); 
          this.isSuccessLoad = false;
        }
      })
    }
    restoreClientFromBlacklist(client: Client) {
      this.isRequestSent = true;
      this.clientService.restoreClientToBlackList(client.id || -1).subscribe({
        next: () => {
          this.isSuccessRequest = true;
          this.isRequestSent = false;
          client.status = 'CLIENT'
          this.delayHiderMessages();
        }, error: (err) => {
          console.error(err);
          this.isFailedRequest = true;
          this.delayHiderMessages();
        } 
      })
    }
    delayHiderMessages() {
      setTimeout(() => {
        this.isFailedRequest = false;
        this.isSuccessRequest = false;
      }, 5000)
    }
  }
  