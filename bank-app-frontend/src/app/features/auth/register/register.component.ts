import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  error: string = '';
  loading: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    // If user is already logged in, redirect to home
    if (this.authService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      console.log('Submitting registration form:', this.registerForm.value);
      this.error = '';
      this.loading = true;
      
      this.authService.register(this.registerForm.value).subscribe({
        next: (response) => {
          console.log('Registration successful:', response);
          // Success message and redirect to login page
          this.router.navigate(['/login'], { 
            queryParams: { 
              registered: 'true',
              username: this.registerForm.value.username 
            }
          });
        },
        error: (error) => {
          console.error('Registration error:', error);
          this.error = error.error || 'Registration failed. Please try again.';
          this.loading = false;
        }
      });
    } else {
      this.error = 'Please correct the form errors.';
    }
  }
}
