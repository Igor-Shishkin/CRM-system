import { Component, OnInit } from '@angular/core';
import { Routes } from '@angular/router';
import { LidsService } from 'src/app/_services/lids.service';
import { SharedServiceService } from 'src/app/_services/shared.service';
import { Lid } from 'src/app/Lid';

@Component({
  selector: 'app-lids',
  templateUrl: './lids.component.html',
  styleUrls: ['./lids.component.css'],
  template: `
  <p>{{ sharedService.commonVariable }}</p>
`,
})

export class LidsComponent implements OnInit{
lids!: Lid[]
isLoaded = false;
responseMessage = '';
errorMessage = '';
isError = false;

constructor(private lidService: LidsService,
            private sharedService: SharedServiceService) {}

  ngOnInit(): void {
    this.lidService.getListOfClients().subscribe({
      next: data => {
        this.lids = data;
        this.isLoaded = true;
      },
      error: (err: any) => {
        console.error(err); // Log the error for debugging
        // Handle error display or any other actions as needed
      }
    })
  }
  deleteLid( id : number)
  {
    this.lidService.deleteLidById(id).subscribe(
      (data: string) => {
        this.responseMessage = data;
      },
      (err: any) => {
        console.error(err);
        this.isError = true;
        this.errorMessage = err;

      }
    );
  }
  editLid(  id : number, name : string, surname : string, email : string, phoneNumber : string)
  {
    this.lidService.editLidData(id, name, surname, email, phoneNumber)
  }

  updateActiveLid(id : Number) {
    this.sharedService.activeLid = this.lids.find((lid) => lid.id === id);
  }
  
}
