package jp.co.sss.cytech.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "order_id")
	 private Integer orderId;
	 
	 @ManyToOne
	 @JoinColumn(name = "user_id",
	         	 referencedColumnName = "user_id",
	         	 foreignKey = @ForeignKey(name = "fk_orders_users")
			 	)
	 
	 private UserEntity user;
	
	 @Column(name = "created_at")
	 private LocalDateTime orderDate;
	 
	 @Column(name = "payment_method")
	 private String paymentMethod;
	 
	 @Column(name = "pickup_location")
	 private String pickupLocation;
	 
	 @Column(name = "recipient_name")
	 private String recipientName;

	 @Column(name = "address")
	 private String address;

	 @Column(name = "apartment")
	 private String apartment;
	 
	 @Column(name = "status", nullable = false)
	 private String status = "PENDING";
	 
	 @Column(name = "total_amount", nullable = false)
	 private Integer totalAmount;
	 public void calculateTotalAmount() {
	        this.totalAmount = items.stream()
	            .mapToInt(item -> item.getQuantity() * item.getTaxedPrice())
	            .sum();
	 }
	 
	 @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	 private List<OrderItemEntity> items = new ArrayList<>(); 
	 
	 public void addItem(OrderItemEntity item) {
	        items.add(item);
	        item.setOrder(this);
	 }
	
	 // Getter and Setter
	 public List<OrderItemEntity> getItems() {
	     return items;
	 }
	
	 public void setItems(List<OrderItemEntity> items) {
	     this.items = items;
	 }
 }

