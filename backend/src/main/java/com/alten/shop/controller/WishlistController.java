package com.alten.shop.controller;

import com.alten.shop.model.Wishlist;
import com.alten.shop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<Wishlist> getWishlist(Authentication authentication) {
        Wishlist wishlist = wishlistService.getOrCreateWishlist(authentication.getName());
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/items")
    public ResponseEntity<Wishlist> addToWishlist(
            @RequestParam Long productId,
            Authentication authentication) {
        Wishlist wishlist = wishlistService.addToWishlist(authentication.getName(), productId);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Wishlist> removeFromWishlist(
            @PathVariable Long itemId,
            Authentication authentication) {
        Wishlist wishlist = wishlistService.removeFromWishlist(authentication.getName(), itemId);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearWishlist(Authentication authentication) {
        wishlistService.clearWishlist(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
