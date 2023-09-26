package com.example.avisosufms.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.avisosufms.R;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.DateCustom;
import com.example.avisosufms.helper.UsuarioFirebase;
import com.example.avisosufms.model.Postagem;
import com.example.avisosufms.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

public class AdicionarPostagemActivity extends AppCompatActivity {
    private TextInputEditText editTextTitulo, editTextPublicacao;
    private Toolbar toolbar;

    private DatabaseReference databaseReference;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_postagem);
        configuracoesIniciais();
    }

    private void configuracoesIniciais(){
        //recupera informações iniciais
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        editTextPublicacao = findViewById(R.id.editTextPublicacao);
        editTextTitulo = findViewById(R.id.editTextTitulo);

        //recupera o usuario logado para a postagem
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //configurações da toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);
    }

    private void publicarPostagem(){
        if(validarCampos()){
            Postagem postagem = new Postagem();

            postagem.setNomeUsuarioPostagem(usuarioLogado.getNome());
            postagem.setIdUsuarioPostagem(usuarioLogado.getId());
            postagem.setTipo(usuarioLogado.getTipo());
            postagem.setTitulo(editTextTitulo.getText().toString());
            postagem.setTexto(editTextPublicacao.getText().toString());

            postagem.setData(DateCustom.getDataAtual());
            postagem.setHora(DateCustom.getHoraAtual());

            if(postagem.salvarNoFirebase()){
                Toast.makeText(this, "Postagem publicada", Toast.LENGTH_SHORT).show();
                Log.i("INFO POST", "Postagem salva no firebase");
            }else{
                Toast.makeText(this, "Erro ao publicar", Toast.LENGTH_SHORT).show();
                Log.e("INFO POST", "Erro ao salvar no firebase");
            }
            finish();
        }
    }
    private boolean validarCampos(){
        if(!editTextTitulo.getText().toString().isEmpty()) {
            if (!editTextPublicacao.getText().toString().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_publicar, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_publicar){
            //publicar a postagem
            publicarPostagem();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}