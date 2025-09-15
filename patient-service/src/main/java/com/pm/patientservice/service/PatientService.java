package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExist;
import com.pm.patientservice.exception.ResourceNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    public List<PatientResponseDTO>  getAllPatient(){
        List<Patient> patients= patientRepository.findAll();
        if(patients.isEmpty()){
            throw new ResourceNotFoundException("no patient found in the database");
        }
      List<PatientResponseDTO> patientResponseDTOS=patients.stream().map(PatientMapper::toDTO).toList();
      return patientResponseDTOS;
    }
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        // An email address must be unique
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExist("Patient email already exists"+patientRequestDTO.getEmail());
        }
        Patient savedPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
       // convert back to response dto
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, @Valid PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("patient not found"));
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDatOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisterDate()));
        Patient updatedPatienr =patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatienr);
    }
}
