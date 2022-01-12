 package com.example.whatsapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

 public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Users> listuser;
    private LayoutInflater inflater;

    //constructeur
    public UserAdapter(Context context, ArrayList<Users> listuser) {
        this.context = context;
        this.listuser = listuser;
        this.inflater  = inflater.from(context);
    }

    //nombre d'element qu'il y aura sur l'ecran
    @Override
    public int getCount() {
        return listuser.size();
    }

    //permet de retourner l'ojet en question
    @Override
    public Users getItem(int position) {
        return listuser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //permet de personnaliser chaque element grace a l'inflater
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.item,null);

        //recuperation du championnat courant
        Users courantItem = getItem(position);
        String nom = courantItem.getNom();
        String image = courantItem.getImage();
        CircleImageView itemIcon = convertView.findViewById(R.id.item_icon);
        Picasso.get().load(listuser.get(position).getImage()).into(itemIcon);
        /*int resId = (int) context.getResources().getIdentifier(String.valueOf(image),"drawable",context.getPackageName());
        itemIcon.setImageResource(resId);*/

        TextView nomItem = convertView.findViewById(R.id.nom);
        nomItem.setText(nom);
        return convertView;
    }
}
