package com.jobshub.controller;

import com.jobshub.dto.location.LocationCreationDto;
import com.jobshub.dto.location.LocationDto;
import com.jobshub.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationCreationDto location) {
        return ResponseEntity.status(201).body(locationService.createLocation(location));
    }




}
