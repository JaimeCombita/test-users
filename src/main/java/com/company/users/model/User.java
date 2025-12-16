package com.company.users.model;

import com.company.users.crosscutting.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "USERS")
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "identification_number", updatable = false, nullable = false, unique = true)
    private String identificationNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Phone> phones;

    @Column
    private LocalDateTime created;

    @Column
    private LocalDateTime modified;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "allow_multisession")
    private Boolean allowMultisession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles rol;

}
