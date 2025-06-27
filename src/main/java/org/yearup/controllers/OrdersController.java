package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrdersController
{
    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final ShoppingCartDao cartDao;
    private final UserDao userDao;
    private final ProfileDao profileDao;

    public OrdersController(OrderDao orderDao,
                           OrderLineItemDao orderLineItemDao,
                           ShoppingCartDao cartDao,
                           UserDao userDao,
                           ProfileDao profileDao)
    {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.cartDao = cartDao;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @PostMapping
    public ResponseEntity<String> checkout(Principal principal)
    {
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            int userId = user.getId();


            Profile profile = profileDao.getProfileByUserId(userId);
            if (profile == null) {
                return ResponseEntity.badRequest().body("Profile not found. Please update your profile before checking out.");
            }

            ShoppingCart cart = cartDao.getByUserId(userId);
            Map<Integer, ShoppingCartItem> cartItems = cart.getItems();
            if (cartItems == null || cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body("Your cart is empty.");
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setDateTime(LocalDateTime.now());
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());
            order.setShippingAmount(new BigDecimal("5.99"));

            int orderId = orderDao.createOrder(order);


            for (ShoppingCartItem item : cartItems.values()) {
                OrderLineItem lineItem = new OrderLineItem();
                lineItem.setOrderId(orderId);
                lineItem.setProductId(item.getProduct().getProductId());
                lineItem.setSalesPrice(item.getProduct().getPrice());
                lineItem.setQuantity(item.getQuantity());
                lineItem.setDiscount(BigDecimal.ZERO);

                orderLineItemDao.addLineItem(lineItem);
            }

            cartDao.clearCart(userId);

            return ResponseEntity.ok("Order placed successfully. Order ID: " + orderId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Checkout failed. Please try again.");
        }
    }
}
