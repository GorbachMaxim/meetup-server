package com.example.meetupserver.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Meetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String modified;

    private String start;

    private String finish;

    private String subject;

    @Column(columnDefinition="VARCHAR")
    private String image;

    @Column(columnDefinition="VARCHAR")
    private String excerpt;

    private String place;

    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "participants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meetup_id"))
    private Set<User> participants = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "voted_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meetup_id"))
    private Set<User> votedUsers = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author")
    private User author;
}
