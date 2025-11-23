import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { BadgeModule } from 'primeng/badge';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, BadgeModule, ButtonModule, MenuModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {
  cartCount;
  menuItems: MenuItem[] = [];

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {
    this.cartCount = this.cartService.cartItemCount;
  }

  get currentUser() {
    return this.authService.currentUser;
  }

  ngOnInit(): void {
    this.cartService.refreshCart();
    this.initMenuItems();
  }

  private initMenuItems(): void {
    this.menuItems = [
      {
        label: 'Logout',
        icon: 'pi pi-sign-out',
        command: () => this.logout()
      }
    ];
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
