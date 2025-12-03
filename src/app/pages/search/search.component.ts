import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { WishlistService } from '../../services/wishlist.service';
import { AuthService } from '../../services/auth.service';
import { Product } from '../../models/product';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent, FooterComponent],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  products: Product[] = [];
  loading = false;
  searchKeyword = '';
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;
  wishlistProductIds: Set<number> = new Set();
  Math = Math;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private productService: ProductService,
    private wishlistService: WishlistService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchKeyword = params['q'] || '';
      this.currentPage = 0;
      this.searchProducts();
    });

    if (this.authService.isLoggedIn()) {
      this.loadWishlist();
    }
  }

  searchProducts(): void {
    if (!this.searchKeyword.trim()) {
      this.products = [];
      return;
    }

    this.loading = true;
    this.productService.searchProducts(
      this.searchKeyword,
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (response) => {
        this.products = response.result.content;
        this.totalPages = response.result.totalPages;
        this.totalElements = response.result.totalElements;
        this.currentPage = response.result.page;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error searching products:', error);
        this.loading = false;
      }
    });
  }

  loadWishlist(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) return;

    this.wishlistService.getWishlist(currentUser.id).subscribe({
      next: (products) => {
        this.wishlistProductIds = new Set(products.map(p => p.id));
      },
      error: (error) => console.error('Error loading wishlist:', error)
    });
  }

  isInWishlist(productId: number): boolean {
    return this.wishlistProductIds.has(productId);
  }

  toggleWishlist(event: Event, productId: number): void {
    event.stopPropagation();

    if (!this.authService.isLoggedIn()) {
      Swal.fire({
        title: 'Bạn chưa đăng nhập!',
        text: 'Vui lòng đăng nhập để thêm sản phẩm vào danh sách yêu thích.',
        icon: 'warning',
        confirmButtonText: 'Đăng nhập ngay',
        confirmButtonColor: '#f97316',
        showCancelButton: true,
        cancelButtonText: 'Hủy'
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/login']);
        }
      });
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) return;

    if (this.isInWishlist(productId)) {
      this.wishlistService.removeFromWishlist(currentUser.id, productId).subscribe({
        next: () => {
          this.wishlistProductIds.delete(productId);
        },
        error: (error) => console.error('Error removing from wishlist:', error)
      });
    } else {
      this.wishlistService.addToWishlist(currentUser.id, productId).subscribe({
        next: () => {
          this.wishlistProductIds.add(productId);
        },
        error: (error) => console.error('Error adding to wishlist:', error)
      });
    }
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/product', productId]);
  }

  getProductThumbnail(product: Product): string | null {
    if (product.images && product.images.length > 0) {
      return product.images[0].url;
    }
    return null;
  }

  getMinPrice(product: Product): number {
    if (product.variants && product.variants.length > 0) {
      return Math.min(...product.variants.map(v => v.salePrice));
    }
    return 0;
  }

  getDiscountPercentage(product: Product): number {
    if (product.variants && product.variants.length > 0) {
      const variant = product.variants[0];
      if (variant.salePrice < variant.regularPrice) {
        return Math.round((1 - variant.salePrice / variant.regularPrice) * 100);
      }
    }
    return 0;
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.searchProducts();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  get pages(): number[] {
    const maxPages = 5;
    const pages: number[] = [];
    let startPage = Math.max(0, this.currentPage - Math.floor(maxPages / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + maxPages - 1);

    if (endPage - startPage < maxPages - 1) {
      startPage = Math.max(0, endPage - maxPages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }
}
