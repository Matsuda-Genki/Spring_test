package jp.co.sss.cytech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "img_path")
	private String productImgPath;
	
	@Column(name = "include_tax")
	private Integer includeTax;
	
	@Column(name = "stock")
	private Integer productStock;	
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private CategoryEntity category;
	
	@ManyToOne
	@JoinColumn(name = "company_id", referencedColumnName = "company_id", insertable = false, updatable = false)
	private CompanyEntity company;
}
