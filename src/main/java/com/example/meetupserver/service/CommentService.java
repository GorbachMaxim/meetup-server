package com.example.meetupserver.service;

import com.example.meetupserver.model.Comment;
import com.example.meetupserver.model.ERole;
import com.example.meetupserver.model.News;
import com.example.meetupserver.model.User;
import com.example.meetupserver.repository.CommentRepository;
import com.example.meetupserver.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Service
public class CommentService {
    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserDetailsServiceImpl userService;


    public void addComment(HttpServletRequest request, Comment comment, long id) throws IllegalAccessException {
        News news = newsRepository.findById(id).orElseThrow(NullPointerException::new);
        User user = userService.getUserFromJWT(request);
        comment.setNews(news);
        comment.setUser(user);
        if(user.isVerified())
            commentRepository.save(comment);
        else
            throw new IllegalAccessException("User not verified!");
    }

    public List<Comment> getCommentsByNewsId(long id){
        return commentRepository.findReviewsByNews_id(id);
    }


    public void deleteCommentById(HttpServletRequest request, long id) throws IllegalAccessException {
        User user1 = userService.getUserFromJWT(request);

        Comment comment = commentRepository.findById(id).orElseThrow(NullPointerException::new);
        User user2 = comment.getUser();

        if(Objects.equals(user1.getId(), user2.getId())){
            commentRepository.deleteById(id);
            return;
        }


        if(user1.getRoles().contains(ERole.ROLE_CHIEF)) {
            commentRepository.deleteById(id);
            return;
        }
        throw new IllegalAccessException("Only Author or Chief can delete this comment");

    }
}
