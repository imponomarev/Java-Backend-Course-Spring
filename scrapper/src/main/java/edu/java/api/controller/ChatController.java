package edu.java.api.controller;

import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable("id") Long id) {
        chatService.registerChat(id);
        log.info("chat is registered");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long id) {
        chatService.deleteChat(id);
        log.info("chat was successfully deleted");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
