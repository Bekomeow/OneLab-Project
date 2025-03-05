package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"registrations", "tickets"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
}
