package edu.hcmut.datn.back_office_service.messaging.productgeneral;

public record ProductGeneralCreatedEvent(
        Long prodGenId,
        String prodName,
        String imgUrl,
        String description,
        Long subSubcategoryId,  // Main field - what product actually links to
        Long categoryId         // Derived field - subcategory ID for ecommerce (parent of subSubcategory)
) {}