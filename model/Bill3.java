package com.restman.model;

import java.time.LocalDateTime;
import java.util.List;

public record Bill3(int id, String code, Customer3 customer, Table3 table, Seller3 seller,
        List<Order3> orders, long totalAmount, long discountAmount, long actualAmount,
        LocalDateTime paymentTime, String paymentMethod, String status, String note) {
}
