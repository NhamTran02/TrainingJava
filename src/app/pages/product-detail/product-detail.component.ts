import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { WishlistService } from '../../services/wishlist.service';
import { Product, ProductVariant } from '../../models/product';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  relatedProducts: Product[] = [];
  loading = true;
  selectedVariant: ProductVariant | null = null;
  selectedImage: string = '';
  quantity = 1;
  Math = Math;
  wishlistProductIds: Set<number> = new Set();
  isCurrentProductInWishlist = false;
  
  // Carousel controls
  thumbnailStartIndex = 0;
  thumbnailsPerView = 5;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private wishlistService: WishlistService
  ) {}

  ngOnInit(): void {
    // Scroll to top khi component load
    window.scrollTo({ top: 0, behavior: 'instant' });
    
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.loadProduct(id);
        this.loadRelatedProducts(id);
        this.loadWishlist();
        // Scroll to top khi chuyển sản phẩm
        window.scrollTo({ top: 0, behavior: 'instant' });
      }
    });
  }

  loadProduct(id: number): void {
    this.loading = true;
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.product = product;
        this.selectedVariant = product.variants?.[0] || null;
        this.selectedImage = this.getThumbnailImage() || '';
        this.isCurrentProductInWishlist = this.wishlistProductIds.has(product.id);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading product:', error);
        this.loading = false;
      }
    });
  }

  loadRelatedProducts(id: number): void {
    this.productService.getRelatedProducts(id).subscribe({
      next: (products) => {
        this.relatedProducts = products;
      },
      error: (error) => {
        console.error('Error loading related products:', error);
      }
    });
  }

  getThumbnailImage(): string | null {
    if (!this.product?.images || this.product.images.length === 0) return null;
    const thumbnail = this.product.images.find(img => img.isThumbnail);
    return thumbnail ? thumbnail.url : this.product.images[0].url;
  }

  getProductThumbnail(product: Product): string | null {
    if (!product.images || product.images.length === 0) return null;
    const thumbnail = product.images.find(img => img.isThumbnail);
    return thumbnail ? thumbnail.url : product.images[0].url;
  }

  getMinPrice(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    return Math.min(...product.variants.map(v => v.salePrice));
  }

  selectVariant(variant: ProductVariant): void {
    this.selectedVariant = variant;
    this.quantity = 1;
  }

  selectImage(imageUrl: string): void {
    this.selectedImage = imageUrl;
  }

  get visibleThumbnails() {
    if (!this.product?.images) return [];
    return this.product.images.slice(
      this.thumbnailStartIndex,
      this.thumbnailStartIndex + this.thumbnailsPerView
    );
  }

  canScrollPrev(): boolean {
    return this.thumbnailStartIndex > 0;
  }

  canScrollNext(): boolean {
    if (!this.product?.images) return false;
    return this.thumbnailStartIndex + this.thumbnailsPerView < this.product.images.length;
  }

  scrollThumbnailsPrev(): void {
    if (this.canScrollPrev()) {
      this.thumbnailStartIndex--;
    }
  }

  scrollThumbnailsNext(): void {
    if (this.canScrollNext()) {
      this.thumbnailStartIndex++;
    }
  }

  incrementQuantity(): void {
    if (this.selectedVariant && this.quantity < this.selectedVariant.stockQuantity) {
      this.quantity++;
    }
  }

  decrementQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  addToCart(): void {
    // Kiểm tra đăng nhập
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
      Swal.fire({
          title: 'Bạn chưa đăng nhập!',
          text: 'Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.',
          icon: 'warning',
          confirmButtonText: 'Đăng nhập ngay',
          confirmButtonColor: '#EE4D2D',
          backdrop: true,
          allowOutsideClick: true,
          showCancelButton: false,
          customClass: {
            popup: 'rounded-xl shadow-lg'
          }
        }).then((result) => {
            if(result.isConfirmed){
                this.router.navigate(['/login'], { 
                queryParams: { returnUrl: `/product/${this.product?.id}` }
                });
            }
        });
    
        return;
    }

    if (!this.product || !this.selectedVariant) {
      Swal.fire({
        title: 'Lỗi!',
        text: 'Vui lòng chọn phiên bản sản phẩm',
        icon: 'error',
        confirmButtonColor: '#EE4D2D'
      });
      return;
    }

    // Lấy userId
    const userId = this.getUserId();
    if (!userId) {
      Swal.fire({
        title: 'Lỗi!',
        text: 'Không tìm thấy thông tin người dùng',
        icon: 'error',
        confirmButtonColor: '#EE4D2D'
      });
      return;
    }

    // Gọi API thêm vào giỏ hàng
    this.cartService.addToCart(userId, {
      variantId: this.selectedVariant.id,
      quantity: this.quantity,
      selected: false
    }).subscribe({
      next: () => {
        Swal.fire({
          title: 'Thành công!',
          text: `Đã thêm ${this.quantity} sản phẩm vào giỏ hàng`,
          icon: 'success',
          confirmButtonColor: '#EE4D2D',
          timer: 2000,
          showConfirmButton: false
        });
        // Reset quantity
        this.quantity = 1;
      },
      error: (error) => {
        console.error('Error adding to cart:', error);
        Swal.fire({
          title: 'Lỗi!',
          text: error.error?.message || 'Không thể thêm sản phẩm vào giỏ hàng',
          icon: 'error',
          confirmButtonColor: '#EE4D2D'
        });
      }
    });
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

  goBack(): void {
    this.router.navigate(['/']);
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/product', productId]).then(() => {
      window.scrollTo({ top: 0, behavior: 'instant' });
    });
  }

  getDiscountPercentage(): number {
    if (!this.selectedVariant) return 0;
    if (this.selectedVariant.salePrice >= this.selectedVariant.regularPrice) return 0;
    return Math.round(((this.selectedVariant.regularPrice - this.selectedVariant.salePrice) / this.selectedVariant.regularPrice) * 100);
  }

  hasDiscount(): boolean {
    if (!this.selectedVariant) return false;
    return this.selectedVariant.salePrice < this.selectedVariant.regularPrice;
  }

  getProductDiscountPercentage(product: Product): number {
    if (!product.variants || product.variants.length === 0) return 0;
    
    // Tìm variant có giá thấp nhất và giá gốc cao nhất để tính % giảm giá tốt nhất
    const minSalePrice = Math.min(...product.variants.map(v => v.salePrice));
    const maxRegularPrice = Math.max(...product.variants.map(v => v.regularPrice));
    
    if (minSalePrice >= maxRegularPrice) return 0;
    
    return Math.round(((maxRegularPrice - minSalePrice) / maxRegularPrice) * 100);
  }

  loadWishlist(): void {
    const userId = this.getUserId();
    if (!userId) return;

    this.wishlistService.getWishlist(userId).subscribe({
      next: (products) => {
        this.wishlistProductIds = new Set(products.map(p => p.id));
        if (this.product) {
          this.isCurrentProductInWishlist = this.wishlistProductIds.has(this.product.id);
        }
      },
      error: (error) => {
        console.error('Error loading wishlist:', error);
      }
    });
  }

  isInWishlist(productId: number): boolean {
    return this.wishlistProductIds.has(productId);
  }

  toggleCurrentProductWishlist(): void {
    if (!this.product) return;
    
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
            queryParams: { returnUrl: `/product/${this.product?.id}` }
          });
        }
      });
      return;
    }

    const productId = this.product.id;

    if (this.isCurrentProductInWishlist) {
      // Remove from wishlist
      this.wishlistService.removeFromWishlist(userId, productId).subscribe({
        next: () => {
          this.isCurrentProductInWishlist = false;
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
          this.isCurrentProductInWishlist = true;
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
            queryParams: { returnUrl: `/product/${this.product?.id}` }
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
}
