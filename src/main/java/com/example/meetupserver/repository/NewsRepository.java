package com.example.meetupserver.repository;

import com.example.meetupserver.model.News;
import com.example.meetupserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>{

}
