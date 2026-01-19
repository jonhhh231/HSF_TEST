package com.group4.ecommerceplatform.responses.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageDataResponse <T> {
    private MetaPagination metaPagination;
    private List<T> data;
}
