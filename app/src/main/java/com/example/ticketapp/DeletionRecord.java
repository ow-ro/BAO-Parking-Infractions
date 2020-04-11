package com.example.ticketapp;

public class DeletionRecord {

    private int ticketId;
    private String plate;
    private String state;
    private String dateTime;
    private String infraction;
    private String location;
    private String notes;
    private int type;
    private String deletionTime;

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int id) {
        this.ticketId = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getInfraction() {
        return infraction;
    }

    public void setInfraction(String infraction) {
        this.infraction = infraction;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(String deletionTime) {
        this.deletionTime = deletionTime;
    }
}
