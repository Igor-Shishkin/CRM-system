import { Component, OnInit } from '@angular/core';
import { StorageService } from '../_services/storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  currentUser: any;
  userPhoto: Blob | undefined;
  selectedFile: File | undefined;
  statusMessage = '';

  constructor(private storageService: StorageService, private userService: UserService) { }
  

  ngOnInit(): void {
    this.currentUser = this.storageService.getUser();
    this.userService.getUserPhoto().subscribe({
      next: (imageData: Blob) => {
        this.userPhoto = imageData;
        this.statusMessage = "Photo is loaded :)"
      },
      error: (error: any) => {
        console.error('Error fetching image:', error);
        this.statusMessage = "Something has gone wrong"
      }
    });
  }
  getImageUrl() {
    return this.userPhoto ? window.URL.createObjectURL(this.userPhoto) : '';
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }
  uploadImage() {
    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile, this.selectedFile.name);

      this.userService.uploadPhoto(formData).subscribe({
        next: (response: any) => {
          this.statusMessage = response;
          console.log('Image uploaded successfully!', response);
        },
        error: (error: any) => {
          this.statusMessage = error;
          console.error(error);
        }
      })
    }
  }
}
