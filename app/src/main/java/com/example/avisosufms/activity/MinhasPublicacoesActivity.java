package com.example.avisosufms.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.avisosufms.R;
import com.example.avisosufms.adapter.PostagemAdapter;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.RecyclerItemClickListener;
import com.example.avisosufms.helper.UsuarioFirebase;
import com.example.avisosufms.model.Postagem;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinhasPublicacoesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostagemAdapter postagemAdapter;
    private List<Postagem> listaPostagem;
    private DatabaseReference postagensReference;
    private ValueEventListener valueEventListener;
    private Postagem postagem, postagemExcluir;
    private String idUsuario;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_publicacoes);

        configuracoesIniciais();
        swipe();

        //configurações adapter e recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postagemAdapter = new PostagemAdapter(listaPostagem, this);
        recyclerView.setAdapter(postagemAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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
    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.ACTION_STATE_IDLE; // drag faz menção a mover o item da recycler para qualquer lado
                int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END; // swipe faz menção para arrastar para os lados, neste caso vai para direita/esquerda
                return makeMovementFlags(dragFlag, swipeFlag); // retorna drag = inativo, swipe = para esquerda e direita
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirPublicacao(viewHolder); // ao usar swipe na horizontal, chama o método excluirMovimentacao para o viewHolder que foi swipado
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excluirPublicacao(RecyclerView.ViewHolder viewHolder){
        //Configuração do AlertDialog, que pede a confirmação do usuário para excluir ou não uma movimentação do app/firebase
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exclusão");
        alertDialog.setMessage("Deseja excluir essa publicação?");
        alertDialog.setCancelable(false);
        //Configuração para caso o usuário deseja confirmar a exclusão
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Recupera a posição do viewHolder swipado no adapter;
                int posicao = viewHolder.getAdapterPosition();
                //Apos recuperar a posição do viewHolder, recupera a posição da movimentação na lista
                postagemExcluir = listaPostagem.get(posicao);
                postagemExcluir.excluirPostagem();
            }
        });
        //Configuração caso o usuário cancele a exclusão
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Apenas é mostrado um toast e o item swipado retorna para a tela
                Toast.makeText(MinhasPublicacoesActivity.this, "Exclusão cancelada", Toast.LENGTH_SHORT).show();
                postagemAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void configuracoesIniciais(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Minhas publicações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);

        recyclerView = findViewById(R.id.recyclerMinhasPublicacoes);
        listaPostagem = new ArrayList<>();
        idUsuario = UsuarioFirebase.getIdUsuario();
        postagensReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("postagem-todos");
    }

    private void recuperarPostagens(){
        valueEventListener = postagensReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPostagem.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    postagem = dataSnapshot.getValue(Postagem.class);
                    if(postagem.getIdUsuarioPostagem().equals(idUsuario)){
                        listaPostagem.add(dataSnapshot.getValue(Postagem.class));
                    }
                }
                Collections.reverse(listaPostagem);
                postagemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}