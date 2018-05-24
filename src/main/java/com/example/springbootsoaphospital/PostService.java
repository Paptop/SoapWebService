package com.example.springbootsoaphospital;

import com.hospital.doctors.Doctor;
import com.hospital.doctors.Post;

import java.util.HashMap;
import java.util.Map;

public class PostService {
    private static final Map<Long, Post> posts = new HashMap<>();

    public Post getPost(long id){
        return posts.get(id);
    }

    public void deletePost(long id){
        posts.remove(id);
    }


    public void addPost(Post post){
        posts.put(post.getId(),post);
    }
}
