package jp.co.sss.cytech.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private Integer productId;
    private String productName;
    private String companyName;
    private Integer price;
    private Integer includeTax;
    private String productImgPath;
    private Integer productStock;
    private Integer categoryId; 
    
    public ProductDTO(Integer productId, String productName, Integer productStock, Integer price, Integer includeTax, String productImgPath, Integer categoryId) {
        this.productId = productId;
        this.productName = productName;
        this.productStock = productStock;
        this.price = price;
        this.includeTax = includeTax;
        this.productImgPath = productImgPath;
        this.categoryId = categoryId;
    }
}
