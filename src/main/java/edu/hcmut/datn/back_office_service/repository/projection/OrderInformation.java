package edu.hcmut.datn.back_office_service.repository.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;

import java.time.LocalDateTime;

public interface OrderInformation {
    
    @JsonProperty("order_id")
    String getOrderId();
    
    @JsonProperty("owned_by")
    String getBuyerId();
    
    @JsonProperty("status")
    OrderStatus getOrderStatus();
    
    @JsonProperty("total_price")
    Long getTotalPrice();
    
    @JsonProperty("packaging_progress")
    Integer getPackagingProgress();
    
    @JsonProperty("f_name")
    String getBuyerFName();
    
    @JsonProperty("l_name")
    String getBuyerLName();
    
    @JsonProperty("email")
    String getBuyerEmail();
    
    @JsonProperty("updated_at")
    LocalDateTime getLastUpdate();
}
