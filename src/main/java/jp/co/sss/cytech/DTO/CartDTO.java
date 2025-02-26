package jp.co.sss.cytech.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {
	private Integer productId;
    private String productName;
    private Integer price;
    private Integer quantity;

    // デフォルトコンストラクタ
    public CartDTO() {}

    // コンストラクタ
    public CartDTO(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // ゲッターとセッター
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
