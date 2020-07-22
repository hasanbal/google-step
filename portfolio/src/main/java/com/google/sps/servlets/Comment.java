package com.google.sps.servlets;

public class Comment {
    public String username;
    public String comment;
    public long timestamp;

    public Comment(String username2 , String comment2 , long timestamp2){
        username = username2;
        comment = comment2;
        timestamp = timestamp2;
    }
}
