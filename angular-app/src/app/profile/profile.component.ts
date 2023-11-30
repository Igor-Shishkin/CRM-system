import { Component, OnInit } from '@angular/core';
import { StorageService } from '../_services/storage.service';
import { AuthService } from '../_services/auth.service';

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

  constructor(private storageService: StorageService, private authService: AuthService) { }
  

  ngOnInit(): void {
    this.currentUser = this.storageService.getUser();
    this.authService.getUserPhoto().subscribe({
      next: (imageData: Blob) => {
        this.userPhoto = imageData;
        this.statusMessage = "Photo is loaded :)"
      },
      error: (error) => {
        console.error('Error fetching image:', error);
        this.statusMessage = "Something has gone wrong"
      }
    });
  }
  getImageUrl() {
    return this.userPhoto ? window.URL.createObjectURL(this.userPhoto) : '';
  }
  // createImageFromBloB(image: Blob): void {
  //   const reader = new FileReader();
  //   reader.addEventListener('load', () => {
  //     this.photo = new Image();
  //     if (typeof reader.result === 'string') {
  //       this.photo.src = reader.result;
  //     }
  //   }, false);
    
  //   if (image) {
  //     reader.readAsDataURL(image);
  //   }
  // }
  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }
  uploadImage() {
    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile, this.selectedFile.name);

      this.authService.uploadPhoto(formData).subscribe({
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
