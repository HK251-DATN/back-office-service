package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_generals")
@NoArgsConstructor
public class Category {

    @Column(name = "category_id")
    @Id
    @Getter
    private Long categoryId;

    @Column(name = "name")
    @Getter
    @Setter
    private String name;

    @Column(name = "name")
    @Getter
    @Setter
    private String description;

    @Column(name = "name")
    private LocalDateTime createdAt;

    @Column(name = "name")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Set createdAt on first save
        updatedAt = LocalDateTime.now(); // Optional: Set initial updatedAt
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // Update on every save after creation
    }
}
