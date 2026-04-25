package com.experimentdemo.ecom.service;

import com.experimentdemo.ecom.entity.*;
import com.experimentdemo.ecom.dto.CartDTO;
import com.experimentdemo.ecom.dto.CartItemDTO;
import com.experimentdemo.ecom.repository.CartRepository;
import com.experimentdemo.ecom.repository.CartItemRepository;
import com.experimentdemo.ecom.repository.UserRepository;
import com.experimentdemo.ecom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public CartDTO getCartByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
        
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId).orElseThrow(() ->
            new RuntimeException("Product not found"));
        
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
        
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        cart.getItems().add(cartItem);
        cartRepository.save(cart);
        
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO removeFromCart(Long userId, Long cartItemId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user).orElseThrow(() ->
            new RuntimeException("Cart not found"));
        
        cartItemRepository.deleteById(cartItemId);
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
        
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user).orElseThrow(() ->
            new RuntimeException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() ->
            new RuntimeException("Cart item not found"));
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        return convertToDTO(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user).orElseThrow(() ->
            new RuntimeException("Cart not found"));
        
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getSubtotal(),
                    item.getProduct().getImageUrl()
                ))
                .collect(Collectors.toList());
        
        return new CartDTO(
            cart.getId(),
            items,
            cart.getTotalPrice(),
            items.size()
        );
    }
}