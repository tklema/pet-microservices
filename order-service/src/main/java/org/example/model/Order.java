package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Long count;

    @Column(name = "user_id")
    private Long userId;
    private Date creationDate;

    public Order(String name, Long count, Long userId) {
        this.name = name;
        this.count = count;
        this.userId = userId;
        this.creationDate = new Date();
    }
}
