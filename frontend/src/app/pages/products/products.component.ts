import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { PaginatorModule } from 'primeng/paginator';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DataViewModule } from 'primeng/dataview';
import { RatingModule } from 'primeng/rating';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, PaginatorModule, ButtonModule, TagModule, DataViewModule, RatingModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {
  products = signal<Product[]>([]);
  totalRecords = 0;
  rows = 10;
  first = 0;

  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) { }

  ngOnInit() {
    this.loadProducts(0, this.rows);
  }

  loadProducts(page: number, size: number) {
    this.productService.getProducts(page, size).subscribe({
      next: (data: any) => {
        // Handle both Page<Product> and List<Product> responses
        if (data.content) {
          this.products.set(data.content);
          this.totalRecords = data.totalElements;
        } else {
          this.products.set(data);
          this.totalRecords = data.length;
        }
      },
      error: (err) => console.error('Error loading products', err)
    });
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
    this.loadProducts(event.page, event.rows);
  }

  addToCart(product: Product) {
    if (product.id) {
      this.cartService.addToCart({ productId: product.id, quantity: 1 }).subscribe({
        next: () => console.log('Added to cart'),
        error: (err) => console.error('Error adding to cart', err)
      });
    }
  }

  getSeverity(status: string): 'success' | 'secondary' | 'info' | 'warning' | 'danger' | 'contrast' | undefined {
    switch (status) {
      case 'INSTOCK':
        return 'success';
      case 'LOWSTOCK':
        return 'warning';
      case 'OUTOFSTOCK':
        return 'danger';
      default:
        return 'info';
    }
  }
}
