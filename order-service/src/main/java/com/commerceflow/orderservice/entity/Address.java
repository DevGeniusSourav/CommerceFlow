package com.commerceflow.orderservice.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    private String recipientName;

    private String street;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private String phone;
}