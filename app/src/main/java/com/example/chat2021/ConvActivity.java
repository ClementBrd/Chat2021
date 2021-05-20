package com.example.chat2021;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CAT = "LE4-SI";
    ScrollView conversation;
    LinearLayout conversationLayout;
    EditText edtContenu;
    Button btnOK;

    APIInterface apiService;
    String hash;
    ListMessage lm;
    int convID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversation);
        conversation = findViewById(R.id.conversation_svMessages);
        conversationLayout = findViewById(R.id.conversation_svLayoutMessages);
        edtContenu = findViewById(R.id.conversation_edtMessage);
        btnOK = findViewById(R.id.conversation_btnOK);

        btnOK.setOnClickListener(this);

        Bundle bdl = this.getIntent().getExtras();
        hash = bdl.getString("hash");
        convID = Integer.parseInt(bdl.getString("conv"));

        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ListMessage> call1 = apiService.doGetListMessage(hash, convID);
        call1.enqueue(new Callback<ListMessage>() {
            @Override
            public void onResponse(Call<ListMessage> call, Response<ListMessage> response) {
                lm = response.body();
                for(Message m : lm.messages) {
                    TextView message = new TextView(ConvActivity.this);
                    message.setText(m.contenu);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    message.setLayoutParams(params);
                    conversationLayout.addView(message);
                }

                Log.i(CAT,lm.toString());
            }

            @Override
            public void onFailure(Call<ListMessage> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onClick(View v) {
        String contenu = edtContenu.getText().toString();
        if(contenu.length() > 0){
            apiService = APIClient.getClient().create(APIInterface.class);
            Call<Message> call1 = apiService.doSetListMessage(hash, convID, contenu);
            call1.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    TextView message = new TextView(ConvActivity.this);
                    message.setText(contenu);
                    conversationLayout.addView(message);
                    Log.i(CAT,response.body().toString());
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    call.cancel();
                }
            });
        }

    }
}