package com.example.meetupserver.controller;

import com.example.meetupserver.model.Meetup;
import com.example.meetupserver.model.User;
import com.example.meetupserver.service.EmailService;
import com.example.meetupserver.service.MeetupService;
import com.example.meetupserver.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("api/email")
@CrossOrigin
public class EmailController {
    @Autowired
    private UserDetailsServiceImpl userService;
    private static final Logger LOG = LoggerFactory.getLogger(EmailController.class);

    HashMap<String, User> usersWaitingForVerification = new HashMap<>();

    @Autowired
    EmailService emailService;

    @Autowired
    MeetupService meetupService;

    @GetMapping(value = "/verification")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public @ResponseBody ResponseEntity sendSimpleEmail(HttpServletRequest request) {
        User user = userService.getUserFromJWT(request);
        if (user.isVerified())
            return new ResponseEntity<>("User already verified!", HttpStatus.BAD_REQUEST);

        try {
            String uuid = String.valueOf(UUID.randomUUID());
            String url = "http://localhost:8081/api/email/" + uuid;
            usersWaitingForVerification.put(uuid, user);
            String message = "Для подтверждения вашего аккаунта пройдите по ссылке:\n\n" + url;
            emailService.sendSimpleEmail(user.getEmail(), "Account verification", message);
        } catch (MailException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }

    @GetMapping(value = "/notify/{id}")
    @PreAuthorize("hasRole('CHIEF')")
    public @ResponseBody ResponseEntity notify(@PathVariable long id) {

        Meetup meetup = meetupService.getMeetupById(id);
        Set<User> users = meetup.getParticipants();

        for(User user: users){
            try {
                emailService.sendSimpleEmail(user.getEmail(), "Напоминание о встрече",
                        "Встреча состоится в " + meetup.getStart() + " по адресу " + meetup.getPlace()
                );
            } catch (MailException mailException) {
                LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            }
        }


        return new ResponseEntity<>("All participants notified", HttpStatus.OK);
    }

    @GetMapping(value = "/{uuid}")
    public @ResponseBody ResponseEntity sendSimpleEmail(@PathVariable String uuid) {
        User user = usersWaitingForVerification.get(uuid);
        if(user != null) {
            user.setVerified(true);
            userService.saveOrUpdate(user);
            usersWaitingForVerification.remove(uuid);
            return new ResponseEntity<>("Verification complete!", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("Incorrect URL or Server was restarted", HttpStatus.BAD_REQUEST);
    }
}
