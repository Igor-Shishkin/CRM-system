import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  form: any = {
    username: null,
    email: null,
    role: [],
    password: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';


  constructor(private authService: AuthService) { }

  onSubmit(): void {
      console.log(this.form.role);
      console.log(this.form);
      const { username, email, role, password } = this.form;

      this.authService.register(username, email, role, password).subscribe({
        next: data => {
          console.log(data);
          this.isSuccessful = true;
          this.isSignUpFailed = false;
        },
        error: err => {
          console.log(err);
          this.errorMessage = err.error.message;
          this.isSignUpFailed = true;
        }
      });
    
  }

  updateRole(role: string) {
    const roleIndex = this.form.role.indexOf(role);
    if (roleIndex !== -1) {
      this.form.role.splice(roleIndex, 1);
    } else {
      this.form.role.push(role);
    }
    console.log(this.form.role)
  }
}
