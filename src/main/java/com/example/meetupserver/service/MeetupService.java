package com.example.meetupserver.service;

import com.example.meetupserver.model.Meetup;
import com.example.meetupserver.model.News;
import com.example.meetupserver.repository.MeetupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetupService {
    private final MeetupRepository meetupRepository;

    public Meetup getMeetupById(long id){
        return meetupRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<Meetup> getAllMeetups(){
        return meetupRepository.findAll();
    }

    public void saveOrUpdate(Meetup meetup){
        meetupRepository.save(meetup);
    }

    public void deleteMeetupById(long id){
        meetupRepository.deleteById(id);
    }
}
