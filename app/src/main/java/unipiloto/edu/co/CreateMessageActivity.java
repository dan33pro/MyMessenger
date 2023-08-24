package unipiloto.edu.co;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import java.io.Serializable;

import java.util.ArrayList;

public class CreateMessageActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String CONVERSATION = "myConversation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);
        setContentView(R.layout.activity_receive_message);

        Intent intentReceived = getIntent();
        String messageSent = intentReceived.getStringExtra(EXTRA_MESSAGE);
        LinearLayout container = findViewById(R.id.containerMessages);
        ScrollView scrollView = findViewById(R.id.scrollViewMessages);

        if (messageSent != null && !messageSent.equals("")) {
            MessageElement messageElement = new MessageElement("1", messageSent, "SecondActivity", "MainActivity", this);
            saveConversation(this, messageElement);

            HistoryConversation myConversation = loadConversation(this, "conversation");
            for (MessageElement msg: myConversation.myConversation) {
                container.addView(msg.messageTextView);
            }
        } else {
            HistoryConversation newConversation = new HistoryConversation();
            SharedPreferences sharedPreferences = this.getSharedPreferences(CONVERSATION, this.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            String conversationJson = gson.toJson(newConversation);
            editor.putString("conversation", conversationJson);
            editor.apply();
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void onSendMessage(View view) {
        EditText messageWriteView = (EditText) findViewById(R.id.messageWrite);
        String messageText = messageWriteView.getText().toString();
        Intent intent = new Intent(this, ReceiveMessageActivity.class);
        intent.putExtra(ReceiveMessageActivity.EXTRA_MESSAGE, messageText);
        startActivity(intent);
    }

    public static void saveConversation(Context context, MessageElement messageElement) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONVERSATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        HistoryConversation myConversation = loadConversation(context, "conversation");
        myConversation.addMessage(messageElement);

        String conversationJson = gson.toJson(myConversation);
        editor.putString("conversation", conversationJson);
        editor.apply();
    }
    public static HistoryConversation loadConversation(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(CONVERSATION, Context.MODE_PRIVATE);

        String jsonConversation = preferences.getString(key, "");
        Gson gson = new Gson();
        return gson.fromJson(jsonConversation, HistoryConversation.class);
    }

    public class MessageElement implements Serializable {
        public String id_element;
        public String transmitter;
        public TextView messageTextView;

        MessageElement(String idElement, String messageReceived, String transmitter, String primaryActivity, Context context) {
            this.id_element = idElement;
            this.transmitter = transmitter;

            this.messageTextView = new TextView(context);
            this.messageTextView.setBackground(getResources().getDrawable(R.drawable.textview_rounded_background));
            this.messageTextView.setTextColor(getResources().getColor(R.color.light_gray));
            this.messageTextView.setPadding(20, 10, 20, 10);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            if (this.transmitter.equals(primaryActivity)) {
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            } else {
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            }
            this.messageTextView.setLayoutParams(layoutParams);
            this.messageTextView.setText(messageReceived);
        }
    }

    public class HistoryConversation implements Serializable {
        public int size;
        public ArrayList<MessageElement> myConversation;

        public HistoryConversation() {
            this.myConversation = new ArrayList<>();
            this.size = myConversation.size();
        }

        public void addMessage(MessageElement msg) {
            this.myConversation.add(msg);
            this.size = this.myConversation.size();
        }
    }
}