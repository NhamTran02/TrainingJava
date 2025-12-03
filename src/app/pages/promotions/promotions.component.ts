import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { ProductService } from '../../services/product.service';
import { WishlistService } from '../../services/wishlist.service';
import { Product } from '../../models/product';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-promotions',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './promotions.component.html',
  styleUrl: './promotions.component.css'
})
export class PromotionsComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  loading = true;
  wishlistProductIds: Set<number> = new Set();
  
  // Countdown timer
  countdown = {
    days: 0,
    hours: 0,
    minutes: 0,
    seconds: 0
  };
  private countdownInterval: any;

  constructor(
    private productService: ProductService,
    private wishlistService: WishlistService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadWishlist();
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  private startCountdown(): void {
    // Tính thời gian kết thúc (3 ngày từ bây giờ)
    const getEndTime = () => {
      const now = new Date().getTime();
      return now + (3 * 24 * 60 * 60 * 1000); // 3 ngày
    };

    let endTime = getEndTime();

    this.countdownInterval = setInterval(() => {
      const now = new Date().getTime();
      const distance = endTime - now;

      if (distance < 0) {
        // Reset countdown khi hết thời gian
        endTime = getEndTime();
        return;
      }

      // Tính toán thời gian còn lại
      this.countdown.days = Math.floor(distance / (1000 * 60 * 60 * 24));
      this.countdown.hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      this.countdown.minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      this.countdown.seconds = Math.floor((distance % (1000 * 60)) / 1000);
    }, 1000);
  }

  loadProducts(): void {
    this.loading = true;
    // Load tất cả sản phẩm và filter những sản phẩm có giảm giá
    this.productService.getAllProducts(0, 100).subscribe({
      next: (response) => {
        // Lọc sản phẩm có giảm giá
        this.products = response.content.filter(product => this.hasDiscount(product));
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.loading = false;
      }
    });
  }

  loadWishlist(): void {
    const userId = this.getUserId();
    if (!userId) return;

    this.wishlistService.getWishlist(userId).subscribe({
      next: (products) => {
        this.wishlistProductIds = new Set(products.map(p => p.id));
      },
      error: (error) => {
        console.error('Error loading wishlist:', error);
      }
    });
  }

  hasDiscount(product: Product): boolean {
    if (!product.variants || product.variants.length === 0) return false;
    return product.variants.some(v => v.salePrice < v.regularPrice);
  }

  getDiscountPercentage(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    const minSalePrice = Math.min(...product.variants.map(v => v.salePrice));
    const minRegularPrice = Math.min(...product.variants.map(v => v.regularPrice));
    if (minRegularPrice === 0) return 0;
    return Math.round(((minRegularPrice - minSalePrice) / minRegularPrice) * 100);
  }

  getThumbnailImage(product: Product): string | null {
    if (!product.images || product.images.length === 0) return null;
    const thumbnail = product.images.find(img => img.isThumbnail);
    return thumbnail ? thumbnail.url : product.images[0].url;
  }

  getMinPrice(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    return Math.min(...product.variants.map(v => v.salePrice));
  }

  getMinRegularPrice(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    return Math.min(...product.variants.map(v => v.regularPrice));
  }

  isInWishlist(productId: number): boolean {
    return this.wishlistProductIds.has(productId);
  }

  toggleWishlist(event: Event, productId: number): void {
    event.stopPropagation();
    
    const userId = this.getUserId();
    if (!userId) {
      Swal.fire({
        title: 'Bạn chưa đăng nhập!',
        text: 'Vui lòng đăng nhập để thêm sản phẩm vào danh sách yêu thích.',
        icon: 'warning',
        confirmButtonText: 'Đăng nhập ngay',
        confirmButtonColor: '#EE4D2D',
        showCancelButton: true,
        cancelButtonText: 'Huỷ'
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/login'], { 
            queryParams: { returnUrl: '/promotions' }
          });
        }
      });
      return;
    }

    if (this.isInWishlist(productId)) {
      this.wishlistService.removeFromWishlist(userId, productId).subscribe({
        next: () => {
          this.wishlistProductIds.delete(productId);
          Swal.fire({
            title: 'Đã xoá!',
            text: 'Sản phẩm đã được xoá khỏi danh sách yêu thích',
            icon: 'success',
            confirmButtonColor: '#EE4D2D',
            timer: 1500,
            showConfirmButton: false
          });
        },
        error: (error) => {
          console.error('Error removing from wishlist:', error);
        }
      });
    } else {
      this.wishlistService.addToWishlist(userId, productId).subscribe({
        next: () => {
          this.wishlistProductIds.add(productId);
          Swal.fire({
            title: 'Thành công!',
            text: 'Sản phẩm đã được thêm vào danh sách yêu thích',
            icon: 'success',
            confirmButtonColor: '#EE4D2D',
            timer: 1500,
            showConfirmButton: false
          });
        },
        error: (error) => {
          console.error('Error adding to wishlist:', error);
        }
      });
    }
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/product', productId]);
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
}
