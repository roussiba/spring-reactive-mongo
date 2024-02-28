package com.genisys.springreactivemongo.utils;

import com.genisys.springreactivemongo.dto.ProductDto;
import com.genisys.springreactivemongo.entity.Product;
import org.springframework.beans.BeanUtils;

public class AppUtils {

    public static ProductDto mapToProductDto(Product product){
        return new ProductDto(
                product.getId(), product.getName(), product.getQty(), product.getPrice()
        );
    }

    public static Product mapToProductEntity(ProductDto dto){
        return Product.builder()
                .id(dto.id())
                .name(dto.name())
                .price(dto.price())
                .qty(dto.qty())
                .build();
    }
}
