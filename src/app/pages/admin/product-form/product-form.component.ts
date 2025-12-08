import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { CategoryService } from '../../../services/category.service';
import { BrandService, Brand } from '../../../services/brand.service';
import { HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';

interface Category {
  id: number;
  name: string;
}

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {
  isEditMode = false;
  productId: number | null = null;
  loading = false;

  productForm = {
    name: '',
    description: '',
    brandId: null as number | null,
    categoryId: null as number | null
  };

  categories: Category[] = [];
  brands: Brand[] = [];
  
  selectedFiles: File[] = [];
  imagePreviews: string[] = [];
  thumbnailIndex: number | null = null; // null = no thumbnail
  uploadProgress = 0;

  // Existing images from server
  existingImages: any[] = [];
  imagesToDelete: number[] = [];
  
  // Carousel controls for existing images
  existingImagesStartIndex = 0;
  imagesPerView = 4;
  
  // Carousel controls for new images
  newImagesStartIndex = 0;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadBrands();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.productId = +id;
      this.loadProduct(this.productId);
    }
  }

  loadProduct(id: number): void {
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.productForm = {
          name: product.name,
          description: product.description || '',
          brandId: product.brandId,
          categoryId: product.categoryId
        };
        
        // Load existing images
        if (product.images && product.images.length > 0) {
          this.existingImages = product.images;
        }
      },
      error: (error) => {
        console.error('Error loading product:', error);
        Swal.fire('Lỗi', 'Không thể tải thông tin sản phẩm', 'error');
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

  onFileSelect(event: any): void {
    const files = event.target.files;
    if (files) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (file.type.startsWith('image/')) {
          this.selectedFiles.push(file);
          
          const reader = new FileReader();
          reader.onload = (e: any) => {
            this.imagePreviews.push(e.target.result);
          };
          reader.readAsDataURL(file);
        }
      }
    }
  }

  removeImage(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.imagePreviews.splice(index, 1);
    
    // Adjust thumbnail index
    if (this.thumbnailIndex !== null) {
      if (this.thumbnailIndex === index) {
        // If removed image was thumbnail, reset to null
        this.thumbnailIndex = null;
      } else if (this.thumbnailIndex > index) {
        // If removed image was before thumbnail, decrease index
        this.thumbnailIndex--;
      }
    }
  }

  removeExistingImage(imageId: number): void {
    Swal.fire({
      title: 'Xác nhận xóa',
      text: 'Bạn có chắc muốn xóa ảnh này?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        this.imagesToDelete.push(imageId);
        this.existingImages = this.existingImages.filter(img => img.id !== imageId);
        Swal.fire({
          title: 'Đã xóa',
          text: 'Ảnh sẽ được xóa khi bạn lưu sản phẩm',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        });
      }
    });
  }

  setThumbnail(index: number): void {
    if (this.thumbnailIndex === index) {
      // Toggle off if clicking same image
      this.thumbnailIndex = null;
    } else {
      this.thumbnailIndex = index;
    }
  }

  async saveProduct(): Promise<void> {
    if (!this.productForm.name || !this.productForm.brandId || !this.productForm.categoryId) {
      Swal.fire('Lỗi', 'Vui lòng điền đầy đủ thông tin bắt buộc', 'error');
      return;
    }

    this.loading = true;

    try {
      let productId: number;

      if (this.isEditMode && this.productId) {
        const updatedProduct = await this.productService.updateProduct(this.productId, this.productForm).toPromise();
        productId = updatedProduct!.id;
        
        // Delete marked images
        if (this.imagesToDelete.length > 0) {
          await this.deleteImages();
        }
      } else {
        const newProduct = await this.productService.createProduct(this.productForm).toPromise();
        productId = newProduct!.id;
      }

      if (this.selectedFiles.length > 0) {
        await this.uploadImages(productId);
      } else {
        Swal.fire({
          title: 'Thành công',
          text: this.isEditMode ? 'Cập nhật sản phẩm thành công' : 'Tạo sản phẩm thành công',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        });
      }

      this.router.navigate(['/admin/products']);
    } catch (error: any) {
      console.error('Error saving product:', error);
      Swal.fire('Lỗi', error.error?.message || 'Không thể lưu sản phẩm', 'error');
    } finally {
      this.loading = false;
    }
  }

  async uploadImages(productId: number): Promise<void> {
    const formData = new FormData();
    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    try {
      // Use thumbnailIndex if set, otherwise default to 0
      const thumbIndex = this.thumbnailIndex !== null ? this.thumbnailIndex : 0;
      await this.http.post(
        `http://localhost:8080/api/product-images/${productId}?thumbnailIndex=${thumbIndex}`,
        formData
      ).toPromise();
      
      Swal.fire({
        title: 'Thành công',
        text: this.isEditMode ? 'Cập nhật sản phẩm và upload ảnh thành công' : 'Tạo sản phẩm và upload ảnh thành công',
        icon: 'success',
        timer: 2000,
        showConfirmButton: false
      });
    } catch (error) {
      console.error('Error uploading images:', error);
      Swal.fire('Cảnh báo', 'Sản phẩm đã được lưu nhưng không thể upload ảnh', 'warning');
    }
  }

  async deleteImages(): Promise<void> {
    try {
      for (const imageId of this.imagesToDelete) {
        await this.http.delete(`http://localhost:8080/api/product-images/${imageId}`).toPromise();
      }
    } catch (error) {
      console.error('Error deleting images:', error);
      throw error;
    }
  }

  cancel(): void {
    this.router.navigate(['/admin/products']);
  }

  // Carousel methods for existing images
  get visibleExistingImages() {
    return this.existingImages.slice(
      this.existingImagesStartIndex,
      this.existingImagesStartIndex + this.imagesPerView
    );
  }

  canScrollExistingPrev(): boolean {
    return this.existingImagesStartIndex > 0;
  }

  canScrollExistingNext(): boolean {
    return this.existingImagesStartIndex + this.imagesPerView < this.existingImages.length;
  }

  scrollExistingPrev(): void {
    if (this.canScrollExistingPrev()) {
      this.existingImagesStartIndex--;
    }
  }

  scrollExistingNext(): void {
    if (this.canScrollExistingNext()) {
      this.existingImagesStartIndex++;
    }
  }

  // Carousel methods for new images
  get visibleNewImages() {
    return this.imagePreviews.slice(
      this.newImagesStartIndex,
      this.newImagesStartIndex + this.imagesPerView
    );
  }

  canScrollNewPrev(): boolean {
    return this.newImagesStartIndex > 0;
  }

  canScrollNewNext(): boolean {
    return this.newImagesStartIndex + this.imagesPerView < this.imagePreviews.length;
  }

  scrollNewPrev(): void {
    if (this.canScrollNewPrev()) {
      this.newImagesStartIndex--;
    }
  }

  scrollNewNext(): void {
    if (this.canScrollNewNext()) {
      this.newImagesStartIndex++;
    }
  }
}
