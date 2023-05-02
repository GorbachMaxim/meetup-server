package com.example.meetupserver.controller;


import com.example.meetupserver.dto.MessageResponse;
import com.example.meetupserver.dto.entityDTO.CommentDTO;
import com.example.meetupserver.dto.entityDTO.NewsDTO;
import com.example.meetupserver.model.Comment;
import com.example.meetupserver.model.Meetup;
import com.example.meetupserver.model.News;
import com.example.meetupserver.model.User;
import com.example.meetupserver.service.CommentService;
import com.example.meetupserver.service.NewsService;
import com.example.meetupserver.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {



    @Autowired
    private NewsService newsService;

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private CommentService commentService;


    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public List<NewsDTO> getAllNews() {
        List<News> allNews = newsService.getAllNews();
        List<NewsDTO> allNewsDTO = new ArrayList<>();
        allNews.forEach(peaceOfNews->{
            allNewsDTO.add(new NewsDTO(peaceOfNews));
        });
        return allNewsDTO;
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public NewsDTO getOneNews(@PathVariable long id){
        News peaceOfNews = newsService.getNewsById(id);
        return new NewsDTO(peaceOfNews);
    }






    @PostMapping
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<?> addOneNews(@RequestBody @Valid News news) {
        User user = userService.getUserById(news.getAuthor().getId());
        news.setComments(null);

        news.setAuthor(user);


        newsService.saveOrUpdate(news);
        return ResponseEntity.ok(new MessageResponse("News CREATED"));
    }

    @PutMapping
    @PreAuthorize("hasRole('CHIEF')")//не работает на обзоры
    public ResponseEntity<?> updateNews(@RequestBody @Valid News news){
        News news1 = newsService.getNewsById(news.getId());
        news1.setTitle(news.getTitle());
        news1.setText(news.getText());
        news1.setImage(news.getImage());
        news1.setPublicationDate(news.getImage());

        if(news.getAuthor() == null)
            news1.setAuthor(news.getAuthor());


        User user = userService.getUserById(news.getAuthor().getId());

        if(user != null)
            news1.setAuthor(user);

        newsService.saveOrUpdate(news1);
        return ResponseEntity.ok(new MessageResponse("News UPDATED"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<?> deleteNews(@PathVariable long id){
        newsService.deleteNewsById(id);
        return ResponseEntity.ok(new MessageResponse("News DELETED"));
    }



    @PostMapping("/{id}/comments")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public ResponseEntity<?> addComment(@PathVariable long id, Comment comment, HttpServletRequest request) {
        try {
            commentService.addComment(request, comment, id);
            return ResponseEntity.ok(new MessageResponse("Comment ADDED"));
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

    }



    @GetMapping("/{id}/comments")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public List<CommentDTO> getComments(@PathVariable long id) {
        List<Comment> comments = commentService.getCommentsByNewsId(id);
        List<CommentDTO> commentsDto = new ArrayList<>();
        comments.forEach(comment -> {
            commentsDto.add(new CommentDTO(comment));
        });
        return commentsDto;
    }


    @DeleteMapping("/comments/{idComment}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public ResponseEntity<?> deleteComment(@PathVariable long idComment, HttpServletRequest request) {

        try {
            commentService.deleteCommentById(request, idComment);
            return ResponseEntity.ok(new MessageResponse("Comment DELETED"));
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
