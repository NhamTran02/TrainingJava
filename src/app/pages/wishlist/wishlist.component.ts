import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { WishlistService } from '../../services/wishlist.service';
import { Product } from '../../models/product';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css']
})
export class WishlistComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  userId: number | null = null;

  constructor(
    private wishlistService: WishlistService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = this.getUserId();
    if (this.userId) {
      this.loadWishlist();
    } else {
      this.router.navigate(['/login']);
    }
  }

  private getUserId(): number | null {
    const user = localStorage.getItem('currentUser');
    if (user) {
      try {
        return JSON.parse(user).id;
      } catch {
        return null;
      }
    }
    return null;
  }

  loadWishlist(): void {
    if (!this.userId) return;
    
    this.loading = true;
    this.wishlistService.getWishlist(this.userId).subscribe({
      next: (products) => {
        this.products = this.processProducts(products);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading wishlist:', error);
        this.loading = false;
      }
    });
  }

  private processProducts(products: Product[]): Product[] {
    return products.map(product => {
      if (product.variants && product.variants.length > 0) {
        const firstVariant = product.variants[0];
        product.regularPrice = firstVariant.regularPrice;
        product.salePrice = firstVariant.salePrice;
      }
      if (product.images && product.images.length > 0) {
        const thumbnail = product.images.find(img => img.isThumbnail);
        product.imageUrl = thumbnail ? thumbnail.url : product.images[0].url;
      }
      return product;
    });
  }

  removeFromWishlist(product: Product): void {
    if (!this.userId) return;

    Swal.fire({
      title: 'Xóa khỏi yêu thích?',
      text: `Bạn có chắc muốn xóa "${product.name}" khỏi danh sách yêu thích?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed && this.userId) {
        this.wishlistService.removeFromWishlist(this.userId, product.id).subscribe({
          next: () => {
            this.loadWishlist();
            Swal.fire({
              title: 'Đã xóa!',
              text: 'Sản phẩm đã được xóa khỏi danh sách yêu thích',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false
            });
          },
          error: (error) => {
            console.error('Error removing from wishlist:', error);
            Swal.fire({
              title: 'Lỗi!',
              text: 'Không thể xóa sản phẩm',
              icon: 'error',
              confirmButtonColor: '#EF4444'
            });
          }
        });
      }
    });
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/product', productId]);
  }
}
