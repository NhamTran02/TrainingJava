import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { OrderService } from '../../services/order.service';
import { OrderDetailView, OrderStatus } from '../../models/order';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.css'
})
export class OrderDetailComponent implements OnInit {
  orderDetail: OrderDetailView | null = null;
  loading = true;
  orderId: number = 0;
  userId: number | null = null;

  OrderStatus = OrderStatus;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.userId = this.getUserId();
    if (!this.userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.route.params.subscribe(params => {
      this.orderId = +params['id'];
      if (this.orderId && this.userId) {
        this.loadOrderDetail();
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

  loadOrderDetail(): void {
    if (!this.userId) return;

    this.loading = true;
    this.orderService.getOrderDetail(this.orderId, this.userId).subscribe({
      next: (detail) => {
        this.orderDetail = detail;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading order detail:', error);
        this.loading = false;
        Swal.fire({
          title: 'Lỗi!',
          text: 'Không thể tải thông tin đơn hàng',
          icon: 'error',
          confirmButtonColor: '#EE4D2D'
        }).then(() => {
          this.router.navigate(['/orders']);
        });
      }
    });
  }

  getStatusLabel(status: string): string {
    const statusMap: Record<string, string> = {
      'PENDING': 'Chờ xác nhận',
      'PROCESSING': 'Đang xử lý',
      'SHIPPING': 'Đang giao hàng',
      'DELIVERED': 'Đã giao hàng',
      'CANCELLED': 'Đã hủy',
      'RETURNED': 'Đã trả hàng'
    };
    return statusMap[status] || status;
  }

  getStatusColor(status: string): string {
    const colorMap: Record<string, string> = {
      'PENDING': 'bg-yellow-100 text-yellow-800',
      'PROCESSING': 'bg-purple-100 text-purple-800',
      'SHIPPING': 'bg-indigo-100 text-indigo-800',
      'DELIVERED': 'bg-green-100 text-green-800',
      'CANCELLED': 'bg-red-100 text-red-800',
      'RETURNED': 'bg-gray-100 text-gray-800'
    };
    return colorMap[status] || 'bg-gray-100 text-gray-800';
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }
}
