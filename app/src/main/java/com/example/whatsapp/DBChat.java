package com.example.whatsapp;

import android.provider.BaseColumns;

public class DBChat implements BaseColumns {

    static final String colonnes_username="username";
    static final String colonnes_userid="userid";
    static final String colonnes_profile="profile";
    static final String nom_de_la_table="MesUsers";
    static final String nom_de_la_base="Mabase";
    static final int version=1;

    static final String colonnes_nom_groupe="nom_groupe";
    static final String colonnes_userid_groupe="userid_groupe";
    static final String nom_de_la_table_groupe="MesGroupes";

    static final String colonnes_idSender="idSender";
    static final String colonnes_idReceiver="idReceiver";
    static final String colonnes_message="message";
    static final String nom_de_la_table_chat="MesChats";



    static final String colonnes_group_id="group_id";
    static final String colonnes_group_date="group_date";
    static final String colonnes_group_message="group_message";
    static final String colonnes_group_name="group_name";
    static final String colonnes_group_time="group_time";
    static final String nom_de_la_table_chatGroup="chatGroup";


}
