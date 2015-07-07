package androidapps.amogh.privatechatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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


public class RegisterActivity extends Activity {
    public Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    public void register(View view){
        new RegisterTask().execute();
    }

    public class RegisterTask extends AsyncTask<Void, Void, Profile> {
        @Override
        protected Profile doInBackground(Void... params) {
            Profile profile;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ServerData.IP);

            try {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();

                EditText nameEditText = (EditText) findViewById(R.id.name_edit_text);
                String name = nameEditText.getText().toString();

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair(ServerData.REQUEST_TYPE, ServerData.REGISTER));
                nameValuePairs.add(new BasicNameValuePair(ServerData.NAME, name));
                nameValuePairs.add(new BasicNameValuePair(ServerData.MAC_ADDRESS, macAddress));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(httpPost, responseHandler);
                int i = response.indexOf('=');
                String id = response.substring(0, i);
                name = response.substring(i+1);
                profile = new Profile(id, name);
                Log.d("RESPONSE", response);

                return profile;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute (Profile result){
            startFriendListActivity(result);
        }
    }

    public void startFriendListActivity(Profile profile){
        this.profile = profile;
        Intent intent = new Intent(this, FriendListActivity.class);
        intent.putExtra(Intent.EXTRA_USER, profile.getName());
        intent.putExtra(Intent.EXTRA_TEXT, profile.getId());
        startActivity(intent);
    }
}
