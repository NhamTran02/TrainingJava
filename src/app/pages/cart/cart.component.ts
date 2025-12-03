import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { CartService } from '../../services/cart.service';
import { Cart, CartItem } from '../../models/cart';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;
  loading = true;
  userId: number | null = null;

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = this.getUserId();
    if (this.userId) {
      this.loadCart();
    } else {
      this.loading = false;
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

  loadCart(): void {
    if (!this.userId) return;
    
    this.loading = true;
    this.cartService.getCart(this.userId).subscribe({
      next: (cart) => {
        console.log('Cart data:', cart);
        console.log('First item:', cart.items[0]);
        this.cart = cart;
        // Cập nhật cartItemCount sau khi load
        this.cartService.updateCartItemCount(cart.items.length);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading cart:', error);
        this.loading = false;
      }
    });
  }

  updateQuantity(item: CartItem, change: number): void {
    if (!this.userId) return;

    const newQuantity = item.quantity + change;
    if (newQuantity < 1){
        this.removeItem(item);
    }

    // Lưu giá trị cũ để rollback nếu có lỗi
    const oldQuantity = item.quantity;
    const oldSubtotal = item.subtotal;
    
    // Optimistic update - Cập nhật UI ngay lập tức
    item.quantity = newQuantity;
    item.subtotal = item.unitPrice * newQuantity;
    
    // Cập nhật tổng tiền nếu item được chọn
    if (item.selected) {
      this.updateCartTotal();
    }

    // Gọi API ngầm: Xóa item cũ và thêm lại với số lượng mới
    this.cartService.removeItem(this.userId, {
      variantId: item.variantResponse.id,
      quantity: oldQuantity
    }).subscribe({
      next: () => {
        // Thêm lại với số lượng mới
        this.cartService.addToCart(this.userId!, {
          variantId: item.variantResponse.id,
          quantity: newQuantity,
          selected: item.selected
        }).subscribe({
          next: () => {
            // Khôi phục trạng thái selected nếu cần
            if (item.selected) {
              this.cartService.toggleSelected(this.userId!, item.variantResponse.id, true).subscribe({
                next: () => {
                  // Không reload
                },
                error: (error) => {
                  console.error('Error toggling selection:', error);
                  this.loadCart(); // Chỉ reload nếu có lỗi
                }
              });
            }
          },
          error: (error) => {
            console.error('Error adding item:', error);
            // Rollback UI nếu có lỗi
            item.quantity = oldQuantity;
            item.subtotal = oldSubtotal;
            if (item.selected) {
              this.updateCartTotal();
            }
            this.loadCart();
          }
        });
      },
      error: (error) => {
        console.error('Error removing item:', error);
        // Rollback UI nếu có lỗi
        item.quantity = oldQuantity;
        item.subtotal = oldSubtotal;
        if (item.selected) {
          this.updateCartTotal();
        }
      }
    });
  }

  toggleItemSelection(item: CartItem): void {
    if (!this.userId) return;

    const newSelectedState = !item.selected;
    
    // Optimistic update - cập nhật UI ngay lập tức
    item.selected = newSelectedState;
    
    // Cập nhật tổng tiền ngay lập tức
    this.updateCartTotal();

    // Gọi API để cập nhật backend (không reload)
    this.cartService.toggleSelected(this.userId, item.variantResponse.id, newSelectedState).subscribe({
      next: () => {
        // Không cần reload, UI đã được cập nhật
        console.log("id variant:"+ item.variantResponse.id);
      },
      error: (error) => {
        console.error('Error toggling selection:', error);
        // Rollback nếu có lỗi
        item.selected = !newSelectedState;
        this.updateCartTotal();
      }
    });
  }

  // Tính lại tổng tiền từ các items đã chọn
  private updateCartTotal(): void {
    if (!this.cart) return;
    
    this.cart.total = this.cart.items
      .filter(item => item.selected)
      .reduce((sum, item) => sum + item.subtotal, 0);
  }

  removeItem(item: CartItem): void {
    if (!this.userId) return;

    Swal.fire({
      title: 'Xác nhận xóa',
      text: `Bạn có chắc muốn xóa "${item.productName}" khỏi giỏ hàng?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
      customClass: {
        popup: 'rounded-xl shadow-lg'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.cartService.removeItem(this.userId!, {
          variantId: item.variantResponse.id,
          quantity: item.quantity
        }).subscribe({
          next: () => {
            // Reload cart để cập nhật UI
            this.loadCart();
            // Hiển thị thông báo thành công
            Swal.fire({
              title: 'Đã xóa!',
              text: 'Sản phẩm đã được xóa khỏi giỏ hàng',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false,
              customClass: {
                popup: 'rounded-xl shadow-lg'
              }
            });
          },
          error: (error) => {
            console.error('Error removing item:', error);
            this.loadCart();
            Swal.fire({
              title: 'Lỗi!',
              text: 'Không thể xóa sản phẩm. Vui lòng thử lại.',
              icon: 'error',
              confirmButtonColor: '#EF4444',
              customClass: {
                popup: 'rounded-xl shadow-lg'
              }
            });
          }
        });
      }
    });
  }

  clearCart(): void {
    if (!this.cart || this.cart.items.length === 0) return;

    Swal.fire({
      title: 'Xóa tất cả sản phẩm?',
      text: 'Bạn có chắc muốn xóa tất cả sản phẩm trong giỏ hàng?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Xóa tất cả',
      cancelButtonText: 'Hủy',
      customClass: {
        popup: 'rounded-xl shadow-lg'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.cartService.clearCart(this.cart!.cartId).subscribe({
          next: () => {
            // Reload cart để cập nhật UI
            this.loadCart();
            // Hiển thị thông báo thành công
            Swal.fire({
              title: 'Đã xóa!',
              text: 'Giỏ hàng đã được làm trống',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false,
              customClass: {
                popup: 'rounded-xl shadow-lg'
              }
            });
          },
          error: (error) => {
            console.error('Error clearing cart:', error);
            this.loadCart();
            Swal.fire({
              title: 'Lỗi!',
              text: 'Không thể xóa giỏ hàng. Vui lòng thử lại.',
              icon: 'error',
              confirmButtonColor: '#EF4444',
              customClass: {
                popup: 'rounded-xl shadow-lg'
              }
            });
          }
        });
      }
    });
  }

  getSelectedItems(): CartItem[] {
    return this.cart?.items.filter(item => item.selected) || [];
  }

  getSelectedTotal(): number {
    // Tính real-time từ frontend cho UX mượt mà
    if (!this.cart) return 0;
    return this.cart.items
      .filter(item => item.selected)
      .reduce((sum, item) => sum + item.subtotal, 0);
  }

  areAllSelected(): boolean {
    if (!this.cart || this.cart.items.length === 0) return false;
    return this.cart.items.every(item => item.selected);
  }

  toggleSelectAll(): void {
    if (!this.userId || !this.cart) return;

    const selectAll = !this.areAllSelected();
    
    // Optimistic update - cập nhật UI ngay lập tức
    this.cart.items.forEach(item => {
      item.selected = selectAll;
    });
    
    // Cập nhật tổng tiền ngay lập tức
    this.updateCartTotal();
    
    // Update all items on backend
    let completed = 0;
    const total = this.cart.items.length;
    
    this.cart.items.forEach(item => {
      this.cartService.toggleSelected(this.userId!, item.variantResponse.id, selectAll).subscribe({
        next: () => {
          completed++;
          // Không cần reload, UI đã được cập nhật
        },
        error: (error) => {
          console.error('Error toggling item:', error);
          completed++;
          if (completed === total) {
            // Chỉ reload nếu có lỗi
            this.loadCart();
          }
        }
      });
    });
  }

  removeSelectedItems(): void {
    if (!this.userId || !this.cart) return;

    const selectedItems = this.getSelectedItems();
    if (selectedItems.length === 0) {
      Swal.fire({
        title: 'Chưa chọn sản phẩm',
        text: 'Vui lòng chọn ít nhất một sản phẩm để xóa!',
        icon: 'info',
        confirmButtonColor: '#F97316',
        confirmButtonText: 'Đã hiểu',
        customClass: {
          popup: 'rounded-xl shadow-lg'
        }
      });
      return;
    }

    Swal.fire({
      title: 'Xóa sản phẩm đã chọn?',
      text: `Bạn có chắc muốn xóa ${selectedItems.length} sản phẩm đã chọn?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
      customClass: {
        popup: 'rounded-xl shadow-lg'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        let completed = 0;
        const total = selectedItems.length;

        selectedItems.forEach(item => {
          this.cartService.removeItem(this.userId!, {
            variantId: item.variantResponse.id,
            quantity: item.quantity
          }).subscribe({
            next: () => {
              completed++;
              if (completed === total) {
                this.loadCart();
                Swal.fire({
                  title: 'Đã xóa!',
                  text: `Đã xóa ${total} sản phẩm khỏi giỏ hàng`,
                  icon: 'success',
                  timer: 1500,
                  showConfirmButton: false,
                  customClass: {
                    popup: 'rounded-xl shadow-lg'
                  }
                });
              }
            },
            error: (error) => {
              console.error('Error removing item:', error);
              completed++;
              if (completed === total) {
                this.loadCart();
              }
            }
          });
        });
      }
    });
  }

  proceedToCheckout(): void {
    const selectedItems = this.getSelectedItems();
    if (selectedItems.length === 0) {
      alert('Vui lòng chọn ít nhất một sản phẩm để thanh toán!');
      return;
    }
    // TODO: Navigate to checkout page
    this.router.navigate(['/checkout']);
  }

  continueShopping(): void {
    this.router.navigate(['/']);
  }

  goToProduct(productId: number): void {
    this.router.navigate(['/product', productId]);
  }
}
