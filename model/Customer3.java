package com.restman.model;

import java.io.Serializable;
import java.util.Objects;


public class Customer3 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String code;
    private String fullName;
    private String phone;
    private String email;

    public Customer3() {
    }

    public Customer3(int id, String code, String fullName, String phone, String email) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int id() {
        return id;
    }

    public String code() {
        return code;
    }

    public String fullName() {
        return fullName;
    }

    public String phone() {
        return phone;
    }

    public String email() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer3 customer3 = (Customer3) o;
        return id == customer3.id && Objects.equals(code, customer3.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Customer3{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
