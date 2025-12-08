import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { OrderService } from '../../services/order.service';
import { OrderResponse, OrderStatus } from '../../models/order';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  filteredOrders: OrderResponse[] = [];
  loading = true;
  userId: number | null = null;
  selectedTab: string = 'ALL';

  OrderStatus = OrderStatus;

  tabs = [
    { key: 'ALL', label: 'Tất cả' },
    { key: OrderStatus.PENDING, label: 'Chờ xác nhận' },
    { key: OrderStatus.PROCESSING, label: 'Đang xử lý' },
    { key: OrderStatus.ON_DELIVERY, label: 'Đang giao' },
    { key: OrderStatus.DELIVERED, label: 'Đã giao' },
    { key: OrderStatus.CANCELLED, label: 'Đã hủy' }
  ];

  constructor(
    private orderService: OrderService,
    public router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.userId = this.getUserId();
    if (!this.userId) {
      this.router.navigate(['/login']);
      return;
    }
    
    // Check for payment success from VNPay callback
    this.route.queryParams.subscribe(params => {
      if (params['payment'] === 'success') {
        this.showPaymentSuccessMessage(
          params['orderId'],
          params['amount'],
          params['trackingNumber']
        );
        // Remove query params from URL
        this.router.navigate([], {
          queryParams: {},
          replaceUrl: true
        });
      }
    });
    
    this.loadOrders();
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

  loadOrders(): void {
    if (!this.userId) return;

    this.loading = true;
    this.orderService.getMyOrders(this.userId).subscribe({
      next: (orders) => {
        this.orders = orders;
        this.filterOrders();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.loading = false;
      }
    });
  }

  selectTab(tab: string): void {
    this.selectedTab = tab;
    this.filterOrders();
  }

  filterOrders(): void {
    if (this.selectedTab === 'ALL') {
      this.filteredOrders = this.orders;
    } else {
      this.filteredOrders = this.orders.filter(order => order.status === this.selectedTab);
    }
  }

  getStatusLabel(status: OrderStatus): string {
    const statusMap: Record<OrderStatus, string> = {
      [OrderStatus.PENDING]: 'Chờ xác nhận',
      [OrderStatus.PROCESSING]: 'Đang xử lý',
      [OrderStatus.ON_DELIVERY]: 'Đang giao hàng',
      [OrderStatus.DELIVERED]: 'Đã giao hàng',
      [OrderStatus.CANCELLED]: 'Đã hủy'
    };
    return statusMap[status] || status;
  }

  getStatusColor(status: OrderStatus): string {
    const colorMap: Record<OrderStatus, string> = {
      [OrderStatus.PENDING]: 'bg-yellow-100 text-yellow-800',
      [OrderStatus.PROCESSING]: 'bg-purple-100 text-purple-800',
      [OrderStatus.ON_DELIVERY]: 'bg-purple-100 text-purple-800',
      [OrderStatus.DELIVERED]: 'bg-green-100 text-green-800',
      [OrderStatus.CANCELLED]: 'bg-red-100 text-red-800'
    };
    return colorMap[status] || 'bg-gray-100 text-gray-800';
  }

  cancelOrder(orderId: number): void {
    Swal.fire({
      title: 'Hủy đơn hàng?',
      text: 'Bạn có chắc muốn hủy đơn hàng này?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Hủy đơn',
      cancelButtonText: 'Đóng'
    }).then((result) => {
      if (result.isConfirmed) {
        this.orderService.cancelOrder(orderId).subscribe({
          next: () => {
            Swal.fire({
              title: 'Đã hủy!',
              text: 'Đơn hàng đã được hủy thành công',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false
            });
            this.loadOrders();
          },
          error: (error) => {
            console.error('Error cancelling order:', error);
            Swal.fire({
              title: 'Lỗi!',
              text: error.error?.message || 'Không thể hủy đơn hàng',
              icon: 'error',
              confirmButtonColor: '#EF4444'
            });
          }
        });
      }
    });
  }

  canCancelOrder(status: OrderStatus): boolean {
    return status === OrderStatus.PENDING;
  }

  private showPaymentSuccessMessage(orderId: string, amount: string, trackingNumber: string): void {
    const amountInVND = parseFloat(amount);

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
      confirmButtonColor: '#F97316',
      confirmButtonText: 'Đã hiểu',
      customClass: {
        popup: 'rounded-xl shadow-lg'
      }
    });
  }
}
