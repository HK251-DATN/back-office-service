package edu.hcmut.datn.back_office_service.messaging.order;

import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;
import edu.hcmut.datn.back_office_service.dao.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderConfirmedEvent {

    private Long orderId;
    private String buyerId;
    private Long addressId;
    private String note;
    private Long totalPrice;
    private List<OrderItemInfo> orderItems;

    public record OrderItemInfo(String batchDetailId, Long quantity) {}

    public Order toOrderEntity() {
        Order newOrder = new Order();
        newOrder.setStatus(OrderStatus.CONFIRMED);
        return newOrder;
    }
}
