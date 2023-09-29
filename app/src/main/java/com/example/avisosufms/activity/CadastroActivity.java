package com.example.avisosufms.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.UsuarioFirebase;
import com.example.avisosufms.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.example.avisosufms.R;

public class CadastroActivity extends AppCompatActivity {
    private TextInputEditText editNome, editEmail, editSenha;
    private Button botaoRegistrar;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private RadioGroup radioGroup;
    private RadioButton botaoAluno, botaoProfessor, botaoSecretaria;
    private String tipoCadastro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //configuracoes iniciais
        configuracoesIniciais();

        //recupera o tipo de cadastro selecionado
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonAluno) {
                    tipoCadastro = "aluno";
                } else if (checkedId == R.id.radioButtonProfessor) {
                    tipoCadastro = "professor";
                } else if (checkedId == R.id.radioButtonSecretaria) {
                    tipoCadastro = "secretaria";
                }
            }
        });

        //click listener para validar as entradas de cadastro
        botaoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCadastro(v);
            }
        });
    }
    public void configuracoesIniciais(){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();

        editNome = findViewById(R.id.textInputNomeCadastro);
        editNome.requestFocus();

        editEmail = findViewById(R.id.textInputEmailCadastro);
        editSenha = findViewById(R.id.textInputSenhaCadastro);
        botaoRegistrar = findViewById(R.id.buttonRegistrarCadastro);
        progressBar = findViewById(R.id.progressCadastro);
        progressBar.setVisibility(View.GONE);

        radioGroup = findViewById(R.id.radioGroup);
        botaoAluno = findViewById(R.id.radioButtonAluno);
        botaoProfessor = findViewById(R.id.radioButtonProfessor);
        botaoSecretaria = findViewById(R.id.radioButtonSecretaria);
        botaoAluno.setActivated(true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);
    }

    public void validarCadastro(View view){
        String textNome = editNome.getText().toString();
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        if(!textNome.isEmpty()){
            if(!textEmail.isEmpty()){
                if(!textSenha.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setNome(textNome);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);
                    usuario.setFoto("");
                    usuario.setTipo(tipoCadastro);

                    cadastrarUsuario(usuario);
                }else{
                    Toast.makeText(CadastroActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CadastroActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
        }
    }
    public void cadastrarUsuario(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            try {
                                progressBar.setVisibility(View.GONE);

                                //Salvar dados no firebase database
                                String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId(idUsuario);
                                usuario.salvarNoFirebase();

                                //salva o nome do usuario no profile do auth
                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                                Log.i("AUTH", "Cadastro de usuário completo");
                                Toast.makeText(CadastroActivity.this, "Cadastro completo com sucesso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            progressBar.setVisibility(View.GONE);
                            String exception;
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                exception = "Digite uma senha mais forte";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                exception = "Digite um email válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                exception = "Conta já cadastrada";
                            }catch (Exception e){
                                exception = "Erro ao cadastrar usuario: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}