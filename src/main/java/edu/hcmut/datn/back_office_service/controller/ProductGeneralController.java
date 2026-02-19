package edu.hcmut.datn.back_office_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.hcmut.datn.back_office_service.dao.ProductGeneral;
import edu.hcmut.datn.back_office_service.dto.request.ProductGeneralCreateRequest;
import edu.hcmut.datn.back_office_service.dto.request.ProductGeneralUpdateRequest;
import edu.hcmut.datn.back_office_service.dto.response.ApiResponse;
import edu.hcmut.datn.back_office_service.service.ProductGeneralService;

@Controller
@RequestMapping("/api/product-general")
public class ProductGeneralController {

    private final ProductGeneralService productGeneralService;

    public ProductGeneralController(ProductGeneralService productGeneralService) {
        this.productGeneralService = productGeneralService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductGeneral>> create(@RequestBody ProductGeneralCreateRequest request) {
        try {
            ProductGeneral created = productGeneralService.create(request.toEntity());

            return ResponseEntity.ok().body(
                    ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Create product general successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping("/{prodGenId}")
    public ResponseEntity<ApiResponse<ProductGeneral>> read(@PathVariable Long prodGenId) {
        try {
            ProductGeneral pg = productGeneralService.read(prodGenId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Read product general successfully", pg));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductGeneral>>> readAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        List<ProductGeneral> list = productGeneralService.readAll(pageNum, pageSize);

        if (list.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No Product General Exists", null));
        }

        return ResponseEntity.ok()
                .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get all product generals successfully", list));
    }

    @PutMapping("/{prodGenId}")
    public ResponseEntity<ApiResponse<ProductGeneral>> update(@PathVariable Long prodGenId,
            @RequestBody ProductGeneralUpdateRequest request) {
        try {
            ProductGeneral updated = productGeneralService.update(prodGenId, request.toEntity());

            return ResponseEntity.ok().body(
                    ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Update product general successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/{prodGenId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long prodGenId) {
        try {
            productGeneralService.delete(prodGenId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Delete product general successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }
}
