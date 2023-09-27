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
import java.util.List;
import java.util.Locale;

public class BuscaActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerPesquisa;
    private PostagemAdapter postagemAdapter;
    private DatabaseReference databaseReference;
    private List<Postagem> listaPostagem;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);
        configuracoesIniciais();

        //configurações recycler view e adapter
        postagemAdapter = new PostagemAdapter(listaPostagem, getApplicationContext());

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
                Postagem postagemSelecionada = listaPostagem.get(position);
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
        //reinicia a lista a cada busca;
        listaPostagem.clear();

        //pesquisar usuario
        if(s.length() > 1){
            Query query = databaseReference.orderByChild("texto_minusculo")
                    .startAt(s)
                    .endAt(s + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listaPostagem.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Postagem postagem = dataSnapshot.getValue(Postagem.class);
                        listaPostagem.add(postagem);
                    }
                    postagemAdapter.notifyDataSetChanged();
                    Log.i("Busca", "Busca: " + s);
                    Log.i("ListSize", "Tamanho da lista: " + listaPostagem.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void configuracoesIniciais(){
        searchView = findViewById(R.id.searchView);
        recyclerPesquisa = findViewById(R.id.recyclerBusca);
        listaPostagem = new ArrayList<>();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("postagem-todos");

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