package com.example.chat2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@EActivity(R.layout.activity_login_m_d)
public class LoginActivityMD extends AppCompatActivity {

    private final String CAT = "LE4-SI";

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @ViewById(R.id.textInputLayoutLogin)
    TextInputLayout textInputLayoutLogin;

    @ViewById(R.id.textInputLayoutPasse)
    TextInputLayout textInputLayoutPasse;

    @ViewById(R.id.edtLogin)
    EditText edtLogin;

    @ViewById(R.id.edtPasse)
    EditText edtPasse;
    
    @ViewById(R.id.checkBoxMD)
    CheckBox checkBoxMD;

    @AfterViews
    void initialize() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        if (sp.getBoolean("remember",false)) {
            // et remplir (si nécessaire) les champs pseudo, passe, case à cocher
            checkBoxMD.setChecked(true);
            edtLogin.setText(sp.getString("login",""));
            edtPasse.setText(sp.getString("passe",""));
        }
    }

    @Click
    void buttonOKMD() {
        alerter("click sur OK ANNOTATIONS");
        if(verifReseau()) {
            // Lors de l'appui sur le bouton OK
            // si case est cochée, enregistrer les données dans les préférences
            alerter("click sur OK");
            if (checkBoxMD.isChecked()) {
                editor.putBoolean("remember",true);
                editor.putString("login", edtLogin.getText().toString());
                editor.putString("passe", edtPasse.getText().toString());
                editor.commit();
            } else {
                editor.clear();
                editor.commit();
            }

            // On envoie une requete HTTP
            doInBackground(sp.getString("urlData","http://tomnab.fr/chat-api/")+"authenticate",
                    "user=" + edtLogin.getText().toString()
                            + "&password=" + edtPasse.getText().toString());
        }

    }

    @Background
    void doInBackground(String... qs) {
        Log.i(CAT,"doInBackground");
        Log.i(CAT,qs[0]);
        Log.i(CAT,qs[1]);
        String result = requete(qs[0], qs[1]);
        Log.i(CAT,result);
        String hash = "";
        //String hash="4e28dafe87d65cca1482d21e76c61a06";

        try {

            JSONObject obR = new JSONObject(result);
            hash = obR.getString("hash");

            String res = "{\"promo\":\"2020-2021\",\"enseignants\":[{\"prenom\":\"Mohamed\",\"nom\":\"Boukadir\"},{\"prenom\":\"Thomas\",\"nom\":\"Bourdeaud'huy\"}]}";
            JSONObject ob = new JSONObject(res);
            String promo = ob.getString("promo");
            JSONArray profs = ob.getJSONArray("enseignants");
            JSONObject tom = profs.getJSONObject(1);
            String prenom = tom.getString("prenom");
            Log.i(CAT,"promo:" + promo + " prenom:" + prenom);

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            String res2 = gson.toJson(ob);
            Log.i(CAT,"chaine recue:" + res);
            Log.i(CAT,"chaine avec gson:" + res2);

            Promo unePromo = gson.fromJson(res,Promo.class);
            Log.i(CAT,unePromo.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        onPostExecute(hash);

    }

    @UiThread
    void onPostExecute(String hash) {
        Log.i(CAT,"onPostExecute");
        Log.i(CAT,hash);

        if(!hash.isEmpty()) {
            textInputLayoutLogin.setErrorEnabled(false);
            textInputLayoutPasse.setErrorEnabled(false);
            Intent iVersChoixConv = new Intent(this,ChoixConvActivityMD_.class);
            Bundle bdl = new Bundle();
            bdl.putString("hash",hash);
            iVersChoixConv.putExtras(bdl);
            startActivity(iVersChoixConv);
        } else {
            textInputLayoutLogin.setError("Erreur login ou mot de passe incorrect");
            textInputLayoutPasse.setError("Erreur login ou mot de passe incorrect");
        }
    }

    // Afficher les éléments du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Utiliser menu.xml pour créer le menu (Préférences, Mon Compte)
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void alerter(String s) {
        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        t.show();
    }

    // Gestionnaire d'événement pour le menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings :
                alerter("Préférences");
                // Changer d'activité pour afficher PrefsActivity
                Intent change2Prefs = new Intent(this,PrefActivity.class);
                startActivity(change2Prefs);
                break;
            case R.id.action_account :
                alerter("Compte");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean verifReseau()
    {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun réseau détecté";
        Boolean bStatut = false;
        if (netInfo != null)
        {

            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType)
                {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "Réseau mobile détecté"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "Réseau wifi détecté"; break;
                }

            }
        }

        alerter(sType);
        return bStatut;
    }

    public String requete(String urlData, String qs) {
        DataOutputStream dataout = null; // new:POST
        if (qs != null)
        {
            try {
                URL url = new URL(urlData); // new:POST
                Log.i(CAT,"url utilisée : " + url.toString());
                HttpURLConnection urlConnection = null;
                urlConnection = (HttpURLConnection) url.openConnection();

                // new:POST
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                dataout = new DataOutputStream(urlConnection.getOutputStream());
                dataout.writeBytes(qs);
                // new:POST

                InputStream in = null;
                in = new BufferedInputStream(urlConnection.getInputStream());
                String txtReponse = convertStreamToString(in);
                urlConnection.disconnect();
                return txtReponse;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "";
    }

    private String convertStreamToString(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}