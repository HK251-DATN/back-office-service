package edu.hcmut.datn.back_office_service.controller;

import java.util.List;

import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;
import edu.hcmut.datn.back_office_service.repository.projection.OrderInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.hcmut.datn.back_office_service.dao.Order;
import edu.hcmut.datn.back_office_service.dto.request.OrderCreateRequest;
import edu.hcmut.datn.back_office_service.dto.request.OrderUpdateRequest;
import edu.hcmut.datn.back_office_service.dto.response.ApiResponse;
import edu.hcmut.datn.back_office_service.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import edu.hcmut.datn.back_office_service.security.portable.AuthenticatedUser;

@Controller
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long getCurrentUserId() {
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> create(@RequestBody OrderCreateRequest request) {
        try {
            Order newOrder = orderService.create(request.toEntity());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Create order successfully", newOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> read(@PathVariable Long orderId) {
        try {
            Order order = orderService.read(orderId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Read order successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<OrderInformation>>> readAll(
            @RequestParam(defaultValue = "") String status
            ) {
        List<OrderInformation> orders = orderService.adminReadAll(status, 0L, 0L,0L);

        if (orders.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No Order Exists", null));
        }

        return ResponseEntity.ok()
                .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get all orders successfully", orders));
    }
    
    @GetMapping("/admin/{orderId}")
    public ResponseEntity<ApiResponse<List<OrderInformation>>> adminRead(
            @PathVariable Long orderId
    ) {
        List<OrderInformation> orders = orderService.adminReadAll("", 0L, 0L, orderId);
        
        if (orders.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No Order Exists", null));
        }
        
        return ResponseEntity.ok()
                .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get all orders successfully", orders));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> update(@PathVariable Long orderId,
            @RequestBody OrderUpdateRequest request) {
        try {
            Order updated = orderService.update(orderId, request.toEntity());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Update order successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long orderId) {
        try {
            orderService.delete(orderId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Delete order successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmOrder(@PathVariable Long orderId) {
        try {
            orderService.empConfirmOrder(orderId, getCurrentUserId());
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Confirm order successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/package")
    public ResponseEntity<ApiResponse<Void>> packageOrder(@PathVariable Long orderId) {
        try {
            orderService.empPackageOrder(orderId, getCurrentUserId());
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Package order successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<ApiResponse<Void>> shipOrder(@PathVariable Long orderId) {
        try {
            orderService.empShipOrder(orderId, getCurrentUserId());
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Ship order successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/progress")
    public ResponseEntity<ApiResponse<Void>> updatePackagingProgress(@PathVariable Long orderId, @RequestBody edu.hcmut.datn.back_office_service.dto.request.PackagingProgressUpdateRequest request) {
        try {
            orderService.updatePackagingProgress(orderId, request.getProgress());
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Update packaging progress successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<ApiResponse<Void>> deliverOrder(@PathVariable Long orderId) {
        try {
            orderService.empDeliverOrder(orderId, getCurrentUserId());
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Order delivered successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }
    
    @GetMapping("/emp/packaging-tasks")
    public ResponseEntity<?> empGetPackageTask(
            @AuthenticationPrincipal AuthenticatedUser principle
    ) {
        Long packagingEmpId = principle.getId();
        List<OrderInformation> orders = orderService.adminReadAll("PACKING", packagingEmpId, 0L, 0L);
        
        if (orders.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No Order Exists", null));
        }
        
        return ResponseEntity.ok()
                .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get all orders successfully", orders));
    }
    
    @GetMapping("/emp/delivering-tasks")
    public ResponseEntity<?> empGetDeliverTask(
            @AuthenticationPrincipal AuthenticatedUser principle
    ) {
        Long deliveryEmpId = principle.getId();
        List<OrderInformation> orders = orderService.adminReadAll("SHIPPING", 0L, deliveryEmpId, 0L);
        
        if (orders.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No Order Exists", null));
        }
        
        return ResponseEntity.ok()
                .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get all orders successfully", orders));
    }
    
    
    
    
}
