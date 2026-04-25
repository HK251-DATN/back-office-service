package edu.hcmut.datn.back_office_service.service;

import java.util.List;

import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;
import edu.hcmut.datn.back_office_service.dao.Order;
import edu.hcmut.datn.back_office_service.repository.projection.OrderInformation;

public interface OrderService {
    Order create(Order order);

    Order read(Long orderId);

    List<Order> readAll(Integer pageNum, Integer pageSize);
    
    List<OrderInformation> adminReadAll(String status, Long packagingEmpId, Long deliveringEmpId, Long orderId);

    Order update(Long orderId, Order order);

    void delete(Long orderId);

    void empConfirmOrder(Long orderId, Long empId);
    
    void empPackageOrder(Long orderId, Long empId);
    
    void empShipOrder(Long orderId, Long empId);
    
    void updatePackagingProgress(Long orderId, int progress);
    
    void empDeliverOrder(Long orderId, Long empId);
}
