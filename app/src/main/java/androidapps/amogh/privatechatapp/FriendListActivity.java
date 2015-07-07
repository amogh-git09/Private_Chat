package androidapps.amogh.privatechatapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends ActionBarActivity {
    String name;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Intent intent = getIntent();
        name = intent.getStringExtra(Intent.EXTRA_USER);
        id = intent.getStringExtra(Intent.EXTRA_TEXT);

        TextView textView = (TextView) findViewById(R.id.profile_name_text_view);
        textView.setText("Hi " + name + "!");

        new FetchClientsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openChatRoom(Friend friend){
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra(Intent.EXTRA_USER, friend.getName());
        intent.putExtra(Intent.EXTRA_TEXT, friend.getId());
        intent.putExtra(ServerData.SELF_ID, id);
        startActivity(intent);
    }

    private void displayFriends(final ArrayList<Friend> friendList){
        ArrayList<String> names = new ArrayList<String>();

        ListView listView = (ListView) findViewById(R.id.friend_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend f = friendList.get(position);
                openChatRoom(f);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

        for(Friend f : friendList){
            names.add(f.getName());
            adapter.notifyDataSetChanged();
        }
    }

    public class FetchClientsTask extends AsyncTask<Void, Void, ArrayList<Friend>>{
        @Override
        protected ArrayList<Friend> doInBackground(Void... params) {
            ArrayList<Friend> friendList = new ArrayList<Friend>();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ServerData.IP);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(ServerData.REQUEST_TYPE, ServerData.FETCH_FRIEND_LIST));
            nameValuePairs.add(new BasicNameValuePair(ServerData.SELF_ID, id));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(httpPost, responseHandler);

                String[] friends = response.split(";");
                Log.d("FRIENDS", friends.length + "");

                for(String f : friends){
                    int i = f.indexOf('=');
                    String id = f.substring(0, i);
                    String name = f.substring(i+1);
                    friendList.add(new Friend(id, name));
                }

                Log.d("FRIENDLIST", response);
                return friendList;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friends) {
            displayFriends(friends);
        }
    }
}
