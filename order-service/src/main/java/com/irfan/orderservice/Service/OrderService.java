package com.irfan.orderservice.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.irfan.orderservice.Repository.OrderRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.irfan.orderservice.Dto.OrderLineItemsDto;
import com.irfan.orderservice.Dto.OrderRequest;
import com.irfan.orderservice.Exception.OrderNotFoundException;
import com.irfan.orderservice.Model.Order;
import com.irfan.orderservice.Model.OrderLineItems;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        log.info("Retrieved orders list:");
        for (Order order : orders) {
            log.info("Order Number: {}", order.getOrderNumber());
        }

        return orders;
    }

    public Order getOrderById(Long orderId) {
        Optional<Order> getOrder = orderRepository.findById(orderId);

        if (getOrder.isPresent()) {
            Order order = getOrder.get();
            log.info("Retrieved Order by ID {}: {}", orderId, order);
            return order;
        } else {
            throw new OrderNotFoundException("Order not found with ID: " + orderId);
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;

    }

    public Order updateProductById(Long orderId, OrderRequest orderRequest) {
        // Check if the order with the given orderId exists
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            // Order found, update its fields
            Order existingOrder = optionalOrder.get();

            List<OrderLineItems> updatedOrderLineItems = orderRequest.getOrderLineItemsDtoList()
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            existingOrder.setOrderLineItemsList(updatedOrderLineItems);

            // Log the update
            log.info("Updated product by ID {}: {}", orderId, existingOrder);

            // Save the updated order
            return orderRepository.save(existingOrder);

        } else {
            // Handle the case where the order doesn't exist, throw an exception or return null, depending on your design
            throw new OrderNotFoundException("Order with ID " + orderId + " not found");
        }
    }



}
