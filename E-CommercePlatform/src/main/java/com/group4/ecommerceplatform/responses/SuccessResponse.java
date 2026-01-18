package com.group4.ecommerceplatform.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuccessResponse {
    private String message;
    private Object data;

}
