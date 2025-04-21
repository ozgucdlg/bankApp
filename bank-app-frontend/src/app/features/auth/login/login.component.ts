import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  error: string = '';
  success: string = '';
  loading: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Check query parameters
    this.route.queryParams.subscribe(params => {
      // Check if user was logged out
      if (params['logout'] === 'true') {
        this.success = 'You have been successfully logged out';
      }
      
      // Check if user just registered
      if (params['registered'] === 'true') {
        const username = params['username'] || '';
        this.success = 'Registration successful! Please log in with your credentials.';
        
        // Pre-fill the username field if available
        if (username) {
          this.loginForm.patchValue({ username });
        }
      }
    });

    // If user is already logged in, redirect to dashboard
    if (this.authService.currentUserValue) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;
      console.log('Attempting login for user:', username);
      
      this.loading = true;
      this.error = '';
      
      this.authService.login(username, password).subscribe({
        next: (response) => {
          console.log('Login successful:', response);
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          console.error('Login failed details:', error);
          this.error = error.error || 'Invalid username or password';
          this.loading = false;
          
          // Additional debugging info
          if (error.status === 0) {
            this.error = 'Server connection error. Please check if the backend is running.';
          } else if (error.status === 401 || error.status === 403) {
            this.error = 'Authentication failed. Please check your credentials.';
          }
        }
      });
    }
  }
}
