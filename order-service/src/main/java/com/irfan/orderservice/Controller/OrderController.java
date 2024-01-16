package com.irfan.orderservice.Controller;

import com.irfan.orderservice.Service.OrderService;
import java.util.List;

import lombok.RequiredArgsConstructor;

// import org.hibernate.mapping.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.irfan.orderservice.Dto.OrderRequest;
import com.irfan.orderservice.Dto.OrderLineItemsDto;

import com.irfan.orderservice.Model.Order;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public Order getProductById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PutMapping("{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public Order updateProductById(@PathVariable Long orderId, @RequestBody OrderRequest orderRequest) {
        return orderService.updateProductById(orderId, orderRequest);
    }

}
