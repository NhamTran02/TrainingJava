import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { UserService } from '../../services/user.service';
import { User, UserUpdateRequest, ChangePasswordRequest } from '../../models/user';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  loading = true;
  activeTab: 'info' | 'password' = 'info';
  
  // Form data
  editMode = false;
  editForm: UserUpdateRequest = {
    fullName: '',
    phoneNumber: '',
    address: ''
  };

  passwordForm: ChangePasswordRequest = {
    username: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  private getUserId(): number | null {
    const user = localStorage.getItem('currentUser');
    if (user) {
      try {
        const userData = JSON.parse(user);
        return userData.id;
      } catch {
        return null;
      }
    }
    return null;
  }

  loadUserProfile(): void {
    const userId = this.getUserId();
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.userService.getUserById(userId).subscribe({
      next: (user) => {
        this.user = user;
        this.editForm = {
          fullName: user.fullName || '',
          phoneNumber: user.phoneNumber || '',
          address: user.address || ''
        };
        this.passwordForm.username = user.username;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.loading = false;
        Swal.fire({
          title: 'Lỗi!',
          text: 'Không thể tải thông tin tài khoản',
          icon: 'error',
          confirmButtonColor: '#EF4444'
        });
      }
    });
  }

  toggleEditMode(): void {
    if (this.editMode) {
      // Cancel edit
      if (this.user) {
        this.editForm = {
          fullName: this.user.fullName || '',
          phoneNumber: this.user.phoneNumber || '',
          address: this.user.address || ''
        };
      }
    }
    this.editMode = !this.editMode;
  }

  saveProfile(): void {
    if (!this.user) return;

    // Validation cơ bản
    if (!this.editForm.fullName || !this.editForm.phoneNumber || !this.editForm.address) {
      Swal.fire({
        title: 'Thiếu thông tin',
        text: 'Vui lòng điền đầy đủ thông tin',
        icon: 'warning',
        confirmButtonColor: '#F97316'
      });
      return;
    }

    // Validation số điện thoại
    const phoneRegex = /^0[0-9]{9}$/;
    if (!phoneRegex.test(this.editForm.phoneNumber)) {
      Swal.fire({
        title: 'Lỗi!',
        text: 'Số điện thoại không hợp lệ. Vui lòng nhập đúng định dạng (0xxxxxxxxx)',
        icon: 'error',
        confirmButtonColor: '#EF4444'
      });
      return;
    }

    this.userService.updateUser(this.user.id, this.editForm).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.editMode = false;
        
        Swal.fire({
          title: 'Thành công!',
          text: 'Cập nhật thông tin thành công',
          icon: 'success',
          timer: 1500,
          showConfirmButton: false
        });
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        Swal.fire({
          title: 'Lỗi!',
          text: error.error?.message || 'Không thể cập nhật thông tin',
          icon: 'error',
          confirmButtonColor: '#EF4444'
        });
      }
    });
  }

  changePassword(): void {
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      Swal.fire({
        title: 'Lỗi!',
        text: 'Mật khẩu mới không khớp',
        icon: 'error',
        confirmButtonColor: '#EF4444'
      });
      return;
    }

    if (this.passwordForm.newPassword.length < 6) {
      Swal.fire({
        title: 'Lỗi!',
        text: 'Mật khẩu mới phải có ít nhất 6 ký tự',
        icon: 'error',
        confirmButtonColor: '#EF4444'
      });
      return;
    }

    if (!this.passwordForm.currentPassword) {
      Swal.fire({
        title: 'Lỗi!',
        text: 'Vui lòng nhập mật khẩu hiện tại',
        icon: 'error',
        confirmButtonColor: '#EF4444'
      });
      return;
    }

    console.log('Change password request:', {
      username: this.passwordForm.username,
      hasCurrentPassword: !!this.passwordForm.currentPassword,
      hasNewPassword: !!this.passwordForm.newPassword,
      hasConfirmPassword: !!this.passwordForm.confirmPassword
    });

    this.userService.changePassword(this.passwordForm).subscribe({
      next: (response) => {
        console.log('Change password response:', response);
        
        Swal.fire({
          title: 'Thành công!',
          text: 'Đổi mật khẩu thành công. Vui lòng đăng nhập lại.',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        }).then(() => {
          // Logout và redirect về trang login
          localStorage.removeItem('currentUser');
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          this.router.navigate(['/login']);
        });
      },
      error: (error) => {
        console.error('Error changing password:', error);
        console.error('Error details:', error.error);
        
        let errorMessage = 'Không thể đổi mật khẩu';
        
        if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.status === 401) {
          errorMessage = 'Mật khẩu hiện tại không đúng';
        } else if (error.status === 403) {
          errorMessage = 'Bạn không có quyền thực hiện thao tác này';
        }
        
        Swal.fire({
          title: 'Lỗi!',
          text: errorMessage,
          icon: 'error',
          confirmButtonColor: '#EF4444'
        });
      }
    });
  }

  switchTab(tab: 'info' | 'password'): void {
    this.activeTab = tab;
    if (this.editMode) {
      this.toggleEditMode();
    }
  }
}
