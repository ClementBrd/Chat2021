package com.example.chat2021;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CAT = "LE4-SI";
    RecyclerView conversation;
    TextInputLayout edtContenu;
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
                MessageAdapter adapter = new MessageAdapter(lm.messages);
                conversation.setAdapter(adapter);
                conversation.setLayoutManager(new LinearLayoutManager(ConvActivity.this));
                Log.i(CAT,lm.messages.toString());
            }

            @Override
            public void onFailure(Call<ListMessage> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onClick(View v) {
        String contenu = edtContenu.getEditText().getText().toString();
        if(contenu.length() > 0){
            apiService = APIClient.getClient().create(APIInterface.class);
            Call<Message> call1 = apiService.doSetListMessage(hash, convID, contenu);
            call1.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Message newMessage = new Message(Integer.toString(lm.messages.size() + 1), contenu, "Tom", "rouge");
                    lm.messages.add(newMessage);
                    edtContenu.getEditText().getText().clear();
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