package com.example.avisosufms.model;

import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Postagem implements Serializable {
    private String idPostagem;
    private String nomeUsuarioPostagem;
    private String idUsuarioPostagem;
    private String emailUsuarioPostagem;
    private String data;
    private String hora;
    private String titulo;
    private String texto;
    private String texto_minusculo;
    private String tipo;

    /*
        Modelo de postagem no firebase
        postagem-tipo
            id_postagem
                titulo
                descricao
                nomeUsuario
                emailUsuario
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

    public String getTexto_minusculo() {
        return texto_minusculo;
    }

    public void setTexto_minusculo(String texto_minusculo) {
        this.texto_minusculo = texto_minusculo;
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

    public String getEmailUsuarioPostagem() {
        return emailUsuarioPostagem;
    }

    public void setEmailUsuarioPostagem(String emailUsuarioPostagem) {
        this.emailUsuarioPostagem = emailUsuarioPostagem;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
