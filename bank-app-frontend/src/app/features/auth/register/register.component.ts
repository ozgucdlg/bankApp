import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

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
    private notificationService: NotificationService,
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
          
          // Send notification to admin about new user registration
          this.sendNewUserNotification(this.registerForm.value.username, this.registerForm.value.email);
          
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
  
  private sendNewUserNotification(username: string, email: string): void {
    console.log('Sending new user registration notification');
    
    // Send notification to admin
    this.notificationService.sendNotification(
      'admin', // assuming admin is the recipient
      'New User Registration',
      `A new user has registered with username: ${username} and email: ${email}.`
    ).subscribe({
      next: () => {
        console.log('Registration notification sent successfully to admin');
      },
      error: (error) => {
        console.error('Error sending registration notification to admin:', error);
        // Don't block the registration process if notification fails
      }
    });
    
    // Send welcome notification to the new user
    this.notificationService.sendNotification(
      username, // the newly registered user
      'Welcome to Secure Bank!',
      `Thank you for registering with Secure Bank. Your account has been created successfully. You can now log in and use all of our banking services. If you have any questions, please contact our support team.`
    ).subscribe({
      next: () => {
        console.log('Welcome notification sent successfully to new user');
      },
      error: (error) => {
        console.error('Error sending welcome notification to new user:', error);
        // Don't block the registration process if notification fails
      }
    });
  }
}
