
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/_services/auth.service';


@Component({
  selector: 'app-register-new-user',
  templateUrl: './register-new-user.component.html',
  styleUrls: ['./register-new-user.component.css']
})
export class RegisterNewUserComponent {
  form: any = {
    username: null,
    email: null,
    role: [],
    password: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';
  isProcess = false;


  constructor(private authService: AuthService,
      private router: Router) { }

  onSubmit(): void {
      const { username, email, role, password } = this.form;
      this.isProcess = true;
      this.authService.register(username, email, role, password).subscribe({
        next: data => {
          console.log(data);
          this.isSuccessful = true;
          this.isSignUpFailed = false;
          this.isProcess = false;
          this.delayGoToListOfUsers();
        },
        error: err => {
          console.log(err);
          this.errorMessage = err.error.message;
          this.isSignUpFailed = true;
          this.isProcess = false;
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
  delayGoToListOfUsers() {
    setTimeout(() => {
      this.router.navigate(['/admin/users']);
    }, 2500)
  }
}
