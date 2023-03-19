package com.backend.qa.api.test.ResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponseDto {

  public String product_name;
  public int product_qty;
  public String cart_owner;
}


