package com.example.avisosufms.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.avisosufms.R;
import com.example.avisosufms.activity.VisualizarPostagemActivity;
import com.example.avisosufms.adapter.PostagemAdapter;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.helper.RecyclerItemClickListener;
import com.example.avisosufms.model.Postagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlunosFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostagemAdapter postagemAdapter;
    private List<Postagem> listaPostagem;

    private DatabaseReference postagensReference;
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alunos, container, false);
        configuracoesIniciais(view);

        //configurações adapter e recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postagemAdapter = new PostagemAdapter(listaPostagem, getActivity());
        recyclerView.setAdapter(postagemAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*
                Seleciona a postagem para abrir a activity para melhor visualização
                 */
                Postagem postagemSelecionada = listaPostagem.get(position);
                Intent intent = new Intent(getActivity(), VisualizarPostagemActivity.class);
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

        return view;
    }

    private void configuracoesIniciais(View view) {
        recyclerView = view.findViewById(R.id.recyclerAlunos);
        listaPostagem = new ArrayList<>();

        //postagemAdapter = new PostagemAdapter();
        postagensReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("postagem-aluno");
    }

    private void recuperarPostagens() {
        valueEventListener = postagensReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPostagem.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listaPostagem.add(dataSnapshot.getValue(Postagem.class));
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
}