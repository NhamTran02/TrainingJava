import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { User, UserUpdateRequest } from '../../../models/user';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchTerm = '';
  selectedUser: User | null = null;
  showEditModal = false;

  editForm = {
    fullName: '',
    email: '',
    phoneNumber: '',
    address: ''
  };

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        Swal.fire('Lỗi', 'Không thể tải danh sách người dùng', 'error');
        this.loading = false;
      }
    });
  }

  filterUsers(): void {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredUsers = this.users;
      return;
    }

    this.filteredUsers = this.users.filter(user =>
      user.username?.toLowerCase().includes(term) ||
      user.email?.toLowerCase().includes(term) ||
      user.fullName?.toLowerCase().includes(term) ||
      user.phoneNumber?.includes(term)
    );
  }

  openEditModal(user: User): void {
    this.selectedUser = user;
    this.editForm = {
      fullName: user.fullName || '',
      email: user.email || '',
      phoneNumber: user.phoneNumber || '',
      address: user.address || ''
    };
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedUser = null;
  }

  saveUser(): void {
    if (!this.selectedUser) return;

    this.userService.updateUser(this.selectedUser.id, this.editForm).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.id === updatedUser.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
          this.filterUsers();
        }
        Swal.fire('Thành công', 'Cập nhật người dùng thành công', 'success');
        this.closeEditModal();
      },
      error: (error) => {
        console.error('Error updating user:', error);
        Swal.fire('Lỗi', error.error?.message || 'Không thể cập nhật người dùng', 'error');
      }
    });
  }

  deleteUser(user: User): void {
    Swal.fire({
      title: 'Xác nhận xóa',
      text: `Bạn có chắc muốn xóa người dùng "${user.username}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            // Update local state
            const index = this.users.findIndex(u => u.id === user.id);
            if (index !== -1) {
              this.users[index].deleted = true;
              this.filterUsers();
            }
            Swal.fire('Đã xóa', 'Người dùng đã được xóa', 'success');
          },
          error: (error) => {
            console.error('Error deleting user:', error);
            Swal.fire('Lỗi', error.error?.message || 'Không thể xóa người dùng', 'error');
          }
        });
      }
    });
  }

  activateUser(user: User): void {
    Swal.fire({
      title: 'Xác nhận kích hoạt',
      text: `Bạn có chắc muốn kích hoạt lại người dùng "${user.username}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#10b981',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Kích hoạt',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        const updateData: UserUpdateRequest = {
          fullName: user.fullName || '',
          phoneNumber: user.phoneNumber || '',
          address: user.address || '',
          deleted: false
        };

        this.userService.updateUser(user.id, updateData).subscribe({
          next: (updatedUser) => {
            const index = this.users.findIndex(u => u.id === updatedUser.id);
            if (index !== -1) {
              this.users[index] = updatedUser;
              this.filterUsers();
            }
            Swal.fire('Đã kích hoạt', 'Người dùng đã được kích hoạt lại', 'success');
          },
          error: (error) => {
            console.error('Error activating user:', error);
            Swal.fire('Lỗi', error.error?.message || 'Không thể kích hoạt người dùng', 'error');
          }
        });
      }
    });
  }

  getRoleBadgeClass(roleNames?: string[]): string {
    if (!roleNames || roleNames.length === 0) return 'bg-gray-100 text-gray-800';
    if (roleNames.includes('ADMIN')) return 'bg-purple-100 text-purple-800';
    return 'bg-blue-100 text-blue-800';
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}
