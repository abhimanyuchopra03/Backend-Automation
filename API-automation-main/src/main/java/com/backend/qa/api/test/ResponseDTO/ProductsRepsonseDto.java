package com.backend.qa.api.test.ResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductsRepsonseDto {
  public String product_name;
  public int product_qty;
  public String product_descr;

}
