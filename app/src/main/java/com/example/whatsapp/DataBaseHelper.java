package com.example.whatsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, DBChat.nom_de_la_base, null, DBChat.version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable ="Create table "
                +DBChat.nom_de_la_table+
                " ( "
                + DBChat._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                DBChat.colonnes_userid + " Text, " + DBChat.colonnes_username + " Text, "+ DBChat.colonnes_profile + " Text  );";


        String createTableChat ="Create table "
                +DBChat.nom_de_la_table_chat+
                " ( "
                + DBChat.colonnes_idSender + " Text, " +
                DBChat.colonnes_idReceiver + " Text, " + DBChat.colonnes_message +
                " Text );";


        String createTableGroupe ="Create table "
                +DBChat.nom_de_la_table_groupe+
                " ( "
                +  DBChat.colonnes_nom_groupe + " Text, " +  DBChat.colonnes_userid_groupe + " Text );";


        String createTableChatGroup ="Create table "
                +DBChat.nom_de_la_table_chatGroup+
                " ( "
                + DBChat.colonnes_group_date + " Text, " +
                DBChat.colonnes_group_id + " Text, " +
                DBChat.colonnes_group_message + " Text, " +
                DBChat.colonnes_group_name + " Text, " + DBChat.colonnes_group_time +
                " Text );";

        db.execSQL(createTable);
        db.execSQL(createTableGroupe);
        db.execSQL(createTableChat);
        db.execSQL(createTableChatGroup);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String delete="Drop table if exists "+DBChat.nom_de_la_table;
        String deleteTableGroupe="Drop table if exists "+DBChat.nom_de_la_table_groupe;
        String deleteTableChat="Drop table if exists "+DBChat.nom_de_la_table_chat;
        String deleteTableChatGroup="Drop table if exists "+DBChat.nom_de_la_table_chat;
        db.execSQL(delete);
        db.execSQL(deleteTableChat);
        db.execSQL(deleteTableChatGroup);
        onCreate(db);

    }
}
