package com.example.mymessage;

public class ChatObject {
    private  String chatId;
    private  String sender;
    private String receiver;
    private  String message;
    private  String type;
    public  ChatObject(String chatId)
    {
        this.chatId=chatId;
    }
    public  ChatObject(String sender,String receiver,String message,String type)
    {
        this.sender=sender;
        this.receiver=receiver;
        this.type=type;
        this.message=message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getChatId() { return chatId; }
}
