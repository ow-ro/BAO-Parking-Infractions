package com.example.ticketapp;

import android.content.Intent;

public class Ticket {

    private int ticketId;
    private String plate;
    private String state;
    private String dateTime;
    private String infraction;
    private String location;
    private String notes;
    private byte[] licencePhoto;
    private byte[] ticketPhoto;
    private int type;
    private int isTowed;
    private int count;

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

    public byte[] getLicencePhoto() {
        return licencePhoto;
    }

    public void setLicencePhoto(byte[] licencePhoto) {
        this.licencePhoto = licencePhoto;
    }

    public byte[] getTicketPhoto() {
        return ticketPhoto;
    }

    public void setTicketPhoto(byte[] ticketPhoto) {
        this.ticketPhoto = ticketPhoto;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsTowed() {
        return isTowed;
    }

    public void setIsTowed(int isTowed) {
        this.isTowed = isTowed;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
