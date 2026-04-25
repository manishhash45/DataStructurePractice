package com.experimentdemo.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {
    private Long id;
    private List<ProductDTO> products;
    private Integer productCount;
}