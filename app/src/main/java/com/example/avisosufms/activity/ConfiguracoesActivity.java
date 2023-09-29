package com.example.avisosufms.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.avisosufms.R;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.UsuarioFirebase;
import com.example.avisosufms.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {
    private CircleImageView circleImagePerfil;
    private TextView textEditarFoto;
    private TextInputEditText editNome, editEmail;
    private Button buttonSalvar;
    private Usuario usuarioLogado;
    private Toolbar toolbar;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        configuracoesIniciais();

        //Permissao.validarPermissoes(permissoes, this, 1);

        //carregar dados do usuario nos componentes, como nome, email e foto
        carregarDadosUsuario();

        //botão responsável por salvar novo nome do usuário
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String novoNomeUsuario = editNome.getText().toString();
                if(novoNomeUsuario.isEmpty()){
                    Toast.makeText(ConfiguracoesActivity.this, "Preencha o nome antes de salvar", Toast.LENGTH_SHORT).show();
                }else{
                    //atualizar nome do usuario no firebase auth
                    UsuarioFirebase.atualizarNomeUsuario(novoNomeUsuario);

                    //atualizar nome do usuario no firebase database
                    usuarioLogado.setNome(novoNomeUsuario);
                    usuarioLogado.atualizarNoFirebase();
                    Toast.makeText(ConfiguracoesActivity.this, "Dados alterados com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        //a foto será atualizada no firebase automaticamente após o usuario trocar
        textEditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //if(intent.resolveActivity(getPackageManager()) != null){
                galeriaActivityResult.launch(intent);
                //}
            }
        });
    }

    private void configuracoesIniciais(){
        //Configuracoes da toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);

        //recupera dados do usuario logado
        firebaseUser = UsuarioFirebase.getUsuarioLogado();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        circleImagePerfil = findViewById(R.id.circleImageEditarPerfil);
        textEditarFoto = findViewById(R.id.textEditarFoto);
        editNome = findViewById(R.id.editTextNomeEditarPerfil);
        editEmail = findViewById(R.id.editTextEmailEditarPerfil);
        editEmail.setFocusable(false);
        buttonSalvar = findViewById(R.id.buttonSalvarEditarPerfil);
    }

    private void carregarDadosUsuario(){
        //recupera foto do usuario caso tenha
        Uri url = firebaseUser.getPhotoUrl();
        if(url != null){
            Glide.with(ConfiguracoesActivity.this).load(url).into(circleImagePerfil);
        }else{
            circleImagePerfil.setImageResource(R.drawable.perfil_padrao);
        }
        //recupera nome e email do usuario
        editNome.setText(usuarioLogado.getNome());
        editEmail.setText(usuarioLogado.getEmail());
    }

    /*
    NOVA MANEIRA DE UTILIZAR O START ACTIVITY FOR RESULT
     */
    private ActivityResultLauncher<Intent> galeriaActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Bitmap imagem = null;
                        try {
                            //seleção da galeria de foto
                            Uri localImagemSelecionado = result.getData().getData();
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionado);

                            if(imagem != null){
                                //salvar imagem na tela do app
                                circleImagePerfil.setImageBitmap(imagem);

                                //recuperar dados para salvar a imagem
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                imagem.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                byte[] dadosImagem = byteArrayOutputStream.toByteArray();

                                //salvar imagem no storage do firebase
                                storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
                                StorageReference imagemRef = storageReference
                                        .child("imagens")
                                        .child("perfil")
                                        .child(usuarioLogado.getId() + ".jpg");

                                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ConfiguracoesActivity.this, "Ocorreu um erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                Uri url = task.getResult();
                                                //Atualizar foto no firebase auth
                                                UsuarioFirebase.atualizarFotoUsuario(url);

                                                //Atualizar foto no firebase database
                                                usuarioLogado.setFoto(url.toString());
                                                usuarioLogado.atualizarNoFirebase();
                                            }
                                        });
                                        Toast.makeText(ConfiguracoesActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_config, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_info){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConfiguracoesActivity.this);
            alertDialog.setTitle("Informações");
            alertDialog.setMessage("Copyright © 2023, Gabriel Portari\nTodos os direitos reservados");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}