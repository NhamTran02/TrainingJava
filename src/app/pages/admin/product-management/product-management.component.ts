import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { CategoryService } from '../../../services/category.service';
import { BrandService, Brand } from '../../../services/brand.service';
import { Product } from '../../../models/product';
import Swal from 'sweetalert2';

interface Category {
  id: number;
  name: string;
}

interface ProductRequest {
  name: string;
  description: string;
  brandId: number | null;
  categoryId: number | null;
}

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.css']
})
export class ProductManagementComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  categories: Category[] = [];
  brands: Brand[] = [];
  loading = false;
  searchTerm = '';
  
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  showModal = false;
  isEditMode = false;
  selectedProduct: Product | null = null;

  productForm: ProductRequest = {
    name: '',
    description: '',
    brandId: null,
    categoryId: null
  };

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
    this.loadBrands();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAllProducts(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.products = response.content;
        this.filteredProducts = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        Swal.fire('Lỗi', 'Không thể tải danh sách sản phẩm', 'error');
        this.loading = false;
      }
    });
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadBrands(): void {
    this.brandService.getAllBrands().subscribe({
      next: (brands) => {
        this.brands = brands;
      },
      error: (error) => {
        console.error('Error loading brands:', error);
      }
    });
  }

  filterProducts(): void {
    if (!this.searchTerm.trim()) {
      this.filteredProducts = this.products;
      return;
    }

    const term = this.searchTerm.toLowerCase();
    this.filteredProducts = this.products.filter(product =>
      product.name?.toLowerCase().includes(term) ||
      product.description?.toLowerCase().includes(term)
    );
  }

  openCreateModal(): void {
    this.router.navigate(['/admin/products/new']);
  }

  openEditModal(product: Product): void {
    this.router.navigate(['/admin/products/edit', product.id]);
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedProduct = null;
  }

  saveProduct(): void {
    if (!this.productForm.name || !this.productForm.brandId || !this.productForm.categoryId) {
      Swal.fire('Lỗi', 'Vui lòng điền đầy đủ thông tin bắt buộc', 'error');
      return;
    }

    if (this.isEditMode && this.selectedProduct) {
      this.productService.updateProduct(this.selectedProduct.id, this.productForm).subscribe({
        next: (updatedProduct) => {
          const index = this.products.findIndex(p => p.id === updatedProduct.id);
          if (index !== -1) {
            this.products[index] = updatedProduct;
            this.filterProducts();
          }
          Swal.fire('Thành công', 'Cập nhật sản phẩm thành công', 'success');
          this.closeModal();
        },
        error: (error) => {
          console.error('Error updating product:', error);
          Swal.fire('Lỗi', error.error?.message || 'Không thể cập nhật sản phẩm', 'error');
        }
      });
    } else {
      this.productService.createProduct(this.productForm).subscribe({
        next: () => {
          this.loadProducts();
          Swal.fire('Thành công', 'Tạo sản phẩm thành công', 'success');
          this.closeModal();
        },
        error: (error) => {
          console.error('Error creating product:', error);
          Swal.fire('Lỗi', error.error?.message || 'Không thể tạo sản phẩm', 'error');
        }
      });
    }
  }

  deleteProduct(product: Product): void {
    Swal.fire({
      title: 'Xác nhận xóa',
      text: `Bạn có chắc muốn xóa sản phẩm "${product.name}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productService.deleteProduct(product.id).subscribe({
          next: () => {
            // Update the product's deleted status instead of removing it
            const index = this.products.findIndex(p => p.id === product.id);
            if (index !== -1) {
              this.products[index].deleted = true;
              this.filterProducts();
            }
            Swal.fire({
                title: 'Đã xóa',
                text: 'Sản phẩm đã được xóa',
                icon: 'success',
                timer: 1500, 
                timerProgressBar: true, 
                showConfirmButton: false
            });
          },
          error: (error) => {
            console.error('Error deleting product:', error);
            Swal.fire('Lỗi', error.error?.message || 'Không thể xóa sản phẩm', 'error');
          }
        });
      }
    });
  }

  restoreProduct(product: Product): void {
    Swal.fire({
      title: 'Xác nhận khôi phục',
      text: `Bạn có chắc muốn khôi phục sản phẩm "${product.name}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#10b981',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Khôi phục',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productService.restoreProduct(product.id).subscribe({
          next: () => {
            const index = this.products.findIndex(p => p.id === product.id);
            if (index !== -1) {
              this.products[index].deleted = false;
              this.filterProducts();
            }
            Swal.fire({
                title: 'Đã khôi phục',
                text: 'Sản phẩm đã được khôi phục',
                icon: 'success',
                timer: 1500, 
                timerProgressBar: true, 
                showConfirmButton: false
            });
          },
          error: (error) => {
            console.error('Error restoring product:', error);
            Swal.fire('Lỗi', error.error?.message || 'Không thể khôi phục sản phẩm', 'error');
          }
        });
      }
    });
  }

  getBrandName(brandId: number): string {
    const brand = this.brands.find(b => b.id === brandId);
    return brand?.name || '-';
  }

  getCategoryName(categoryId: number): string {
    const category = this.categories.find(c => c.id === categoryId);
    return category?.name || '-';
  }

  getThumbnailUrl(product: Product): string | null {
    if (!product.images || product.images.length === 0) {
      return null;
    }
    // Find thumbnail image
    const thumbnail = product.images.find(img => img.isThumbnail);
    if (thumbnail) {
      return thumbnail.url;
    }
    // If no thumbnail, return first image
    return product.images[0]?.url || null;
  }

  goToProductDetail(productId: number): void {
    this.router.navigate(['/product', productId]);
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }
}
