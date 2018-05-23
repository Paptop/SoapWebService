package com.service;

import com.hospital.doctors.Doctor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class DoctorService {

    private static final Map<Long, Doctor> doctors= new HashMap<>();

    @PostConstruct
    public void initialize(){
        Doctor peter = new Doctor();
        peter.setId(1);
        peter.setName("Peter");
        peter.setSurname("Peterson");
        peter.setSpeciality("RandomDoctor");
        doctors.put(peter.getId(),peter);

    }

    public Doctor getDoctor(long id){
        return doctors.get(id);
    }
}
