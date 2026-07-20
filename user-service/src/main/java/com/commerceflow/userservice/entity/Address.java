package com.commerceflow.userservice.entity;

import com.commerceflow.userservice.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private String street;

    private String apartmentNumber;

    private String landmark;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @Column(nullable = false)
    private boolean defaultAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Setter(AccessLevel.PROTECTED)
    private User user;

    public static Address create(String recipientName, String recipientPhone,  String street, String apartmentNumber, String landmark, String city, String state, String country, String postalCode, AddressType addressType){
        return new Address(recipientName, recipientPhone, street, apartmentNumber, landmark, city, state, country, postalCode, addressType);
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Address(String recipientName, String recipientPhone,  String street, String apartmentNumber, String landmark, String city, String state, String country, String postalCode, AddressType addressType) {
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.street = street;
        this.apartmentNumber = apartmentNumber;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.addressType = addressType;
    }
}
