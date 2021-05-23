package com.example.chat2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoixConvActivityMD extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout textInputLayout;
    private AutoCompleteTextView dropdownText;

    private static final String CAT = "LE4-SI";
    APIInterface apiService;
    String hash;
    Spinner listeConv;
    ListConversation lc;
    Button btnOK;
    int idItemSelected = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conv_m_d);

        textInputLayout = findViewById(R.id.choixConvMD);
        dropdownText = findViewById(R.id.dropdown_text);

        Bundle bdl = this.getIntent().getExtras();
        Log.i(CAT,bdl.getString("hash"));
        hash = bdl.getString("hash");
        btnOK = findViewById(R.id.buttonChoixOKMD);
        btnOK.setOnClickListener(this);

        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ListConversation> call1 = apiService.doGetListConversation(hash);

        String[] items = new String[]{};

        call1.enqueue(new Callback<ListConversation>() {
            @Override
            public void onResponse(Call<ListConversation> call, Response<ListConversation> response) {
                lc = response.body();
                List<String> spinnerArray =  new ArrayList<String>();
                List<Integer> idArray = new ArrayList<Integer>();
                for(Conversation c : lc.conversations) {
                    spinnerArray.add(c.theme);
                    idArray.add(Integer.parseInt(c.id));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ChoixConvActivityMD.this,
                        R.layout.dropdown_items,
                        spinnerArray
                );

                dropdownText.setAdapter(adapter);

                dropdownText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        alerter("ID ITEM SELECTED " + Integer.toString(idArray.get(arg2 + 1)));
                        idItemSelected = idArray.get(arg2);
                    }

                });

                Log.i(CAT,lc.toString());
            }

            @Override
            public void onFailure(Call<ListConversation> call, Throwable t) {
                call.cancel();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        alerter("Click sur OK Conv");

        if(idItemSelected == Integer.MAX_VALUE){

            dropdownText.setError("Veuillez sélectionner une conversation",null);
            alerter("Veuillez sélectionner une conversation");

        }else{
            Intent change2Conv = new Intent(this,ConvActivity.class);
            Bundle bdl = new Bundle();
            // Conversation conv = (Conversation) listeConv.getSelectedItem();
            bdl.putString("conv", Integer.toString(idItemSelected));
            bdl.putString("hash", hash);
            change2Conv.putExtras(bdl);
            startActivity(change2Conv);
        }


    }

    private void alerter(String s) {
        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        t.show();
    }

}