import { AfterViewChecked, AfterViewInit, Component, ElementRef, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { SharedServiceService } from '../_services/shared.service';
import { HistoryMessage } from '../../entities/HistoryMessage';
import { UserService } from '../_services/user.service';
import { StorageService } from '../_services/storage.service';
import { BehaviorSubject, Subscription, combineLatest } from 'rxjs';
// import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class SideBarComponent implements OnInit{


    isDropdownActionVisible = true;
    isDropdownHistoryVisible = true;
    history$: BehaviorSubject<HistoryMessage[]> = new BehaviorSubject<HistoryMessage[]>([]);
    history: HistoryMessage[] = [];
    filteredHistory?: HistoryMessage[];
    activeClientId?: number;
    private historySubscription: Subscription;
    content?: string;

  constructor(private sharedService: SharedServiceService,
      private userService: UserService,
      private storageService: StorageService,
      // private _snackBar: MatSnackBar
      ) {
        this.historySubscription = this.storageService.history$.subscribe((userHistory: HistoryMessage[]) => {
          this.history = userHistory;
        });
  }


  ngOnInit(){

    this.storageService.history$.subscribe((history: HistoryMessage[]) => {
      this.history = history;
      this.filteredHistory = this.history.sort((a, b) => b.dateOfCreation.getTime() - a.dateOfCreation.getTime());
    });
    this.storageService.activeClientId$.subscribe((id: number) => {
      this.activeClientId = id;
    });

    console.log('message from side-bar - onInit')
  }

  showAllHistory(){
    this.filteredHistory = this.history;
  }
  showActiveClientHistory(){
    this.filteredHistory = this.history?.filter(message => message.lidId === this.activeClientId);
    console.log(this.activeClientId);
  }
  refreshHistory(){
    this.userService.getHistory().subscribe({
      next: data => {
        this.updateHistory(data);
        console.log('message from log');
      },
      error: err => {
        console.log(err.toString());
      }
    });
  }
  updateHistory(newHistory: HistoryMessage[]) {
    this.storageService.setHistory(newHistory);
  }
  // openSnackBar(message: string, action: string) {
  //   this._snackBar.open(message, action, {
  //     duration: 2000, // Duration in milliseconds for the toast to be shown
  //   });
  // }

  // showToast() {
  //   this.openSnackBar('Hello, world!', 'Close');
  // }
}
