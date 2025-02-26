package jp.co.sss.cytech.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sales_items")
@Getter
@Setter
public class SalesItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_item_id")
    private Integer saleItemId;
    
    @Column(name = "sale_name")
    private String saleName;

    @Column(name = "sales_img_path")
    private String salesImgPath;
    
    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "start_month")
    private LocalDate startMonth;

    @Column(name = "end_month")
    private LocalDate endMonth;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private ProductEntity product;
}
