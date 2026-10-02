package com.jobshub.controller;

import com.jobshub.dto.vacancy.*;
import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.service.VacancyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<VacancyAdminListDto>> getAdminVacancies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false)VacancyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VacancyAdminListDto> result = vacancyService.getAllVacanciesAdminList(name, categoryId, companyId, status, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/full") // (NO USAGE): TEST EVERYTHING FLOWS
    public ResponseEntity<List<VacancyFullDto>> getFullVacancies() {
        return ResponseEntity.ok(vacancyService.getFullVacancyDetails());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VacancyFullDto> createNewVacancy(@Valid @RequestBody VacancyCreationDto vacancy) {
        return ResponseEntity.status(201).body(vacancyService.createNewVacancy(vacancy));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VacancyFullDto> updateVacancy(@PathVariable Integer id, @Valid @RequestBody VacancyUpdateDto vacancy) {
        return ResponseEntity.ok(vacancyService.updateVacancy(id, vacancy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vacancy> getVacancyById(@PathVariable Integer id) {
        return ResponseEntity.ok(vacancyService.getVacancyById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable Integer id) {
        vacancyService.deleteVacancy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<VacancyDetailsDto> showVacancyDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(vacancyService.showVacancyDetails(id));
    }



}
