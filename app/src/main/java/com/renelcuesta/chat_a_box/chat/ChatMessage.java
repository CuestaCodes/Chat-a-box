package com.renelcuesta.chat_a_box.chat;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    public String TAG = "Chat-a-boxChatMessage";

    public ChatMessage() {
    }

    public String chatSender;

    public String chatMessage;

    public String chatSendTime;

    public ChatMessage(Object chatMessages) {
    }

    public String getChatSender() {
        return chatSender;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public String getChatSendTime() {
        return chatSendTime;
    }

    public String toString() {
        String result = "Chat Message : Sender [" + chatSender + "] Time [" + chatSendTime +
                "] Message [" + chatMessage + "]";
        return result;
    }

    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }

        if (otherObject == null) {
            return false;
        }

        if (this.getClass() != otherObject.getClass()) {
            return false;
        }

        ChatMessage chat = (ChatMessage) otherObject;

        boolean chatBoolean = (this.chatSender == chat.chatSender &&
                this.chatMessage == chat.chatMessage && this.chatSendTime == chat.chatSendTime);

        return chatBoolean;
    }
}
