package com.example.avisosufms.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.avisosufms.R;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.UsuarioFirebase;
import com.example.avisosufms.model.Postagem;
import com.example.avisosufms.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {
    private TextView textTitulo, textNome, textEmail, textTexto, textHora;
    private Toolbar toolbar;
    private Postagem postagem;
    private CircleImageView circleImagePerfil;
    private DatabaseReference usuarioReference;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);
        configuracoesIniciais();

        //recuperar postagem passada pela outra activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            postagem = (Postagem) bundle.getSerializable("postagemSelecionada");

            //recupera informações da postagem
            textTitulo.setText(postagem.getTitulo());
            textTexto.setText(postagem.getTexto());
            String dataHora = postagem.getData() + " às " + postagem.getHora();
            textHora.setText(dataHora);
            
            //recupera referencia do usuario para obter informações de quem publicou
            usuarioReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios").child(postagem.getIdUsuarioPostagem());
            usuarioReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usuario = snapshot.getValue(Usuario.class);

                    //recupera foto do usuario caso tenha
                    if(!usuario.getFoto().isEmpty()){
                        Uri url = Uri.parse(usuario.getFoto());
                        Glide.with(VisualizarPostagemActivity.this).load(url).into(circleImagePerfil);
                    }else{
                        circleImagePerfil.setImageResource(R.drawable.perfil_padrao);
                    }
                    //recupera nome e email do usuario
                    textNome.setText(usuario.getNome());
                    textEmail.setText(usuario.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        circleImagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Seleciona o usuario para visualizar
                 */
                Intent intent = new Intent(VisualizarPostagemActivity.this, VisualizarFotoActivity.class);
                intent.putExtra("usuarioPostagem", usuario);
                startActivity(intent);
            }
        });
    }

    private void configuracoesIniciais(){
        textTitulo = findViewById(R.id.textTituloVisualizar);
        textNome = findViewById(R.id.textNomeVisualizar);
        textEmail = findViewById(R.id.textEmailVisualizar);
        textTexto = findViewById(R.id.textTextoVisualizar);
        textHora = findViewById(R.id.textDataHoraVisualizar);
        circleImagePerfil = findViewById(R.id.circleImagePerfilVisualizar);

        usuario = new Usuario();

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