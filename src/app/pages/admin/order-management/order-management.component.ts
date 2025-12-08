import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { OrderResponse, OrderStatus } from '../../../models/order';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.css']
})

export class OrderManagementComponent implements OnInit {
  orders: OrderResponse[] = [];
  filteredOrders: OrderResponse[] = [];
  loading = false;
  searchTerm = '';
  selectedStatus = '';
  
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  statusOptions = [
    { value: '',label: 'Tất cả'},
    { value: 'PENDING', label: 'Chờ xác nhận' },
    { value: 'PROCESSING', label: 'Đã xác nhận' },
    { value: 'ON_DELIVERY', label: 'Đang giao' },
    { value: 'DELIVERED', label: 'Đã giao' },
    { value: 'CANCELLED', label: 'Đã hủy' }
  ];

  constructor( 
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getAllOrders(this.currentPage, this.pageSize, 'createdAt', 'DESC').subscribe({
      next: (response) => {
        this.orders = response.content;
        this.filteredOrders = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        Swal.fire('Lỗi', 'Không thể tải danh sách đơn hàng', 'error');
        this.loading = false;
      }
    });
  }

  filterOrders(): void {
    let filtered = this.orders;

    // Filter by status
    if (this.selectedStatus) {
      filtered = filtered.filter(order => order.status === this.selectedStatus);
    }

    // Filter by search term
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(order =>
        order.id?.toString().includes(term) ||
        order.trackingNumber?.toLowerCase().includes(term)
      );
    }

    this.filteredOrders = filtered;
  }

  updateOrderStatus(order: OrderResponse, newStatus: string): void {
    const statusLabel = this.statusOptions.find(s => s.value === newStatus)?.label || newStatus;
    
    Swal.fire({
      title: 'Xác nhận cập nhật',
      text: `Cập nhật trạng thái đơn hàng #${order.id} thành "${statusLabel}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#f97316',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Cập nhật',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        this.orderService.updateOrderStatus(order.id, newStatus).subscribe({
          next: (updatedOrder) => {
            const index = this.orders.findIndex(o => o.id === updatedOrder.id);
            if (index !== -1) {
              this.orders[index] = updatedOrder;
              this.filterOrders();
            }
            Swal.fire('Thành công', 'Cập nhật trạng thái đơn hàng thành công', 'success');
          },
          error: (error) => {
            console.error('Error updating order:', error);
            Swal.fire('Lỗi', error.error?.message || 'Không thể cập nhật đơn hàng', 'error');
          }
        });
      }
    });
  }

  cancelOrder(order: OrderResponse): void {
    Swal.fire({
      title: 'Xác nhận hủy đơn',
      text: `Bạn có chắc muốn hủy đơn hàng #${order.id}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Hủy đơn',
      cancelButtonText: 'Đóng'
    }).then((result) => {
      if (result.isConfirmed) {
        this.orderService.cancelOrder(order.id).subscribe({
          next: () => {
            const index = this.orders.findIndex(o => o.id === order.id);
            if (index !== -1) {
              this.orders[index].status = OrderStatus.CANCELLED;
              this.filterOrders();
            }
            Swal.fire('Đã hủy', 'Đơn hàng đã được hủy', 'success');
          },
          error: (error) => {
            console.error('Error cancelling order:', error);
            Swal.fire('Lỗi', error.error?.message || 'Không thể hủy đơn hàng', 'error');
          }
        });
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'bg-yellow-100 text-yellow-800',
      'PROCESSING': 'bg-blue-100 text-blue-800',
      'ON_DELIVERY': 'bg-purple-100 text-purple-800',
      'DELIVERED': 'bg-green-100 text-green-800',
      'CANCELLED': 'bg-red-100 text-red-800'
    };
    return statusMap[status] || 'bg-gray-100 text-gray-800';
  }

  getStatusLabel(status: string): string {
    const option = this.statusOptions.find(s => s.value === status);
    return option?.label || status;
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString('vi-VN');
  }

  goToOrderDetail(orderId: number): void {
    this.router.navigate(['/orders', orderId]);
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadOrders();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadOrders();
    }
  }
}
