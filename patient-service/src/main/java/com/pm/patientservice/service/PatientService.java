package com.pm.patientservice.service;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExist;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import com.pm.patientservice.kafka.Kafkaproducer;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Service
public class PatientService {
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final PatientRepository patientRepository;
    private final Kafkaproducer kafkaProducer;
    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, Kafkaproducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }
    public List<PatientResponseDTO> getAllPatient() {
        List<Patient> patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            throw new PatientNotFoundException("no patient found in the database");
        }
        List<PatientResponseDTO> patientResponseDTOS = patients.stream().map(PatientMapper::toDTO).toList();
        return patientResponseDTOS;
    }
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        // An email address must be unique
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExist("Patient email already exists     " + patientRequestDTO.getEmail());
        }
        Patient savedPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        //call the billing service
        billingServiceGrpcClient.createBillingAccount(
                savedPatient.getId().toString(),
                savedPatient.getName(), savedPatient.getEmail());
        kafkaProducer.sendEvent(savedPatient);
        return PatientMapper.toDTO(savedPatient);
    }
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("patient not found with    " + patientRequestDTO.getEmail()));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExist("Patient email already exists     " + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDatOfBirth()));
        //  patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisterDate()));
        Patient updatedPatienr = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatienr);
    }
    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("patient not found with    " + id));
        patientRepository.delete(patient);
    }
}