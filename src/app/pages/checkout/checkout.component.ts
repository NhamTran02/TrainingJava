import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { Cart } from '../../models/cart';
import { PaymentMethod, ShippingType } from '../../models/order';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent, ReactiveFormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  checkoutForm!: FormGroup;
  cart: Cart | null = null;
  loading = true;
  submitting = false;
  userId: number | null = null;

  PaymentMethod = PaymentMethod;
  ShippingType = ShippingType;

  shippingFees = {
    [ShippingType.STANDARD]: 15000,
    [ShippingType.EXPRESS]: 20000,
    [ShippingType.FAST]: 30000
  };

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.userId = this.getUserId();
    if (!this.userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.initForm();
    this.loadCart();
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

  private initForm(): void {
    this.checkoutForm = this.fb.group({
      shippingAddress: ['', [Validators.required, Validators.minLength(10)]],
      note: [''],
      paymentMethod: [PaymentMethod.COD, Validators.required],
      shippingType: [ShippingType.STANDARD, Validators.required]
    });
  }

  private loadCart(): void {
    if (!this.userId) return;

    this.loading = true;
    this.cartService.getCart(this.userId).subscribe({
      next: (cart) => {
        this.cart = cart;
        const selectedItems = cart.items.filter(item => item.selected);
        
        if (selectedItems.length === 0) {
          Swal.fire({
            title: 'Giỏ hàng trống',
            text: 'Vui lòng chọn sản phẩm để thanh toán',
            icon: 'warning',
            confirmButtonColor: '#F97316'
          }).then(() => {
            this.router.navigate(['/cart']);
          });
        }
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading cart:', error);
        this.loading = false;
        this.router.navigate(['/cart']);
      }
    });
  }

  getSelectedItems() {
    return this.cart?.items.filter(item => item.selected) || [];
  }

  getSubtotal(): number {
    return this.getSelectedItems().reduce((sum, item) => sum + item.subtotal, 0);
  }

  getShippingFee(): number {
    const shippingType = this.checkoutForm.get('shippingType')?.value as ShippingType;
    return this.shippingFees[shippingType] || 0;
  }

  getTotal(): number {
    return this.getSubtotal() + this.getShippingFee();
  }

  onSubmit(): void {
    if (this.checkoutForm.invalid || !this.userId) {
      Object.keys(this.checkoutForm.controls).forEach(key => {
        this.checkoutForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.submitting = true;
    const orderRequest = this.checkoutForm.value;

    this.orderService.createOrder(this.userId, orderRequest).subscribe({
      next: (response) => {
        this.submitting = false;
        
        if (response.paymentUrl) {
          // Redirect to VNPay
          window.location.href = response.paymentUrl;
        } else {
          // COD success
          Swal.fire({
            title: 'Đặt hàng thành công!',
            html: `
              <p>Mã đơn hàng: <strong>#${response.id}</strong></p>
              <p>Tổng tiền: <strong>${this.getTotal().toLocaleString()}đ</strong></p>
              <p>Chúng tôi sẽ liên hệ với bạn sớm nhất!</p>
            `,
            icon: 'success',
            confirmButtonColor: '#F97316',
            confirmButtonText: 'Xem đơn hàng'
          }).then(() => {
            this.router.navigate(['/orders']);
          });
        }
      },
      error: (error) => {
        this.submitting = false;
        console.error('Error creating order:', error);
        Swal.fire({
          title: 'Lỗi!',
          text: error.error?.message || 'Không thể tạo đơn hàng. Vui lòng thử lại.',
          icon: 'error',
          confirmButtonColor: '#EF4444'
        });
      }
    });
  }
}
