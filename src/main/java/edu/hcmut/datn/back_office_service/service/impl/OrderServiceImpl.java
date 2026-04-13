package edu.hcmut.datn.back_office_service.service.impl;

import java.util.List;

import edu.hcmut.datn.back_office_service.repository.projection.OrderInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.hcmut.datn.back_office_service.dao.Order;
import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;

import edu.hcmut.datn.back_office_service.messaging.order.OrderEventProducer;
import jakarta.transaction.Transactional;
import edu.hcmut.datn.back_office_service.exception.order.OrderNotFoundException;
import edu.hcmut.datn.back_office_service.repository.OrderRepository;
import edu.hcmut.datn.back_office_service.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order read(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
    }

    @Override
    public List<Order> readAll(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<Order> page = orderRepository.findAll(pageable);

        return page.toList();
    }
    
    @Override
    public List<OrderInformation> adminReadAll () {
        return orderRepository.getOrderInformationList();
    }
    
    @Override
    public Order update(Long orderId, Order order) {
        Order cur = read(orderId);

        if (order.getStatus() != null) {
            cur.setStatus(order.getStatus());
        }

        if (order.getConfirmedBy() != null) {
            cur.setConfirmedBy(order.getConfirmedBy());
        }

        if (order.getPackagedBy() != null) {
            cur.setPackagedBy(order.getPackagedBy());
        }

        if (order.getShippedBy() != null) {
            cur.setShippedBy(order.getShippedBy());
        }
        
        if (order.getPackagingProgress() != null) {
            cur.setPackagingProgress(order.getPackagingProgress());
            
            if (order.getPackagingProgress() == 100) {
                cur.setStatus(OrderStatus.READY_FOR_PICKUP);
            }
        }

        return orderRepository.save(cur);
    }

    @Override
    public void delete(Long orderId) {
        orderRepository.delete(read(orderId));
    }

    @Override
    @Transactional
    public void empConfirmOrder(Long orderId, Long empId) {
        Order order = read(orderId);
        
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Order must be in CREATED status to be confirmed. Current status: " + order.getStatus());
        }

        order.setConfirmedBy(empId);
        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void empPackageOrder(Long orderId, Long empId) {
        Order order = read(orderId);
        
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order must be in CONFIRMED status to be packaged. Current status: " + order.getStatus());
        }

        order.setPackagedBy(empId);
        order.setStatus(OrderStatus.PACKING);

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void empShipOrder(Long orderId, Long empId) {
        Order order = read(orderId);
        
        if (order.getStatus() != OrderStatus.PACKING && order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new RuntimeException("Order must be in PACKING or READY_FOR_PICKUP status to be shipped. Current status: " + order.getStatus());
        }

        order.setShippedBy(empId);
        order.setStatus(OrderStatus.SHIPPING);

        orderRepository.save(order);

        orderEventProducer.publishOrderDelivering(new edu.hcmut.datn.back_office_service.messaging.order.OrderDeliveringEvent(orderId));
    }

    @Override
    @Transactional
    public void updatePackagingProgress(Long orderId, int progress) {
        Order order = read(orderId);

        if (order.getStatus() != OrderStatus.PACKING) {
            throw new RuntimeException("Order must be in PACKING status to update progress. Current status: " + order.getStatus());
        }

        order.setPackagingProgress(progress);

        if (progress == 100) {
            order.setStatus(OrderStatus.READY_FOR_PICKUP);
        }

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void empDeliverOrder(Long orderId, Long empId) {
        Order order = read(orderId);

        if (order.getStatus() != OrderStatus.SHIPPING) {
            throw new RuntimeException("Order must be in SHIPPING status to be delivered. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.DELIVERY);

        orderRepository.save(order);

        orderEventProducer.publishOrderDelivered(new edu.hcmut.datn.back_office_service.messaging.order.OrderDeliveredEvent(orderId));
    }
}
