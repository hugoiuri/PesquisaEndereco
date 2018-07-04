package br.com.hugo.pesquisaendereco;

import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText edtCep;
    private TextInputEditText txtStreet;
    private TextInputEditText txtComplement;
    private TextInputEditText txtDistrict;
    private TextInputEditText txtCity;
    private TextInputEditText txtState;

    public void print(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCep = findViewById(R.id.edtCepId);
        txtStreet = findViewById(R.id.txtStreetId);
        txtComplement = findViewById(R.id.txtComplementId);
        txtDistrict = findViewById(R.id.txtDistrictId);
        txtCity = findViewById(R.id.txtCityId);
        txtState = findViewById(R.id.txtStateId);
    }

    public void onClickSearch(View view){
        String cep = edtCep.getText().toString();
        print(cep);

        if(cep == null || cep.equals("")){
            print("Obrigatório informar o CEP!");
        }else {
            print("Clicou no search");
            WebServiceAddress webServiceAddress = new WebServiceAddress();
            webServiceAddress.execute(cep);
        }
    }

    public class WebServiceAddress extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://viacep.com.br/ws/" + strings[0] + "/json/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuffer buffer = new StringBuffer();
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\n");
                }

                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {

            if(data == null)
                print("Não foi possível recuperar os dados...");
            else {
                try {

                    JSONObject json = new JSONObject(data);

                    txtStreet.setText(json.getString("logradouro"));
                    txtComplement.setText(json.getString("complemento"));
                    txtDistrict.setText(json.getString("bairro"));
                    txtCity.setText(json.getString("localidade"));
                    txtState.setText(json.getString("uf"));

                    print("Endereço recuperado com sucesso!");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
