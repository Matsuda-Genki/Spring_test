package jp.co.sss.cytech.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private Integer productId;
    private String productName;
    private Integer price;
    private Integer includeTax;
    private String productImgPath;
    private Integer productStock;
    private Integer discountRate;
    private Integer discountedPrice;
    private Integer discountedIncludeTax;
    private Integer categoryId; 
    private Integer companyId;
    private String companyName;
    
    public ProductDTO(
    		Integer productId,
    		String productName,
    		Integer productStock, 
    		Integer price, 
    		Integer includeTax, 
    		String productImgPath, 
    		Integer categoryId, 
    		Integer companyId, 
    		String companyName) {
        this.productId = productId;
        this.productName = productName;
        this.productStock = productStock;
        this.price = price;
        this.includeTax = includeTax;
        this.productImgPath = productImgPath;
        this.categoryId = categoryId;
        this.companyId = companyId;
        this.companyName = companyName;
    }
}
