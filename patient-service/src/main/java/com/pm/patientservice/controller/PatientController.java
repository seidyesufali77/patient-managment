package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private  final PatientService patientService;
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatient(){
        List<PatientResponseDTO> patientResponseDTOS=patientService.getAllPatient();
        return  ResponseEntity.ok(patientResponseDTOS);

    }
    // check all the validations has been done  and @ RequestBody is used to
    //convert the request json object  to PatientRequestDTO object
    @PostMapping
    public  ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patientResponseDTO1=patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO1);

    }


}
