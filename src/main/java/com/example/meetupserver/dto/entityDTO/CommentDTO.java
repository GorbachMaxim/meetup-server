package com.example.meetupserver.dto.entityDTO;

import com.example.meetupserver.model.Comment;
import com.example.meetupserver.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private long id;

    private String text;

    private User user;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.user = comment.getUser();
        user.setPassword(null);
    }

    public CommentDTO() {
    }
}
