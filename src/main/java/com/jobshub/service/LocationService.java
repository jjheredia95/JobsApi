package com.jobshub.service;

import com.jobshub.dto.location.LocationCreationDto;
import com.jobshub.dto.location.LocationDto;
import com.jobshub.error.BadRequestException;
import com.jobshub.model.Location;
import com.jobshub.repository.LocationRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepo locationRepo;

    public LocationService(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    public List<LocationDto> getAllLocations() {
        List<Location> locations = locationRepo.findAll();
        return locations.stream().map(
                location -> new LocationDto(
                        location.getId(), location.getCity(), location.getState())).toList();
    }
}











