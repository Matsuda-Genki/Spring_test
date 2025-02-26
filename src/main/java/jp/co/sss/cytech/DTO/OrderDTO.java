package jp.co.sss.cytech.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private Integer orderId;
    private String orderDate;
    private String paymentMethod;
    private String pickupLocation;
    
    private List<OrderItemDTO> items;
    private Integer totalAmount;
    private String recipientName;
    private String address;
    private String apartment;
    private String fullAddress; // 追加
    private Integer totalQuantity;
    
    public OrderDTO(Integer orderId,
		            String orderDate,
		            String paymentMethod,
		            String pickupLocation,
		            List<OrderItemDTO> items,
		            Integer totalAmount,
		            String fullAddress) {
    	
		 this.orderId = orderId;
		 this.orderDate = orderDate;
		 this.paymentMethod = paymentMethod;
		 this.pickupLocation = pickupLocation;
		 this.items = items;
		 this.totalAmount = totalAmount;
		 this.fullAddress = fullAddress;
    }
    
    // コンストラクタ
    public OrderDTO() {}

    // getter/setter
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
    
    public String getFullAddress() { return this.fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
}
