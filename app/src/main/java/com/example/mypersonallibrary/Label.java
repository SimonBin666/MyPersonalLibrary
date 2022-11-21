package com.example.mypersonallibrary;
import java.util.UUID;
public class Label {
    private UUID id;
    private String title;
    public Label(){
        id = UUID.randomUUID();
    }
    public Label(UUID uuid){
        id = uuid;
    }
    @Override
    public String toString(){
        return title;
    }
    public UUID getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
}
