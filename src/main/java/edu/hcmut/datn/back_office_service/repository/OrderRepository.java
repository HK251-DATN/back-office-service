package edu.hcmut.datn.back_office_service.repository;

import edu.hcmut.datn.back_office_service.repository.projection.OrderInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.hcmut.datn.back_office_service.dao.Order;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{
    
    @Query(value = """
            select
                o.order_id as orderId,
                o.owned_by as buyerId,
                o.status as orderStatus,
                o.total_price as totalPrice,
                u.f_name as buyerFName,
                u.l_name as buyerLName,
                u.email as buyerEmail,
                o.updated_at as lastUpdate
            from
                orders o
                    inner join users u on u.user_id = o.owned_by;
            """, nativeQuery = true)
    List<OrderInformation> getOrderInformationList();
}
