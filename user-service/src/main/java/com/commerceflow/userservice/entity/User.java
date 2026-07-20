package com.commerceflow.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
    private List<Address> addresses = new ArrayList<>();

    public static User create(String firstName, String lastName, String email, String password) {
        User newUser = new User(firstName, lastName, email, password);
        newUser.createdAt = Instant.now();
        newUser.updatedAt = Instant.now();
        return newUser;
    }

    public void addAddress(Address address) {
        address.setUser(this);
        this.addresses.add(address);
    }

    public void removeAddress(Address address) {
        address.setUser(null);
        this.addresses.remove(address);
    }

    @Builder(access = AccessLevel.PRIVATE)
    private User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
