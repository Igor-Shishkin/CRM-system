import { Component, OnInit } from '@angular/core';
import { Routes } from '@angular/router';
import { LidsService } from 'src/app/_services/lids.service';
import { Lid } from 'src/app/Lid';

@Component({
  selector: 'app-lids',
  templateUrl: './lids.component.html',
  styleUrls: ['./lids.component.css']
})

export class LidsComponent implements OnInit{
content?: Lid[]
isError = false;
stringError = '';

constructor(private lidService: LidsService) {}

  ngOnInit(): void {
    this.lidService.getListOfClients().subscribe({
      next: data => {
        this.content = data;
      },
      error: (err: any) => {
        this.isError = true;
        console.error(err); // Log the error for debugging
        // Handle error display or any other actions as needed
      }
    })
  }

}
