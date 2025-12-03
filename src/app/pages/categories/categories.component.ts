import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { CategoryService } from '../../services/category.service';
import { ProductService } from '../../services/product.service';
import { WishlistService } from '../../services/wishlist.service';
import { Category } from '../../models/category';
import { Product } from '../../models/product';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  products: Product[] = [];
  selectedCategory: Category | null = null;
  loading = true;
  loadingProducts = false;
  wishlistProductIds: Set<number> = new Set();

  // Pagination
  currentPage = 1;
  itemsPerPage = 20;
  totalPages = 1;
  totalElements = 0;

  constructor(
    private categoryService: CategoryService,
    private productService: ProductService,
    private wishlistService: WishlistService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadWishlist();
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.loading = false;
        
        // Load all products initially
        if (categories.length > 0) {
          this.loadAllProducts();
        }
      },
      error: (error) => {
        console.error('Error loading categories:', error);
        this.loading = false;
      }
    });
  }

  loadAllProducts(): void {
    this.loadingProducts = true;
    this.selectedCategory = null;
    this.currentPage = 1;
    this.productService.getAllProducts(this.currentPage - 1, this.itemsPerPage).subscribe({
      next: (response) => {
        this.products = this.processProducts(response.content);
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loadingProducts = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.loadingProducts = false;
      }
    });
  }

  selectCategory(category: Category): void {
    this.selectedCategory = category;
    this.loadingProducts = true;
    this.currentPage = 1;
    
    this.productService.getProductsByCategory(category.id, this.currentPage - 1, this.itemsPerPage).subscribe({
      next: (response) => {
        this.products = this.processProducts(response.content);
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loadingProducts = false;
      },
      error: (error) => {
        console.error('Error loading products by category:', error);
        this.loadingProducts = false;
      }
    });
  }

  private processProducts(products: Product[]): Product[] {
    return products.map(product => {
      // Get price from first variant
      if (product.variants && product.variants.length > 0) {
        const firstVariant = product.variants[0];
        product.regularPrice = firstVariant.regularPrice;
        product.salePrice = firstVariant.salePrice;
      }
      
      // Get thumbnail image
      if (product.images && product.images.length > 0) {
        const thumbnail = product.images.find(img => img.isThumbnail);
        product.imageUrl = thumbnail ? thumbnail.url : product.images[0].url;
      }
      
      return product;
    });
  }

  viewProductDetail(productId: number): void {
    this.router.navigate(['/product', productId]);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.loadingProducts = true;
      
      if (this.selectedCategory) {
        this.productService.getProductsByCategory(this.selectedCategory.id, this.currentPage - 1, this.itemsPerPage).subscribe({
          next: (response) => {
            this.products = this.processProducts(response.content);
            this.totalPages = response.totalPages;
            this.totalElements = response.totalElements;
            this.loadingProducts = false;
            window.scrollTo({ top: 0, behavior: 'smooth' });
          },
          error: (error) => {
            console.error('Error loading products:', error);
            this.loadingProducts = false;
          }
        });
      } else {
        this.productService.getAllProducts(this.currentPage - 1, this.itemsPerPage).subscribe({
          next: (response) => {
            this.products = this.processProducts(response.content);
            this.totalPages = response.totalPages;
            this.totalElements = response.totalElements;
            this.loadingProducts = false;
            window.scrollTo({ top: 0, behavior: 'smooth' });
          },
          error: (error) => {
            console.error('Error loading products:', error);
            this.loadingProducts = false;
          }
        });
      }
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
      return Array.from({ length: this.totalPages }, (_, i) => i + 1);
    }
    
    pages.push(1);
    
    let startPage = Math.max(2, this.currentPage - 1);
    let endPage = Math.min(this.totalPages - 1, this.currentPage + 1);
    
    if (this.currentPage <= 3) {
      endPage = Math.min(4, this.totalPages - 1);
    } else if (this.currentPage >= this.totalPages - 2) {
      startPage = Math.max(this.totalPages - 3, 2);
    }
    
    if (startPage > 2) {
      pages.push(-1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    if (endPage < this.totalPages - 1) {
      pages.push(-1);
    }
    
    if (this.totalPages > 1) {
      pages.push(this.totalPages);
    }
    
    return pages;
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
            queryParams: { returnUrl: '/categories' }
          });
        }
      });
      return;
    }

    if (this.isInWishlist(productId)) {
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
          Swal.fire({
            title: 'Lỗi!',
            text: 'Không thể xoá sản phẩm khỏi danh sách yêu thích',
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
          Swal.fire({
            title: 'Lỗi!',
            text: 'Không thể thêm sản phẩm vào danh sách yêu thích',
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
