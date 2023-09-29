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
import android.view.View;
import android.widget.TextView;

import com.example.avisosufms.R;
import com.example.avisosufms.adapter.ViewPagerAdapter;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.UsuarioFirebase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private String nomeUsuarioLogado;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fabAdicionarPostagem;

    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configuracoesIniciais();

        //configuração do tablayout e viewpager adapter;
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        //configuração dos listeners do tablayout para mudança de fragment
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        //listener do fab para adicionar postagem
        fabAdicionarPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdicionarPostagemActivity.class));
            }
        });
    }


    private void configuracoesIniciais(){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        nomeUsuarioLogado = UsuarioFirebase.getUsuarioLogado().getDisplayName();

        //configuração toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        fabAdicionarPostagem = findViewById(R.id.fabAdicionarPostagem);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_buscar){
            startActivity(new Intent(getApplicationContext(), BuscaActivity.class));
        }
        if(item.getItemId() == R.id.menu_configuracoes){
            startActivity(new Intent(getApplicationContext(), ConfiguracoesActivity.class));
        }
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