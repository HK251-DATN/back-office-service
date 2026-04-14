package edu.hcmut.datn.back_office_service.repository;

import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;
import edu.hcmut.datn.back_office_service.repository.projection.OrderInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.hcmut.datn.back_office_service.dao.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{
    
    @Query(value = """
            select
                o.order_id as orderId,
                o.owned_by as buyerId,
                o.status as orderStatus,
                o.total_price as totalPrice,
                o.packaging_progress as packagingProgress,
                u.f_name as buyerFName,
                u.l_name as buyerLName,
                u.email as buyerEmail,
                o.updated_at as lastUpdate
            from
                orders o
                    inner join users u on u.user_id = o.owned_by
            where
                    (:status = '' OR o.status = :status)
                AND (:packagingEmpId = 0 OR o.packaged_by = :packagingEmpId)
                AND (:deliveringEmpId = 0 OR o.packaged_by = :deliveringEmpId)
                AND (:order_id = 0 OR o.order_id = :order_id)
            """, nativeQuery = true)
    List<OrderInformation> getOrderInformationList(
            @Param("status") String status,
            @Param("packagingEmpId") Long packagingEmpId,
            @Param("deliveringEmpId") Long deliveringEmpId,
            @Param("order_id") Long orderId
    );
}
