package com.example.meetupserver.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String username;
    private String name;

    private String surname;
    private String email;

    private String post;

    @Column(name = "is_verified")
    private boolean isVerified;

    @JsonIgnore
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();



    public User() {
        isVerified = false;
    }

    public User(String username, String name, String surname, String post, String email, String password) {

        this.username = username;
        this.name = name;
        this.surname = surname;
        this.post = post;
        this.email = email;
        this.password = password;
        isVerified = false;
    }

    public User(String username, String name, String surname, String post, String email, String password, Set<Role> set) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.post = post;
        this.email = email;
        this.password = password;
        this.roles = set;
        isVerified = false;
    }




}
