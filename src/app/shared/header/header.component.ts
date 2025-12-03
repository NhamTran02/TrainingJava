import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { User } from '../../models/user';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  currentUser: User | null = null;
  showUserMenu = false;
  showMobileMenu = false;
  cartItemCount = 0;
  searchKeyword = '';

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.cartService.cartItemCount$.subscribe(count => {
      this.cartItemCount = count;
    });
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }

  toggleMobileMenu(): void {
    this.showMobileMenu = !this.showMobileMenu;
  }

  closeMobileMenu(): void {
    this.showMobileMenu = false;
  }

  logout(): void {
    this.authService.logout();
    this.showUserMenu = false;
    this.router.navigate(['/login']);
  }

  goToCart(): void {
    if (!this.currentUser || !localStorage.getItem('accessToken')) {
      Swal.fire({
        title: 'Bạn chưa đăng nhập!',
        text: 'Vui lòng đăng nhập để xem giỏ hàng.',
        icon: 'warning',
        confirmButtonText: 'Đăng nhập ngay',
        confirmButtonColor: '#EE4D2D',
        allowOutsideClick: true,
        showCancelButton: false,
        backdrop: true,
        customClass: {
          popup: 'rounded-xl shadow-lg'
        }
      }).then((result) => {
        if(result.isConfirmed){
          this.router.navigate(['/login'], { 
            queryParams: { returnUrl: '/cart' }
          });
        }
      });
      return;
    }
    this.router.navigate(['/cart']);
  }

  onSearch(event?: Event): void {
    if (event) {
      event.preventDefault();
    }
    
    if (this.searchKeyword.trim()) {
      this.router.navigate(['/search'], {
        queryParams: { q: this.searchKeyword.trim() }
      });
      this.closeMobileMenu();
    }
  }

  onSearchKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.onSearch();
    }
  }
}
