import { Component, OnInit } from '@angular/core';
import { Routes } from '@angular/router';
import { LidsService } from 'src/app/_services/leads.service';
import { SharedServiceService } from 'src/app/_services/shared.service';
import { Lead } from 'src/app/Lid';

@Component({
  selector: 'app-lids',
  templateUrl: './lids.component.html',
  styleUrls: ['./lids.component.css'],
  template: `
  <p>{{ sharedService.commonVariable }}</p>
`,
})

export class LidsComponent implements OnInit{
lids!: Lead[]
isLoaded = false;
responseMessage = '';
errorMessage = '';
isError = false;

constructor(private lidService: LidsService,
            private sharedService: SharedServiceService) {}

  ngOnInit(): void {
    this.lidService.getListOfLids().subscribe({
      next: data => {
        this.lids = data;
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
        if (err instanceof Object) {
          this.errorMessage = JSON.stringify(err);
        } else {
          this.errorMessage = err.toString();
        }
      }
    });
  }
  updateActiveLid(id : Number) {
    this.sharedService.activeLid = this.lids.find((lid) => lid.id === id);
  } 
}
