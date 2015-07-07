package androidapps.amogh.privatechatapp;

import java.util.ArrayList;

/**
 * Created by amogh_000 on 6/21/2015.
 */
public class Friend {
    protected String name;
    protected String id;

    public Friend(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }
}
