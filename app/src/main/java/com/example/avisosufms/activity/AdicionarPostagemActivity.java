package com.example.avisosufms.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AdicionarPostagemActivity extends AppCompatActivity {
    private TextInputEditText editTextTitulo, editTextPublicacao;
    private Toolbar toolbar;

    private DatabaseReference databaseReference;
    private Usuario usuarioLogado;
    private AlertDialog alertDialog;

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

    private void dialogCarregando(String titulo){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregando);

        alertDialog = alert.create();
        alertDialog.show();

    }

    private void publicarPostagem(){
        if(validarCampos()){
            /*
            Cria uma referencia no database para fazer uma consulta do usuario logado,
            visto que o metodo getDadosUsuarioLogado da classe UsuarioFirebase não é possivel
            recuperar o tipo de login do usuario
             */
            dialogCarregando("Publicando");
            DatabaseReference usuarioLogadoReference = ConfiguracaoFirebase.getFirebaseDatabaseReference()
                    .child("usuarios")
                    .child(usuarioLogado.getId());
            usuarioLogadoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //recupera os dados do usuario logado via consulta no firebase
                    Postagem postagem = new Postagem();

                    postagem.setTipo(snapshot.getValue(Usuario.class).getTipo());
                    postagem.setIdUsuarioPostagem(snapshot.getValue(Usuario.class).getId());
                    postagem.setTitulo(editTextTitulo.getText().toString());
                    postagem.setTexto(editTextPublicacao.getText().toString());
                    postagem.setData(DateCustom.getDataAtual());
                    postagem.setHora(DateCustom.getHoraAtual());

                    if(postagem.salvarNoFirebase()){
                        Toast.makeText(getApplicationContext(), "Postagem publicada", Toast.LENGTH_SHORT).show();
                        Log.i("INFO POST", "Postagem salva no firebase");
                    }else{
                        Toast.makeText(getApplicationContext(), "Erro ao publicar", Toast.LENGTH_SHORT).show();
                        Log.e("INFO POST", "Erro ao salvar no firebase");
                    }
                    alertDialog.cancel();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private boolean validarCampos(){
        if(!editTextTitulo.getText().toString().isEmpty()) {
            if (!editTextPublicacao.getText().toString().isEmpty()) {
                return true;
            }else{
                Toast.makeText(this, "Preencha os campos antes de continuar!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha os campos antes de continuar!", Toast.LENGTH_SHORT).show();
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