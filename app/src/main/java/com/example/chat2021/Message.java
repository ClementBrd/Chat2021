package com.example.chat2021;

public class Message {
    String id;
    String contenu;
    String auteur;
    String couleur;

    public Message(String id, String contenu, String auteur, String couleur){
        this.id = id;
        this.contenu = contenu;
        this.auteur = auteur;
        this.couleur = couleur;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", contenu='" + contenu + '\'' +
                ", auteur='" + auteur + '\'' +
                ", couleur='" + couleur + '\'' +
                '}';
    }
}
