import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  content?: string;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getPublicContent().subscribe({
      next: data => {
        this.content = data;
      },
      error: err => {
        if (err.error) {
          try {
            const res = JSON.parse(err.error);
            this.content = res.message;
          } catch {
            this.content = `Error with status: ${err.status} - ${err.statusText}`;
          }
        } else {
          this.content = `Error with status: ${err.status}`;
        }
      }
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.uploadImage(file);
    }
  }

  uploadImage(imageFile: File) {
    const formData = new FormData();
    formData.append('image', imageFile, imageFile.name);
  
    // Replace 'YOUR_BACKEND_ENDPOINT' with your actual backend API endpoint
    // this.http.post('YOUR_BACKEND_ENDPOINT', formData).subscribe(
    //   (response) => {
    //     console.log('Image uploaded successfully!', response);
    //     // Handle response from the server
    //   },
    //   (error) => {
    //     console.error('Error uploading image:', error);
    //     // Handle error
    //   }
    // );
  }
}
