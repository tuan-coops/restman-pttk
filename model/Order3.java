package com.restman.model;

import java.time.LocalDateTime;
import java.util.List;

public record Order3(int id, String code, LocalDateTime orderTime, String status,
        Seller3 seller, List<DishOrder3> dishOrders, List<ComboOrder3> comboOrders) {
}
