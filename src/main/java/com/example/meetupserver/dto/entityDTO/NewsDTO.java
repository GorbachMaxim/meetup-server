package com.example.meetupserver.dto.entityDTO;

import com.example.meetupserver.model.Meetup;
import com.example.meetupserver.model.News;
import com.example.meetupserver.model.User;
import lombok.Data;

import javax.persistence.*;

@Data
public class NewsDTO {
    private long id;

    private String publicationDate;

    private String title;


    private String text;


    private String image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author")
    private User author;


    public NewsDTO(News news){
        this.id = news.getId();
        this.publicationDate = news.getPublicationDate();
        this.title = news.getTitle();
        this.text = news.getText();
        this.image = news.getImage();
        this.author = news.getAuthor();
        author.setPassword(null);
    }
}
