import { Component, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { CartItem } from '../../models/cart.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, ButtonModule, InputNumberModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cart;

  totalPrice;

  constructor(public cartService: CartService) {
    this.cart = this.cartService.cart;
    this.totalPrice = computed(() => {
      const currentCart = this.cart();
      return currentCart?.items.reduce((sum, item) => sum + (item.product.price * item.quantity), 0) ?? 0;
    });
  }

  ngOnInit() {
    this.cartService.getCart().subscribe();
  }

  updateQuantity(item: CartItem, quantity: number) {
    if (item.id) {
      this.cartService.updateQuantity(item.id, quantity).subscribe();
    }
  }

  removeItem(item: CartItem) {
    if (item.id) {
      this.cartService.removeItem(item.id).subscribe();
    }
  }

  clearCart() {
    this.cartService.clearCart().subscribe();
  }

  checkout() {
    this.cartService.checkout().subscribe({
      next: (response) => {
        // Afficher un message de succès à l'utilisateur
        alert('Order placed successfully! Thank you for your purchase.');
      },
      error: (error) => {
        // Afficher un message d'erreur à l'utilisateur
        alert('Error placing order: ' + error.message);
      }
    });
  }
}
