package org.paul.participantservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paul.participantservice.data.entity.Participant;
import org.paul.participantservice.data.repository.ParticipantRepository;
import org.paul.participantservice.exception.EmailAlreadyRegisteredException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    @Test
    void registerSavesParticipantWithHashedPassword() {
        var name = "Test User";
        var email = "test@example.com";
        var password = "password";
        when(participantRepository.existsByEmail(email)).thenReturn(false);
        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> {
            var participant = invocation.getArgument(0, Participant.class);
            participant.setId(1L);
            return participant;
        });

        var result = participantService.register(name, email, password);

        assertEquals(1L, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertNotEquals(password, result.getPassword());
        assertNotNull(result.getDateRegistered());
    }

    @Test
    void registerThrowsWhenEmailAlreadyRegistered() {
        var email = "test@example.com";
        when(participantRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> participantService.register("Test", email, "password"));
    }

    @Test
    void loginReturnsParticipantOnCorrectPassword() {
        var email = "test@example.com";
        var password = "password";
        var participant = new Participant();
        participant.setEmail(email);
        participant.setPassword(hash(password));
        when(participantRepository.findByEmail(email)).thenReturn(Optional.of(participant));

        var result = participantService.login(email, password);

        assertTrue(result.isPresent());
    }

    @Test
    void loginReturnsEmptyOnWrongPassword() {
        var email = "test@example.com";
        var participant = new Participant();
        participant.setEmail(email);
        participant.setPassword(hash("other"));
        when(participantRepository.findByEmail(email)).thenReturn(Optional.of(participant));

        var result = participantService.login(email, "password");

        assertTrue(result.isEmpty());
    }

    @Test
    void loginReturnsEmptyWhenEmailMissing() {
        when(participantRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        var result = participantService.login("missing@example.com", "password");

        assertTrue(result.isEmpty());
    }

    private String hash(String password) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
