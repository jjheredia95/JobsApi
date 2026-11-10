package com.jobshub.controller;

import com.jobshub.dto.home.HomeStatDto;
import com.jobshub.service.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/stats")
    public ResponseEntity<HomeStatDto> showHomeStatistics() {
        return ResponseEntity.ok(homeService.showHomeStats());
    }















}
