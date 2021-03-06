package com.example.springbootsoaphospital;

import com.example.springbootsoaphospital.Exceptions.ResourceConflict409;
import com.example.springbootsoaphospital.Exceptions.ResourceNotFoundException404;
import com.example.springbootsoaphospital.Exceptions.ResourceRegisterFirstException;
import com.hospital.doctors.*;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeaderException;
import sun.font.CreatedFontTracker;

import javax.xml.soap.SOAPException;
import java.util.Map;

@Endpoint
public class DoctorEndPoint {

    @Autowired
    private DoctorService doctorService;
    private PostService postService = new PostService();
    private final String URL_NP = "http://hospital.com/doctors";
    private RestTemplate restTemplate = new RestTemplate();


    private final String URL_USERS= "http://userspostscomments_app_1:80/api/users";
    private final String URL_POSTS= "http://userspostscomments_app_1:80/api/posts";
    private final String URL_COMMENTS= "http://userspostscomments_app_1:80/api/comments";
    //private final String DEBUG_URL_USERS= "http://0.0.0.0:80/api/users";
    //private final String DEBUG_URL_POSTS= "http://0.0.0.0:80/api/posts";
    //private final String DEBUG_URL_COMMENTS="http://0.0.0.0:80/api/comments";

    private Doctor createDoctor(long id, String name, String surname, String speciality){
        Doctor doc = new Doctor();
        doc.setId(id);
        doc.setSurname(surname);
        doc.setSpeciality(speciality);
        doc.setName(name);
        return doc;
    }

    @PayloadRoot(namespace = URL_NP, localPart = "getDoctorRequest")
    @ResponsePayload
    public GetDoctorResponse getDoctorResponse(@RequestPayload GetDoctorRequest request){
        GetDoctorResponse response = new GetDoctorResponse();
        Doctor doc = doctorService.getDoctor(request.getId());
        if(doc == null)
            throw new ResourceNotFoundException404("Doctor","id",request.getId());
        else
            response.setDoctor(doctorService.getDoctor(request.getId()));
        return response;
    }

    @PayloadRoot(namespace = URL_NP, localPart = "getAllDoctorRequest")
    @ResponsePayload
    public GetAllDoctorResponse getAllDoctorResponse(@RequestPayload GetAllDoctorRequest request){
       GetAllDoctorResponse response = new GetAllDoctorResponse();
       String speciality = request.getSpeciality();
       String name = request.getName();
       for(Map.Entry<Long,Doctor> entry : doctorService.getAllDoctors().entrySet()){
           // Very bad code but it works !!!
           if(speciality != null && name != null){
                if(speciality.equals(entry.getValue().getSpeciality()) &&
                        name.equals(entry.getValue().getName()))  {
                    response.getDoctors().add(entry.getValue());
                }
            }else if( speciality == null && name != null ){
                if(name.equals(entry.getValue().getName())) {
                    response.getDoctors().add(entry.getValue());
                }
            }else if(speciality != null && name == null){
               if(speciality.equals(entry.getValue().getSpeciality())){
                   response.getDoctors().add(entry.getValue());
               }
            }else{
               response.getDoctors().add(entry.getValue());
           }
       }
       return response;
    }

    @PayloadRoot(namespace = URL_NP, localPart = "deleteDoctorRequest")
    @ResponsePayload
    public DeleteDoctorResponse deleteDoctorResponse(@RequestPayload DeleteDoctorRequest request){
        DeleteDoctorResponse response = new DeleteDoctorResponse();
        Doctor doc = doctorService.getDoctor(request.getId());
        if(doc == null)
            throw new ResourceNotFoundException404("Doctor","id",request.getId());
        else
            doctorService.deleteDoctor(request.getId());
        return response;
    }


    @PayloadRoot(namespace = URL_NP, localPart = "createDoctorRequest")
    @ResponsePayload
    public CreateDoctorResponse createDoctorResponse(@RequestPayload CreateDoctorRequest request){
        CreateDoctorResponse response = new CreateDoctorResponse();
        Doctor newDoctor = new Doctor();
        boolean isPresent = doctorService.isPresent(request.getId());
        if(!isPresent)
            newDoctor.setId(request.getId());
        newDoctor.setName(request.getName());
        newDoctor.setSurname(request.getSurname());
        newDoctor.setSpeciality(request.getSpeciality());
        /*External service part*/
        User user = new User();
        user.setEmail(newDoctor.getName().concat(newDoctor.getSurname()).concat("@VilniusHospital.com"));
        user.setUsername(newDoctor.getName().concat(newDoctor.getSurname()));
        HttpEntity<User> newRequest = new HttpEntity<>(user);
        try {
            ResponseEntity<User> userResponse = restTemplate.exchange(URL_USERS, HttpMethod.POST, newRequest, User.class);
            newDoctor.setUserId(userResponse.getBody().getId());
            newDoctor.setUser(userResponse.getBody());
            doctorService.createDoctor(newDoctor,isPresent);
            return response;
        }catch(RestClientException exc){
            if(exc.getMessage().equals("409 null")){
                throw new ResourceConflict409("User","username",user.getUsername());
            }else{
                doctorService.createDoctor(newDoctor,isPresent);
                throw new SoapHeaderException("Internal Error");
            }
        }
    }

