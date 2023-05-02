package com.example.meetupserver.dto.entityDTO;

import com.example.meetupserver.model.Meetup;
import com.example.meetupserver.model.User;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
public class MeetupDTO {
    private long id;

    private String modified;

    private String start;

    private String finish;

    private String subject;

    private String excerpt;

    private String place;

    private String status;

    private String image;

    private int participantsCount;


    private int votedUsersCount;


    private User author;


    MeetupDTO() {};

    public MeetupDTO(Meetup meetup){
        this.id = meetup.getId();
        this.modified = meetup.getModified();
        this.start = meetup.getStart();
        this.finish = meetup.getFinish();
        this.subject = meetup.getSubject();
        this.excerpt = meetup.getExcerpt();
        this.place = meetup.getPlace();
        this.status = meetup.getStatus();
        this.image = meetup.getImage();
        if(meetup.getParticipants() == null)
            this.participantsCount=0;
        else
            this.participantsCount = meetup.getParticipants().size();

        if(meetup.getVotedUsers() == null)
            this.votedUsersCount=0;
        else
            this.votedUsersCount = meetup.getVotedUsers().size();



        this.author = meetup.getAuthor();
        author.setPassword(null);
    }

}
