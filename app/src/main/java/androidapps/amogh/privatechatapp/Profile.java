package androidapps.amogh.privatechatapp;

import java.util.ArrayList;

/**
 * Created by amogh_000 on 6/21/2015.
 */
public class Profile extends Friend{
    private ArrayList<Friend> friendList;

    public Profile(String id, String name){
        super(id, name);
        this.friendList = new ArrayList<Friend>();
    }

    public void addFriend(Friend friend){
        friendList.add(friend);
    }

    public void setFriendList(ArrayList<Friend> friendList){
        this.friendList.clear();
        this.friendList.addAll(friendList);
    }
}
