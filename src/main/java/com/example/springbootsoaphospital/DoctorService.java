package com.example.springbootsoaphospital;

import com.example.springbootsoaphospital.Exceptions.ResourceNotFoundException404;
import com.hospital.doctors.Doctor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class DoctorService {

    private static final Map<Long, Doctor> doctors= new HashMap<>();
    private long counter = 1;

    @PostConstruct
    public void initialize(){
        Doctor peter = new Doctor();
        peter.setId(counter++);
        peter.setName("Jason");
        peter.setSurname("Peterson");
        peter.setSpeciality("Hermatologist");
        doctors.put(peter.getId(),peter);

        Doctor walter = new Doctor();
        walter.setId(counter++);
        walter.setName("Walter");
        walter.setSurname("White");
        walter.setSpeciality("Hygienist");
        doctors.put(walter.getId(),walter);

        Doctor nick = new Doctor();
        nick.setId(counter++);
        nick.setName("Nick");
        nick.setSurname("Johnson");
        nick.setSpeciality("Gastroenterologist");
        doctors.put(nick.getId(),nick);

        Doctor page= new Doctor();
        page.setId(counter++);
        page.setName("Page");
        page.setSurname("Nickel");
        page.setSpeciality("Neurologist");
        doctors.put(page.getId(),page);

        Doctor dwayne = new Doctor();
        dwayne.setId(counter++);
        dwayne.setName("Dwayne");
        dwayne.setSurname("Johnson");
        dwayne.setSpeciality("Psychiatrist");
        doctors.put(dwayne.getId(),dwayne);
    }

    public Doctor getDoctor(long id) {
        Doctor doc = doctors.get(id);
        return doc;
    }

    public void createDoctor(Doctor doctor, boolean isPresent){
       if(isPresent) {
           do {
               doctor.setId(counter);
           }while(doctors.containsKey(counter++));
       }
       doctors.put(doctor.getId(),doctor);
    }

    public void updateDoctor(Doctor updateFields){
        Doctor doc = doctors.get(updateFields.getId());
        updateFields.getName();
        updateFields.getSpeciality();
        updateFields.getSurname();
        if(updateFields.getName() != null)
            doc.setName(updateFields.getName());
        if(updateFields.getSurname() != null) doc.setSurname(updateFields.getSurname());
        if(updateFields.getSpeciality() != null) doc.setSpeciality(updateFields.getSpeciality());

    }

    public Map<Long,Doctor> getAllDoctors(){
        return this.doctors;
    }

    public void deleteDoctor(long id){
        doctors.remove(id);
    }

    public boolean isPresent(long id ){
        return doctors.containsKey(id);
    }
}
