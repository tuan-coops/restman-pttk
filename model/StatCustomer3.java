package com.restman.model;

import java.io.Serializable;
import java.util.Objects;


public class StatCustomer3 extends Customer3 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalOrders;
    private long totalSpent;

    public StatCustomer3() {
        super();
    }

    public StatCustomer3(int id, String code, String fullName, String phone, String email, int totalOrders, long totalSpent) {
        super(id, code, fullName, phone, email);
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
    }

    public StatCustomer3(Customer3 customer, int totalOrders, long totalSpent) {
        super(customer != null ? customer.getId() : 0,
              customer != null ? customer.getCode() : "",
              customer != null ? customer.getFullName() : "",
              customer != null ? customer.getPhone() : "",
              customer != null ? customer.getEmail() : "");
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(long totalSpent) {
        this.totalSpent = totalSpent;
    }

    public int totalOrders() {
        return totalOrders;
    }

    public long totalSpent() {
        return totalSpent;
    }


    public Customer3 customer() {
        return this;
    }

    public Customer3 getCustomer() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StatCustomer3 that = (StatCustomer3) o;
        return totalOrders == that.totalOrders && totalSpent == that.totalSpent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), totalOrders, totalSpent);
    }

    @Override
    public String toString() {
        return "StatCustomer3{" +
                "id=" + getId() +
                ", code='" + getCode() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", totalOrders=" + totalOrders +
                ", totalSpent=" + totalSpent +
                '}';
    }
}
