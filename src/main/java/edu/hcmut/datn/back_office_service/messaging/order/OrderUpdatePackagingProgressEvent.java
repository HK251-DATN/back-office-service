package edu.hcmut.datn.back_office_service.messaging.order;

import edu.hcmut.datn.back_office_service.dao.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderUpdatePackagingProgressEvent {
    private Long orderId;
    private Integer packagingProgress;
    
    public Order toOrderEntity() {
        Order order = new Order();
        
        order.setPackagingProgress(packagingProgress);
        
        return order;
    }
    
}
