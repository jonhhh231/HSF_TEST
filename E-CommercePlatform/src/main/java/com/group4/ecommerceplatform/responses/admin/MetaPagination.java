package com.group4.ecommerceplatform.responses.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetaPagination {
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
