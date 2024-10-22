package com.javanostra.meetyourmatch;

public class Message {
    private String message;
    private int idSender;
    private int idRecipient;
    private String dateOfSending;

    public Message(String message, int idSender, int idRecipient, String dateOfSending) {
        this.message = message;
        this.idSender = idSender;
        this.idRecipient = idRecipient;
        this.dateOfSending = dateOfSending;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIdSender() {
        return idSender;
    }

    public void setIdSender(int idSender) {
        this.idSender = idSender;
    }

    public String getDateOfSending() {
        return dateOfSending;
    }

    public void setDateOfSending(String dateOfSending) {
        this.dateOfSending = dateOfSending;
    }

    public int getIdRecipient() {
        return idRecipient;
    }

    public void setIdRecipient(int idRecipient) {
        this.idRecipient = idRecipient;
    }
}
