package com.example.avisosufms.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.avisosufms.R;
import com.example.avisosufms.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarFotoActivity extends AppCompatActivity {
    private Usuario usuarioPostagem;
    private Toolbar toolbar;
    private ImageView imagePerfil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_foto);
        configuracoesIniciais();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioPostagem = (Usuario) bundle.getSerializable("usuarioPostagem");
            toolbar.setTitle(usuarioPostagem.getNome());

            if(!usuarioPostagem.getFoto().isEmpty()){
                Uri url = Uri.parse(usuarioPostagem.getFoto());
                Glide.with(VisualizarFotoActivity.this).load(url).into(imagePerfil);
            }
        }
    }

    private void configuracoesIniciais(){
        imagePerfil = findViewById(R.id.imagePerfilVisualizar);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#000000"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}