import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      fullName: [''],
      phoneNumber: [''],
      address: ['']
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const registerData = {
      ...this.registerForm.value,
      roleIds: [2] // USER role
    };

    this.authService.register(registerData).subscribe({
      next: (response) => {
        if (response.code === 200) {
          this.successMessage = response.message || 'Đăng ký thành công! Đang chuyển đến trang đăng nhập...';
          setTimeout(() => this.router.navigate(['/login']), 2000);
        } else {
          this.errorMessage = response.message || 'Đăng ký thất bại';
          this.loading = false;
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.message || error.message || 'Đăng ký thất bại. Vui lòng thử lại.';
        this.loading = false;
      }
    });
  }
}
