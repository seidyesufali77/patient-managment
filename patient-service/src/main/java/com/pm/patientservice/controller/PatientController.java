package com.pm.patientservice.controller;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "An API for manging patients ")
public class PatientController {
    private  final PatientService patientService;
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    @GetMapping
    @Operation(summary = "Get all patients",description = "fetch all patients using patient Id")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatient(){
        List<PatientResponseDTO> patientResponseDTOS=patientService.getAllPatient();
        return  ResponseEntity.ok(patientResponseDTOS);
    }
    @PostMapping
    @Operation(summary = "Create a patient",description = "create a patient in the system")
    public  ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patientResponseDTO1=patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO1);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update a patient",description = "update a patient in the system")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable UUID id,
            @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO updated = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok(updated);
    }
@DeleteMapping("/id")
@Operation(summary = "Delete a patient",description = "delete a patient in the system")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
       patientService.deletePatient(id);
        return  ResponseEntity.noContent().build();
}

}
