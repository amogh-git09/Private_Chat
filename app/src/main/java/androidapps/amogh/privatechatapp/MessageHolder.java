package androidapps.amogh.privatechatapp;

import java.util.ArrayList;

/**
 * Created by amogh_000 on 6/21/2015.
 */
public class MessageHolder {
    ArrayList<Message> messages;

    public MessageHolder(){
        messages = new ArrayList<Message>();
    }

    public ArrayList<String> getMessagesAsString(String friendId){
        ArrayList<String> result = new ArrayList<String>();

        for(Message m : messages){
            if(m.getSenderId().equals(friendId)){
                result.add(m.getMessage());
            }
        }

        return result;
    }

    public void addMessage(String id, String message){
        messages.add(new Message(id, message));
    }
}
