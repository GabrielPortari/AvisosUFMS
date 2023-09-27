package com.example.avisosufms.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.example.avisosufms.R;
import com.example.avisosufms.model.Postagem;

public class VisualizarPostagemActivity extends AppCompatActivity {
    private TextView textTitulo, textNome, textEmail, textTexto, textHora;
    private Toolbar toolbar;
    private Postagem postagem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);
        configuracoesIniciais();

        //recuperar postagem passada pela outra activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            postagem = (Postagem) bundle.getSerializable("postagemSelecionada");

            textTitulo.setText(postagem.getTitulo());
            textNome.setText(postagem.getNomeUsuarioPostagem());
            textEmail.setText(postagem.getEmailUsuarioPostagem());
            textTexto.setText(postagem.getTexto());
            String dataHora = postagem.getData() + " às " + postagem.getHora();
            textHora.setText(dataHora);
        }
    }

    private void configuracoesIniciais(){
        textTitulo = findViewById(R.id.textTituloVisualizar);
        textNome = findViewById(R.id.textNomeVisualizar);
        textEmail = findViewById(R.id.textEmailVisualizar);
        textTexto = findViewById(R.id.textTextoVisualizar);
        textHora = findViewById(R.id.textDataHoraVisualizar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}