package com.example.chat2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import com.google.android.material.textfield.TextInputLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_show_conversation)
public class ConvActivity extends AppCompatActivity {

    private static final String CAT = "LE4-SI";
    APIInterface apiService;
    String hash;
    ListMessage lm;
    int convID;

    @ViewById(R.id.conversation_svMessages)
    RecyclerView conversation_svMessages;
    
    @ViewById(R.id.conversation_edtMessage)
    TextInputLayout conversation_edtMessage;

    @AfterViews
    void initialize() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bdl = this.getIntent().getExtras();
        hash = bdl.getString("hash");
        convID = Integer.parseInt(bdl.getString("conv"));

        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ListMessage> call1 = apiService.doGetListMessage(hash, convID);
        call1.enqueue(new Callback<ListMessage>() {
            @Override
            public void onResponse(@NonNull Call<ListMessage> call, @NonNull Response<ListMessage> response) {
                lm = response.body();
                MessageAdapter adapter = new MessageAdapter(lm.messages);
                conversation_svMessages.setAdapter(adapter);
                conversation_svMessages.setLayoutManager(new LinearLayoutManager(ConvActivity.this));
                Log.i(CAT,lm.messages.toString());
            }

            @Override
            public void onFailure(@NonNull Call<ListMessage> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    @Click
    void conversation_btnOK() {
        if(conversation_edtMessage.getEditText() != null) {
            String contenu = conversation_edtMessage.getEditText().getText().toString();
            if(contenu.length() > 0) {
                apiService = APIClient.getClient().create(APIInterface.class);
                Call<Message> call1 = apiService.doSetListMessage(hash, convID, contenu);
                doInBackground(call1, contenu);
            }
        }
    }

    @Background
    void doInBackground(Call<Message> call1, String contenu) {
        call1.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                Message newMessage = new Message(Integer.toString(lm.messages.size() + 1), contenu, "tom", "rouge");
                onPostExecute(newMessage);
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    @UiThread
    void onPostExecute(Message newMessage) {
        lm.messages.add(newMessage);
        if(conversation_edtMessage.getEditText() != null)
            conversation_edtMessage.getEditText().getText().clear();
        Log.i(CAT,newMessage.toString());
    }
}