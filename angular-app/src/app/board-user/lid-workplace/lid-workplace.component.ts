import { Component, OnInit } from '@angular/core';
import { Lead } from 'src/entities/Lead';
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

