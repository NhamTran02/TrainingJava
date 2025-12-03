import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-payment-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gray-100 flex items-center justify-center">
      <div class="text-center">
        <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-orange-500 mx-auto"></div>
        <p class="mt-4 text-gray-600">Đang xử lý kết quả thanh toán...</p>
      </div>
    </div>
  `
})
export class PaymentCallbackComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Lấy query params từ backend callback
    this.route.queryParams.subscribe(params => {
      const orderId = params['orderId'];
      const amount = params['amount'];
      const status = params['status'];
      const message = params['message'];
      const trackingNumber = params['trackingNumber'];

      if (status === 'SUCCESS') {
        // Thanh toán thành công
        this.showSuccessMessage(orderId, amount, trackingNumber);
      } else {
        // Thanh toán thất bại
        this.showErrorMessage(message || 'Thanh toán không thành công');
      }
    });
  }

  private showSuccessMessage(orderId: string, amount: string, trackingNumber: string): void {
    const amountInVND = parseInt(amount);

    Swal.fire({
      title: 'Đặt hàng thành công!',
      html: `
        <div class="text-left space-y-2">
          <p><strong>Mã đơn hàng:</strong> #${orderId}</p>
          <p><strong>Mã vận đơn:</strong> ${trackingNumber}</p>
          <p><strong>Tổng tiền:</strong> ${amountInVND.toLocaleString()}đ</p>
          <p class="mt-4 text-gray-600">Thanh toán VNPay thành công!</p>
          <p class="text-gray-600">Chúng tôi sẽ liên hệ với bạn sớm nhất!</p>
        </div>
      `,
      icon: 'success',
      timer: 3000, // Tự động đóng sau 3 giây
      timerProgressBar: true,
      showConfirmButton: true,
      confirmButtonColor: '#F97316',
      confirmButtonText: 'Xem đơn hàng ngay',
      allowOutsideClick: false,
      customClass: {
        popup: 'rounded-xl shadow-lg'
      }
    }).then(() => {
      this.router.navigate(['/orders']);
    });
  }

  private showErrorMessage(message: string): void {
    Swal.fire({
      title: 'Thanh toán thất bại!',
      text: message,
      icon: 'error',
      confirmButtonColor: '#EF4444',
      confirmButtonText: 'Thử lại',
      showCancelButton: true,
      cancelButtonText: 'Về trang chủ',
      allowOutsideClick: false,
      customClass: {
        popup: 'rounded-xl shadow-lg'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.router.navigate(['/checkout']);
      } else {
        this.router.navigate(['/']);
      }
    });
  }
}
