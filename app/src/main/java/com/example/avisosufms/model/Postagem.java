package com.example.avisosufms.model;

import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Postagem {
    private String idPostagem;
    private String nomeUsuarioPostagem;
    private String idUsuarioPostagem;
    private String data;
    private String hora;
    private String titulo;
    private String texto;
    private String tipo;

    /*
        Modelo de postagem no firebase
        postagem-tipo
            id_postagem
                titulo
                descricao
                nomeUsuario
                idUsuario
                data
                hora
         */

    public Postagem() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference postagemReference = databaseReference.child("postagem-todos");
        String idPostagem = postagemReference.push().getKey();
        setIdPostagem(idPostagem);
    }

    public boolean salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference postagemReference = databaseReference.child("postagem-todos").child(getIdPostagem());

        //salva a postagem nas postagens em geral
        postagemReference.setValue(this);

        //salva a postagem no tipo especifico
        String postagem = "postagem-"+getTipo();
        DatabaseReference postagemTipo = databaseReference.child(postagem).child(getIdPostagem());
        postagemTipo.setValue(this);

        return true;
    }
    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getNomeUsuarioPostagem() {
        return nomeUsuarioPostagem;
    }

    public void setNomeUsuarioPostagem(String nomeUsuarioPostagem) {
        this.nomeUsuarioPostagem = nomeUsuarioPostagem;
    }

    public String getIdUsuarioPostagem() {
        return idUsuarioPostagem;
    }

    public void setIdUsuarioPostagem(String idUsuarioPostagem) {
        this.idUsuarioPostagem = idUsuarioPostagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
