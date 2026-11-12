package com.restman.model;

public record ComboOrder3(int id, Combo3 combo, int quantity, long price) {
    public long amount() {
        return price * quantity;
    }
}
