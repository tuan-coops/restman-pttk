package com.restman.model;

import java.util.List;

public record Combo3(int id, String code, String name, long price, List<ComboDish3> dishes) {
    public Combo3(int id, String code, String name, long price) {
        this(id, code, name, price, List.of());
    }
}

