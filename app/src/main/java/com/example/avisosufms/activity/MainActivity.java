package com.example.avisosufms.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.avisosufms.R;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.UsuarioFirebase;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private String nomeUsuarioLogado;
    private Toolbar toolbar;
    private ViewPager2 viewPager2;
    private TextView textBemvindo;
    private TabLayout tabLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configuracoesIniciais();
    }


    private void configuracoesIniciais(){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        //configuração toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);

        textBemvindo = findViewById(R.id.textBemvindo);
        nomeUsuarioLogado = UsuarioFirebase.getUsuarioLogado().getDisplayName();
        textBemvindo.setText("Seja bem vindo, " + nomeUsuarioLogado);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_sair){
            deslogar();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private void deslogar(){
        try{
            firebaseAuth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}