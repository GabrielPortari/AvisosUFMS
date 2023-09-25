package com.example.avisosufms.model;

import com.example.avisosufms.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome;
    private String email;
    private String id;
    private String senha;

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario() {
    }
    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference userRef = databaseReference.child("usuarios").child(getId());
        userRef.setValue(this);
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
