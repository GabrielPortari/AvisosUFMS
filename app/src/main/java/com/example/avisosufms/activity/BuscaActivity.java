package com.example.avisosufms.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.avisosufms.R;
import com.example.avisosufms.adapter.PostagemAdapter;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.RecyclerItemClickListener;
import com.example.avisosufms.model.Postagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BuscaActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerPesquisa;
    private PostagemAdapter postagemAdapter;
    private DatabaseReference postagensReference;
    private List<Postagem> listaPostagem, listaPostagemFiltrada;
    private Toolbar toolbar;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);
        configuracoesIniciais();

        //configurações recycler view e adapter
        postagemAdapter = new PostagemAdapter(listaPostagemFiltrada, getApplicationContext());

        recyclerPesquisa.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerPesquisa.setLayoutManager(layoutManager);
        recyclerPesquisa.setAdapter(postagemAdapter);

        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerPesquisa, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*
                Seleciona a postagem para abrir a activity para melhor visualização
                 */
                Postagem postagemSelecionada = listaPostagemFiltrada.get(position);
                Intent intent = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);
                intent.putExtra("postagemSelecionada", postagemSelecionada);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        searchView.setQueryHint("Buscar publicação");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*
                Faz a busca ao clicar na lupa, não será utilizado
                 */
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoPesquisado = newText.toLowerCase();
                //busca pelas postagens a cada letra digitado
                buscarPostagem(textoPesquisado);
                return true;
            }
        });

    }

    private void buscarPostagem(String s){
        listaPostagemFiltrada.clear();
        for(Postagem p : listaPostagem){
            if(p.getTexto_minusculo().contains(s) || p.getTitulo().toLowerCase().contains(s)){
                listaPostagemFiltrada.add(p);
            }
        }
        Collections.reverse(listaPostagemFiltrada);
        postagemAdapter.notifyDataSetChanged();
    }

    private void recuperarPostagens(){
        valueEventListener = postagensReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPostagem.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listaPostagem.add(dataSnapshot.getValue(Postagem.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configuracoesIniciais(){
        searchView = findViewById(R.id.searchView);
        recyclerPesquisa = findViewById(R.id.recyclerBusca);
        listaPostagem = new ArrayList<>();
        listaPostagemFiltrada = new ArrayList<>();
        postagensReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("postagem-todos");

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
    @Override
    public void onStart() {
        super.onStart();
        recuperarPostagens();
    }

    @Override
    public void onStop() {
        super.onStop();
        postagensReference.removeEventListener(valueEventListener);
    }
}