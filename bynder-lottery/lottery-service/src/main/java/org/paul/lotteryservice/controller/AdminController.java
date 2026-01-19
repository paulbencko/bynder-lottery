package org.paul.lotteryservice.controller;

import lombok.RequiredArgsConstructor;
import org.paul.lotteryservice.service.LotteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final LotteryService lotteryService;

    @PostMapping("/draw")
    public ResponseEntity<String> drawWinningNumbers() {
        lotteryService.drawWinningCombinations();
        return ResponseEntity.ok("Draw triggered");
    }
}
