import { Component, OnInit } from '@angular/core';
import { Client } from 'src/entities/Client';
import { SharedServiceService } from 'src/app/_services/shared.service';

@Component({
  selector: 'app-lid-workplace',
  templateUrl: './lid-workplace.component.html',
  styleUrls: ['./lid-workplace.component.css']
})
export class LidWorkplaceComponent implements OnInit{
  lid?: Client;

  constructor(private sharedService : SharedServiceService) {}

  ngOnInit(): void {
    this.lid = this.sharedService.activeLid
  }

}

