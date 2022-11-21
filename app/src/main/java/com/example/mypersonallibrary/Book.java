package com.example.mypersonallibrary;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import android.util.Log;
import java.util.ArrayList;
public class Book implements Serializable{
    private String title;
    private UUID id;
    private List<String> authors;
    private List<String> translators;
    private Map<String,String> WebIds;
    private String publisher;
    private Calendar pubtime;
    private Calendar addTime;
    private String isbn;
    private boolean hasCover;
    private UUID bookshelfID;
    private int readingStatus;
    private String notes;
    private String website;
    private List<UUID> labelID;
    public Book(){
        id = UUID.randomUUID();
        bookshelfID = UUID.fromString("407c4479-5a57-4371-8b94-ad038f1276fe");
        readingStatus = 0;
    }
    public Book(UUID uuid){
        id = uuid;
    }
    public String getCoverPhotoFileName(){
        return "Cover_"+id.toString()+".jpg";
    }
    public Calendar getAddTime() {
        return addTime;
    }
    public void setAddTime(Calendar addTime) {
        this.addTime = addTime;
    }
    public Map<String, String> getWebIds() {
        return WebIds;
    }
    public void setWebIds(Map<String, String> webIds) {
        WebIds = webIds;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public Calendar getPubtime() {
        return pubtime;
    }
    public void setPubtime(Calendar pubtime) {
        this.pubtime = pubtime;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<String> getAuthors() {
        return authors;
    }
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
    public List<String> getTranslators() {
        return translators;
    }
    public void setTranslators(List<String> translators) {
        this.translators = translators;
    }
    public boolean isHasCover() {
        return hasCover;
    }
    public void setHasCover(boolean hasCover) {
        this.hasCover = hasCover;
    }
    public UUID getBookshelfID() {
        return bookshelfID;
    }
    public void setBookshelfID(UUID bookshelfID) {
        this.bookshelfID = bookshelfID;
    }
    public int getReadingStatus() {
        return readingStatus;
    }
    public void setReadingStatus(int readingStatus) {
        this.readingStatus = readingStatus;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public List<UUID> getLabelID() {
        return labelID;
    }
    public void addLabel(Label label){
        if(labelID == null){
            labelID = new ArrayList<>();
        }
        labelID.add(label.getId());
    }
    public void addLabel(UUID labelid){
        if(labelID == null){
            labelID = new ArrayList<>();
        }
        labelID.add(labelid);
    }
    public void removeLabel(UUID labelid){
        if(labelID!=null){
            labelID.remove(labelid);
        }
    }
}