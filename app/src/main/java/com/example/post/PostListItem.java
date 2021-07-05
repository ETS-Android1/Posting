package com.example.post;

public class PostListItem {
    private String writer;
    private String title;
    private String contents;
    private String date;
    private int imgResource;

    //Writer
    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    //Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //Contetns
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    //Date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //Image
    public int getImgResource() {
        return imgResource;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }


}
