import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'product/:id',
    loadComponent: () =>
      import('./pages/product-detail/product-detail.component').then((m) => m.ProductDetailComponent),
  },
  {
    path: 'cart',
    loadComponent: () =>
      import('./pages/cart/cart.component').then((m) => m.CartComponent),
  },
  {
    path: 'checkout',
    loadComponent: () =>
      import('./pages/checkout/checkout.component').then((m) => m.CheckoutComponent),
  },
  {
    path: 'orders',
    loadComponent: () =>
      import('./pages/orders/orders.component').then((m) => m.OrdersComponent),
  },
  {
    path: 'orders/:id',
    loadComponent: () =>
      import('./pages/order-detail/order-detail.component').then((m) => m.OrderDetailComponent),
  },
  {
    path: 'payment/callback',
    loadComponent: () =>
      import('./pages/payment-callback/payment-callback.component').then((m) => m.PaymentCallbackComponent),
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./pages/profile/profile.component').then((m) => m.ProfileComponent),
  },
  {
    path: 'products',
    loadComponent: () =>
      import('./pages/categories/categories.component').then((m) => m.CategoriesComponent),
  },
  {
    path: 'wishlist',
    loadComponent: () =>
      import('./pages/wishlist/wishlist.component').then((m) => m.WishlistComponent),
  },
  {
    path: 'promotions',
    loadComponent: () =>
      import('./pages/promotions/promotions.component').then((m) => m.PromotionsComponent),
  },
  {
    path: 'contact',
    loadComponent: () =>
      import('./pages/contact/contact.component').then((m) => m.ContactComponent),
  },
  {
    path: 'about',
    loadComponent: () =>
      import('./pages/about/about.component').then((m) => m.AboutComponent),
  },
  {
    path: 'search',
    loadComponent: () =>
      import('./pages/search/search.component').then((m) => m.SearchComponent),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'admin/users',
    loadComponent: () =>
      import('./pages/admin/user-management/user-management.component').then((m) => m.UserManagementComponent),
  },
  {
    path: 'admin/orders',
    loadComponent: () =>
      import('./pages/admin/order-management/order-management.component').then((m) => m.OrderManagementComponent),
  },
  {
    path: 'admin/products',
    loadComponent: () =>
      import('./pages/admin/product-management/product-management.component').then((m) => m.ProductManagementComponent),
  },
  {
    path: 'admin/products/new',
    loadComponent: () =>
      import('./pages/admin/product-form/product-form.component').then((m) => m.ProductFormComponent),
  },
  {
    path: 'admin/products/edit/:id',
    loadComponent: () =>
      import('./pages/admin/product-form/product-form.component').then((m) => m.ProductFormComponent),
  },
  {
    path: '**',
    redirectTo: '',
  }
];
