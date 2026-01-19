package org.paul.participantservice.service;

import lombok.RequiredArgsConstructor;
import org.paul.participantservice.data.entity.Participant;
import org.paul.participantservice.data.repository.ParticipantRepository;
import org.paul.participantservice.exception.EmailAlreadyRegisteredException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public Participant register(String name, String email, String password) {
        if (participantRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }
        var participant = new Participant();
        participant.setName(name);
        participant.setEmail(email);
        participant.setPassword(hashPassword(password));
        participant.setDateRegistered(LocalDateTime.now());
        return participantRepository.save(participant);
    }

    public Optional<Participant> login(String email, String password) {
        return participantRepository.findByEmail(email)
                .filter(participant -> participant.getPassword().equals(hashPassword(password)));
    }

    private String hashPassword(String password) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}
