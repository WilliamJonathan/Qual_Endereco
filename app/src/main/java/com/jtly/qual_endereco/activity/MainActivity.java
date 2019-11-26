package com.jtly.qual_endereco.activity;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.jtly.qual_endereco.R;
import com.jtly.qual_endereco.api.DataService;
import com.jtly.qual_endereco.model.CEP;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private TextView textoResultado;
    private EditText cep;
    private Button buscar;
    private String cepDigitado;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textoResultado = findViewById(R.id.txtResultado);
        cep = findViewById(R.id.edtCep);
        buscar = findViewById(R.id.btnPesquisar);
        progressBar = findViewById(R.id.load);


        /**
         * Configura mascara no editText
         * */
        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(cep, simpleMaskFormatter);
        cep.addTextChangedListener(maskTextWatcher);

        /**
         * Configura retrofit
         * */
        retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressBar.setVisibility(View.GONE);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                cepDigitado = cep.getText().toString();
                if (cepDigitado.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Digite o CEP para começar!", Toast.LENGTH_SHORT).show();
                }else{
                    recuperarCEP();
                }
            }
        });

    }

    private void recuperarCEP(){

        cepDigitado = cep.getText().toString();

        DataService dataService = retrofit.create(DataService.class);
        Call<CEP> call = dataService.recuperarCEP(cepDigitado);
        call.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    CEP cep = response.body();
                    textoResultado.setText(
                            "Rua: "+cep.getLogradouro()+"\n"+
                            "Bairro: "+cep.getBairro()+"\n"+
                            "Cidade: "+cep.getLocalidade()+"\n"+
                            "UF: "+cep.getUf()+"\n"+
                            "Complemento: "+cep.getComplemento()+"\n"+
                            "CEP: "+cep.getCep());
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "CEP não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CEP> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Sem contato com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