    @PayloadRoot(namespace = URL_NP, localPart = "updateDoctorRequest")
    @ResponsePayload
    public UpdateDoctorResponse updateDoctorResponse(@RequestPayload UpdateDoctorRequest request){
        UpdateDoctorResponse response = new UpdateDoctorResponse();
        Doctor doc = doctorService.getDoctor(request.getId());
        if(doc == null)
            throw new ResourceNotFoundException404("Doctor","id",request.getId());
        else {
            doctorService.updateDoctor(createDoctor(request.getId(),
                    request.getName(),
                    request.getSurname(),
                    request.getSpeciality()));
        }
        return response;
    }

    @PayloadRoot(namespace = URL_NP, localPart = "registerDoctorAsUserRequest")
    @ResponsePayload
    public RegisterDoctorAsUserResponse registerDoctorAsUserResponse(@RequestPayload RegisterDoctorAsUserRequest request){
       RegisterDoctorAsUserResponse response = new RegisterDoctorAsUserResponse();
       Doctor doc = doctorService.getDoctor(request.getId());
       if(doc ==null)
           throw new ResourceNotFoundException404("Doctor","id",request.getId());
       else{
           /*External service part*/
           response.setDoctor(createUserBasedOnDoctor(doc));
           return response;
       }
    }

    @PayloadRoot(namespace = URL_NP, localPart = "createPostForDoctorRequest")
    @ResponsePayload
    public CreatePostForDoctorResponse createPostForDoctorResponse(@RequestPayload CreatePostForDoctorRequest request){
        CreatePostForDoctorResponse response = new CreatePostForDoctorResponse();
        Doctor doc = doctorService.getDoctor(request.getId());
        if(doc == null)
            throw new ResourceNotFoundException404("Doctor", "id",request.getId());
        else{
            if(doc.getUserId() != 0){
               if(request.getTitle() == null) request.setTitle("New Title");
               if(request.getBody() == null) request.setBody("New body");
               Post post = createPost(request.getTitle(), request.getBody(), doc.getUserId());
               Post finalPost = createExternalPost(post);
               finalPost.getId();
               if(finalPost!= null){
                   postService.addPost(finalPost);
                   doc.getPosts().add(finalPost);
                   response.setPost(finalPost);
               }
               return response;
            }else{
               // Throw registerEntityFirst exception;
                throw new ResourceRegisterFirstException("Doctor","id",String.valueOf(doc.getId()),"user");
            }
        }
    }


    private Post createPost(String title, String body, long userId){
        Post post = new Post();
        post.setBody(body);
        post.setTitle(title);
        post.setUserId(userId);
        return post;
    }

    private Comment createComment(String body, long userId, long postId){
       Comment com = new Comment();
       com.setPostId(postId);
       com.setUserId(userId);
       com.setBody(body);
       return com;
    }

    private Doctor createUserBasedOnDoctor(Doctor doc){

        User user = new User();
        user.setEmail(doc.getName().concat(doc.getSurname()).concat("@VilniusHospital.com"));
        user.setUsername(doc.getName().concat(doc.getSurname()));
        HttpEntity<User> newRequest = new HttpEntity<>(user);
        try {
            ResponseEntity<User> userResponse = restTemplate.exchange(URL_USERS, HttpMethod.POST, newRequest, User.class);
            doc.setUserId(userResponse.getBody().getId());
            doc.setUser(userResponse.getBody());
            return doc;
        }catch(RestClientException exc){
            if(exc.getMessage().equals("409 null")){
                throw new ResourceConflict409("User","username",user.getUsername());
            }else{
                throw new SoapHeaderException("Internal Error");
            }
        }
    }


    private Post createExternalPost(Post post){
        HttpEntity<Post> newRequest = new HttpEntity<>(post);
        try {
            ResponseEntity<Post> postResponse = restTemplate.exchange(URL_POSTS, HttpMethod.POST, newRequest, Post.class);
            return postResponse.getBody();
        }catch(RestClientException exc){
            if(exc.getMessage().equals("409 null")){
                //throw new ResourceConflict409("User","username",user.getUsername());
                return null;
            }else{
                throw new SoapHeaderException("Internal Error");
            }
        }
    }

    @PayloadRoot(namespace = URL_NP, localPart = "createCommentForDoctorRequest")
    @ResponsePayload
    public CreateCommentForDoctorResponse createCommentForDoctor(@RequestPayload CreateCommentForDoctorRequest request){
       CreateCommentForDoctorResponse response = new CreateCommentForDoctorResponse();
       Doctor doc = doctorService.getDoctor(request.getId());
        if(doc == null)
            throw new ResourceNotFoundException404("Doctor", "id",request.getId());
        else{
            if(doc.getUserId() != 0) {
                Post p = postService.getPost(request.getPostId());
                if (p == null)
                    throw new ResourceNotFoundException404("Post", "id", request.getPostId());
                else {
                    Comment finalComment = createExternalComment(createComment(
                            request.getBody(),
                            doc.getUserId(),
                            request.getPostId()
                    ));
                    p.getComment().add(finalComment);
                    doc.getComments().add(finalComment);
                    response.setComment(finalComment);
                    return response;
                }
            }else{
                throw new ResourceRegisterFirstException("Doctor","id",String.valueOf(doc.getId()),"user");
            }
        }
    }



    public Comment createExternalComment(Comment comment){
        HttpEntity<Comment> newRequest = new HttpEntity<>(comment);
        try {
            ResponseEntity<Comment> commentResponse = restTemplate.exchange(URL_COMMENTS, HttpMethod.POST, newRequest, Comment.class);
            return commentResponse.getBody();
        }catch(RestClientException exc){
            if(exc.getMessage().equals("409 null")){
                throw new ResourceConflict409("Comment","username",comment.getUserId());
            }else{
                throw new SoapHeaderException("Internal Error");
            }
        }
    }
}
