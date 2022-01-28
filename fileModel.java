package com.example.signup;

public class fileModel {
    String Authorname,Bookname,Description,Post,Publishername;

    public fileModel(String authorname, String bookname, String description, String post, String publishername) {
        Authorname = authorname;
        Bookname = bookname;
        Description = description;
        Post = post;
        Publishername = publishername;
    }

    public fileModel() {
    }

    public String getAuthorname() {
        return Authorname;
    }

    public String getBookname() {
        return Bookname;
    }

    public String getDescription() {
        return Description;
    }

    public String getPost() {
        return Post;
    }

    public String getPublishername() {
        return Publishername;
    }

    public void setAuthorname(String authorname) {
        Authorname = authorname;
    }

    public void setBookname(String bookname) {
        Bookname = bookname;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setPost(String post) {
        Post = post;
    }

    public void setPublishername(String publishername) {
        Publishername = publishername;
    }
}
