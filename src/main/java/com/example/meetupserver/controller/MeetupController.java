package com.example.meetupserver.controller;

import com.example.meetupserver.dto.MessageResponse;
import com.example.meetupserver.dto.entityDTO.MeetupDTO;
import com.example.meetupserver.model.ERole;
import com.example.meetupserver.model.Meetup;
import com.example.meetupserver.model.User;
import com.example.meetupserver.service.MeetupService;
import com.example.meetupserver.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/meetups")
@CrossOrigin
public class MeetupController {


    @Autowired
    private MeetupService meetupService;

    @Autowired
    private UserDetailsServiceImpl userService;


    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public List<MeetupDTO> getAllMeetups() {
        List<Meetup> meetups = meetupService.getAllMeetups();
        List<MeetupDTO> meetupDTOS = new ArrayList<>();
        meetups.forEach(meetup->{
            meetupDTOS.add(new MeetupDTO(meetup));
        });
        return meetupDTOS;
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public MeetupDTO getOneMeetup(@PathVariable long id){
        Meetup meetup = meetupService.getMeetupById(id);
        return new MeetupDTO(meetup);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public ResponseEntity<?> addOneMeetup(@RequestBody @Valid Meetup meetup) {
        User user = userService.getUserById(meetup.getAuthor().getId());
        meetup.setAuthor(user);
        meetupService.saveOrUpdate(meetup);
        return ResponseEntity.ok(new MeetupDTO(meetup));
    }

    @PutMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public ResponseEntity<?> updateNews(@RequestBody @Valid Meetup meetup, HttpServletRequest request){

        User user1 = userService.getUserFromJWT(request);
        Meetup meetupForUpdating = meetupService.getMeetupById(meetup.getId());
        if(user1.getRoles().contains(ERole.ROLE_CHIEF) || Objects.equals(user1.getId(), meetupForUpdating.getAuthor().getId())) {

            meetupForUpdating.setFinish(meetup.getFinish());
            meetupForUpdating.setStart(meetup.getStart());
            meetupForUpdating.setExcerpt(meetup.getExcerpt());
            meetupForUpdating.setImage(meetup.getImage());

            meetupForUpdating.setModified(meetup.getModified());
            meetupForUpdating.setPlace(meetup.getPlace());
            meetupForUpdating.setSubject(meetup.getSubject());
            meetupForUpdating.setStatus(meetup.getStatus());

            if (meetup.getAuthor() == null)
                meetupForUpdating.setAuthor(null);


            User user = userService.getUserById(meetup.getAuthor().getId());

            if (user != null)
                meetupForUpdating.setAuthor(user);

            meetupService.saveOrUpdate(meetupForUpdating);
            return ResponseEntity.ok(new MeetupDTO(meetupForUpdating));
        }
        else {
            return ResponseEntity.badRequest().body(new MessageResponse("Only Author or Chief can change this meetup"));
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public ResponseEntity<?> deleteMeetup(@PathVariable long id, HttpServletRequest request){

        User user1 = userService.getUserFromJWT(request);
        Meetup meetupForDeleting = meetupService.getMeetupById(id);

        if(user1.getRoles().contains(ERole.ROLE_CHIEF) || (user1.getId() == meetupForDeleting.getAuthor().getId())) {
            meetupService.deleteMeetupById(id);

            return ResponseEntity.ok(new MeetupDTO(meetupForDeleting));
        }
        else {
            return ResponseEntity.badRequest().body(new MessageResponse("Only Author or Chief can delete this meetup"));
        }
    }



    @PostMapping("/{id}/votedusers")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public Set<User> addVotedUser(@PathVariable long id, HttpServletRequest request) {
        User user = userService.getUserFromJWT(request);
        Meetup meetup = meetupService.getMeetupById(id);
        meetup.getVotedUsers().add(user);
        meetupService.saveOrUpdate(meetup);
        Set<User> users = meetup.getVotedUsers();
        users.forEach(user1 -> {
            user1.setPassword(null);
        });
        return users;
    }

    @PostMapping("/{id}/participants")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public Set<User> addParticipant(@PathVariable long id, HttpServletRequest request) {
        User user = userService.getUserFromJWT(request);
        Meetup meetup = meetupService.getMeetupById(id);
        meetup.getParticipants().add(user);
        meetupService.saveOrUpdate(meetup);
        Set<User> users = meetup.getParticipants();
        users.forEach(user1 -> {
            user1.setPassword(null);
        });
        return users;
    }

    @GetMapping("/{id}/votedusers")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public Set<User> getVotedUsers(@PathVariable long id) {
        Meetup meetup = meetupService.getMeetupById(id);
        Set<User> users = meetup.getVotedUsers();

        users.forEach(user -> {user.setPassword(null);});
        return users;
    }

    @GetMapping("/{id}/participants")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public Set<User> getParticipants(@PathVariable long id) {
        Meetup meetup = meetupService.getMeetupById(id);
        Set<User> users = meetup.getParticipants();

        users.forEach(user -> {user.setPassword(null);});
        return users;
    }



    @DeleteMapping("/{id}/participants")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public ResponseEntity<?> deleteParticipant(@PathVariable long id, HttpServletRequest request) {
        User user = userService.getUserFromJWT(request);
        Meetup meetup = meetupService.getMeetupById(id);
        meetup.getParticipants().remove(user);
        meetupService.saveOrUpdate(meetup);
        return ResponseEntity.ok(new MessageResponse("Participant DELETED"));
    }

    @DeleteMapping("/{id}/votedusers")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public ResponseEntity<?> deleteVotedUser(@PathVariable long id, HttpServletRequest request) {
        User user = userService.getUserFromJWT(request);
        Meetup meetup = meetupService.getMeetupById(id);
        meetup.getVotedUsers().remove(user);
        meetupService.saveOrUpdate(meetup);
        return ResponseEntity.ok(new MessageResponse("VotedUser DELETED"));
    }
}
