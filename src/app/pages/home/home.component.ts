import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { WishlistService } from '../../services/wishlist.service';
import { Product } from '../../models/product';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import Swal from 'sweetalert2';

interface Slide {
  image: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  loading = true;
  wishlistProductIds: Set<number> = new Set();
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 20;
  totalPages = 1;
  totalElements = 0;
  pages: number[] = [];

  // Slider
  currentSlide = 0;
  slideInterval: any;
  slides: Slide[] = [
    {
      image: 'assets/images/slider_1.png'
    },
    {
      image: 'assets/images/slider_2.png'
    },
    {
      image: 'assets/images/slider_3.png'
    }
  ];

  constructor(
    private productService: ProductService,
    private wishlistService: WishlistService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadWishlist();
    this.startSlideShow();
  }

  ngOnDestroy(): void {
    this.stopSlideShow();
  }
  // Slider methodsf
  startSlideShow(): void {
    this.slideInterval = setInterval(() => {
      this.nextSlide();
    }, 3000); // Auto slide every 3 seconds
  }

  stopSlideShow(): void {
    if (this.slideInterval) {
      clearInterval(this.slideInterval);
    }
  }

  nextSlide(): void {
    this.currentSlide = (this.currentSlide + 1) % this.slides.length;
  }

  previousSlide(): void {
    this.currentSlide = this.currentSlide === 0 ? this.slides.length - 1 : this.currentSlide - 1;
  }

  goToSlide(index: number): void {
    this.currentSlide = index;
    this.stopSlideShow();
    this.startSlideShow(); // Restart auto slide
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAllProducts(this.currentPage - 1, this.itemsPerPage).subscribe({
      next: (response) => {
        console.log('Response from API:', response);
        this.products = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.products = [];
        this.loading = false;
      }
    });
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  getDisplayedPages(): number[] {
    const maxPagesToShow = 5;
    const pages: number[] = [];
    
    if (this.totalPages <= maxPagesToShow) {
      // Nếu tổng số trang ít, hiển thị tất cả
      return Array.from({ length: this.totalPages }, (_, i) => i + 1);
    }
    
    // Luôn hiển thị trang đầu
    pages.push(1);
    
    let startPage = Math.max(2, this.currentPage - 1);
    let endPage = Math.min(this.totalPages - 1, this.currentPage + 1);
    
    // Điều chỉnh nếu ở đầu hoặc cuối
    if (this.currentPage <= 3) {
      endPage = Math.min(4, this.totalPages - 1);
    } else if (this.currentPage >= this.totalPages - 2) {
      startPage = Math.max(this.totalPages - 3, 2);
    }
    
    // Thêm dấu ... nếu cần
    if (startPage > 2) {
      pages.push(-1); // -1 đại diện cho "..."
    }
    
    // Thêm các trang ở giữa
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    // Thêm dấu ... nếu cần
    if (endPage < this.totalPages - 1) {
      pages.push(-1); // -1 đại diện cho "..."
    }
    
    // Luôn hiển thị trang cuối
    if (this.totalPages > 1) {
      pages.push(this.totalPages);
    }
    
    return pages;
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

  getMaxPrice(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    return Math.max(...product.variants.map(v => v.salePrice));
  }

  getMinRegularPrice(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    return Math.min(...product.variants.map(v => v.regularPrice));
  }

  hasDiscount(product: Product): boolean {
    if (!product.variants || product.variants.length === 0) return false;
    return product.variants.some(v => v.salePrice < v.regularPrice);
  }

  getDiscountPercentage(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    const minSalePrice = this.getMinPrice(product);
    const minRegularPrice = this.getMinRegularPrice(product);
    if (minRegularPrice === 0) return 0;
    return Math.round(((minRegularPrice - minSalePrice) / minRegularPrice) * 100);
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/product', productId]).then(() => {
      window.scrollTo({ top: 0, behavior: 'instant' });
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
            queryParams: { returnUrl: '/' }
          });
        }
      });
      return;
    }

    const isCurrentlyInWishlist = this.isInWishlist(productId);

    if (isCurrentlyInWishlist) {
      // Remove from wishlist
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
          const errorMessage = error.error?.message || 'Không thể xoá sản phẩm khỏi danh sách yêu thích';
          Swal.fire({
            title: 'Lỗi!',
            text: errorMessage,
            icon: 'error',
            confirmButtonColor: '#EE4D2D'
          });
        }
      });
    } else {
      // Add to wishlist
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
          const errorMessage = error.error?.message || 'Không thể thêm sản phẩm vào danh sách yêu thích';
          // Nếu sản phẩm đã có trong wishlist, cập nhật state
          if (error.status === 409) {
            this.wishlistProductIds.add(productId);
          }
          Swal.fire({
            title: 'Lỗi!',
            text: errorMessage,
            icon: 'error',
            confirmButtonColor: '#EE4D2D'
          });
        }
      });
    }
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
