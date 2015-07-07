package androidapps.amogh.privatechatapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.Socket;


public class ChatAppActivity extends ActionBarActivity {
    public static final String MESSAGE_KEY = "1122994848392-";
    Socket socket;
    PrintWriter writer;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_app);
        editText = (EditText) findViewById(R.id.message_edit_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view){
        writer.println(editText.getText().toString());
        writer.flush();
        displayMessage(editText.getText().toString());
        editText.setText("");
        editText.requestFocus();
    }

    public void displayMessage(String message){
        ViewGroup viewGroup = (LinearLayout) findViewById(R.id.layout);

        TextView textView = new TextView(this);
        textView.setText(message);
        viewGroup.addView(textView);
    }
}
