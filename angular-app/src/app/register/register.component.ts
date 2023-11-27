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
  roles: string[] = ['User', 'Moderator', 'Admin'];
  selectedRole: string | null = null;
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService) { }

  onSubmit(): void {
    if (this.selectedRole != null) {
      this.form.role.push(this.selectedRole)
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
          this.errorMessage = err.error.message;
          this.isSignUpFailed = true;
        }
      });
    }
  }
  selectRole(role: string){
    this.selectedRole = role;
    console.log(this.selectedRole)
  };
}
