package jp.co.sss.cytech.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer price;
    private Integer taxedPrice;
    private String productImgPath;

    // 全引数コンストラクター
    public OrderItemDTO(Integer productId,
    					String productName, 
    				    Integer quantity, 
    				    Integer price,
    				    Integer taxedPrice,
                        String productImgPath) {
    	this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.taxedPrice = taxedPrice;
        this.productImgPath = productImgPath;
    }
    
    // デフォルトコンストラクタ
    public OrderItemDTO() {}
}

