package com.example.mymessage.chat;

public class MessageObject {
    String messageId,message,senderId;

    public MessageObject(String mesageId,String senderId,String message)
    {
        this.messageId=messageId;
        this.message=message;
        this.senderId=senderId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

}

