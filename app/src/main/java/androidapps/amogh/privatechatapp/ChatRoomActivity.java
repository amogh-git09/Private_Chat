package androidapps.amogh.privatechatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ChatRoomActivity extends ActionBarActivity {
    String friendName;
    String friendId;
    String selfId;
    MessageHolder messageHolder = new MessageHolder();
    ArrayList<String> messages = new ArrayList<String>();
    ListView listView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        friendId = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        friendName = getIntent().getStringExtra(Intent.EXTRA_USER);
        selfId = getIntent().getStringExtra(ServerData.SELF_ID);

        listView = (ListView) findViewById(R.id.chat_messages_list_view);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, messages);

        listView.setAdapter(adapter);

        new Thread(new Polling()).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        getSupportActionBar().setTitle(friendName);
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

    public void sendMessage(View view){
        EditText editText = (EditText) findViewById(R.id.message_edit_text);
        String message = editText.getText().toString();
        editText.setText("");
        messages.add("Me: " + message);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
        new SendMessageTask().execute(message);
    }

    public void newMessagesReceived(String response){
        String[] splitMsg = response.split(ServerData.MESSAGE_SEPARATOR);

        for(String msg : splitMsg) {
            int index = msg.indexOf(ServerData.MESSAGE_ID_SEPARATOR);
            if (index == -1)
                break;
            String id = msg.substring(0, index);
            String s = msg.substring(index + 1);
            messageHolder.addMessage(id, s);
            if (id.equals(friendId)) {
                messages.add(friendName + ": " + s);
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount() - 1);
            }
        }
    }

    private class Polling implements Runnable{

        @Override
        public void run() {
            while(true){
                new PollingTask().execute();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String msg = params[0];
            Log.d("CHAT_ROOM_ACTIVITY", msg);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ServerData.IP);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(ServerData.REQUEST_TYPE, ServerData.SEND_MESSAGE));
            nameValuePairs.add(new BasicNameValuePair(ServerData.SELF_ID, selfId));
            nameValuePairs.add(new BasicNameValuePair(ServerData.TARGET_ID, friendId));
            nameValuePairs.add(new BasicNameValuePair(ServerData.MESSAGE, msg));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(httpPost, responseHandler);

                return response;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class PollingTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ServerData.IP);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(ServerData.REQUEST_TYPE, ServerData.FETCH_MESSAGES_REQUEST));
            nameValuePairs.add(new BasicNameValuePair(ServerData.SELF_ID, selfId));

            try {
                Log.d("SEND REQUEST", "Sending request");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(httpPost, responseHandler);
                Log.d("RECEIVED MESSAGE", response);
                return response;
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
        protected void onPostExecute(String strings) {
            newMessagesReceived(strings);
        }
    }
}
