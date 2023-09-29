package com.example.avisosufms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avisosufms.R;
import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.example.avisosufms.model.Postagem;
import com.example.avisosufms.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostagemAdapter extends RecyclerView.Adapter<PostagemAdapter.MyViewHolder> {
    private List<Postagem> listaPostagens;
    private Context context;
    private Usuario usuario;
    public PostagemAdapter(List<Postagem> listaPostagens, Context context) {
        this.listaPostagens = listaPostagens;
        this.context = context;
        usuario = new Usuario();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postagem_recyclerview, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Postagem postagem = listaPostagens.get(position);

        /*
        recuperar a referencia do usuario no firebase
        a partir do ID do usuario na postagem para
        configuração da exibição dos dados
         */
        DatabaseReference usuarioReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios").child(postagem.getIdUsuarioPostagem());
        usuarioReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
                holder.textEmail.setText(usuario.getEmail());

                String nome = usuario.getNome();
                String[] nome_array = nome.split(" ");
                holder.textNome.setText(nome_array[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.textTitulo.setText(postagem.getTitulo());

        //formatação do horario para mostragem
        String horario = postagem.getData() + " às " + postagem.getHora();
        holder.textHora.setText(horario);

        //Formata a publicação caso tenha mais de 100 caracteres
        if(postagem.getTexto().length() > 100){
            String texto = postagem.getTexto();
            String[] texto_array = texto.split("(?<=\\G.{" + 88 + "})");
            String textoFormatado = texto_array[0] + "... ver mais";
            holder.textTexto.setText(textoFormatado);
        }else{
            holder.textTexto.setText(postagem.getTexto());
        }

    }

    @Override
    public int getItemCount() {
        return listaPostagens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textTitulo, textNome, textEmail, textTexto, textHora;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTituloRecycler);
            textNome = itemView.findViewById(R.id.textNomeRecycler);
            textEmail = itemView.findViewById(R.id.textEmailRecycler);
            textTexto = itemView.findViewById(R.id.textTextoRecycler);
            textHora = itemView.findViewById(R.id.textHorarioRecycler);
        }
    }
}
