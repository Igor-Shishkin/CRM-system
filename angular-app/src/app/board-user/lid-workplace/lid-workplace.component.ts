import { Component, OnInit } from '@angular/core';
import { Lead } from 'src/app/Lead';
import { LidsService } from 'src/app/_services/leads.service';
import { SharedServiceService } from 'src/app/_services/shared.service';

@Component({
  selector: 'app-lid-workplace',
  templateUrl: './lid-workplace.component.html',
  styleUrls: ['./lid-workplace.component.css']
})
export class LidWorkplaceComponent implements OnInit{
  lid?: Lead;

  constructor(private sharedService : SharedServiceService) {}

  ngOnInit(): void {
    this.lid = this.sharedService.activeLid
  }

}

