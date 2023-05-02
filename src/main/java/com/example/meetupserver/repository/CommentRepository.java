package com.example.meetupserver.repository;

import com.example.meetupserver.model.Comment;
import com.example.meetupserver.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

    List<Comment> findReviewsByNews_id(long id);


}
