package com.endpoint;

import com.hospital.doctors.GetDoctorRequest;
import com.hospital.doctors.GetDoctorResponse;
import com.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class DoctorEndPoint {

    @Autowired
    private DoctorService doctorService;

    @PayloadRoot(namespace = "http://hospital.com/doctors", localPart = "getDoctorRequest")
    @ResponsePayload
    public GetDoctorResponse getUserResponse(@RequestPayload GetDoctorRequest request){
        GetDoctorResponse response = new GetDoctorResponse();
        response.setDoctor(doctorService.getDoctor(request.getId()));
        return response;
    }

}
