package com.example.meetupserver.service;


import com.example.meetupserver.model.News;
import com.example.meetupserver.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public News getNewsById(long id){
        return newsRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<News> getAllNews(){
        return newsRepository.findAll();
    }

    public void saveOrUpdate(News news){
        newsRepository.save(news);
    }

    public void deleteNewsById(long id){
        newsRepository.deleteById(id);
    }
}
