package com.example.whatsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentGroups#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGroups extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private  View groupview;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private DatabaseReference reference,useridref;
    private FirebaseAuth mAuth;
    String userid;
    DataBaseHelper dataBaseHelper;

    public FragmentGroups() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentGroups.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGroups newInstance(String param1, String param2) {
        FragmentGroups fragment = new FragmentGroups();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        groupview =  inflater.inflate(R.layout.fragment_groups, container, false);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("groupes");
        useridref = FirebaseDatabase.getInstance().getReference().child("groupes");
        iniatiseFields();

        dataBaseHelper=new DataBaseHelper(getContext());
        SQLiteDatabase db=dataBaseHelper.getWritableDatabase();
        Cursor cursor=db.query(DBChat.nom_de_la_table_groupe,new String[]
                {
                        DBChat.colonnes_nom_groupe,
                        DBChat.colonnes_userid_groupe
                },null,null,null,null,null);


        while (cursor.moveToNext()) {
            int indiceNom = cursor.getColumnIndex(DBChat.colonnes_nom_groupe);
            int userid_groupe = cursor.getColumnIndex(DBChat.colonnes_userid_groupe);

            String nom = cursor.getString(indiceNom).toString();
            String colonnes_userid_groupe = cursor.getString(userid_groupe).toString();
            if(userid.equals(colonnes_userid_groupe))
            {
                arrayList.add(nom);
            }
            arrayAdapter.notifyDataSetChanged();

        }

        displayGroup();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currutgroupName = parent.getItemAtPosition(position).toString();
                Intent i = new Intent(getContext(),GroupChatActivity.class);
                i.putExtra("currentgroup",currutgroupName);
                startActivity(i);
            }
        });

        return groupview;
    }

    private void displayGroup()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    final String groupcourant = ds.getKey().toString();
                    reference.child(groupcourant).child("userid").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren())
                            {
                                String id = d.getKey().toString();
                                if(userid.equals(id))
                                {
                                    int v = 0;
                                    for (int i = 0; i<arrayList.size(); i++)
                                    {
                                        if(groupcourant.equals(arrayList.get(i)))
                                        {
                                            v = 1;
                                            break;
                                        }
                                    }
                                    if(v!=1)
                                    {
                                        arrayList.add(groupcourant);
                                        boolean createSuccessful,delete=false;
                                        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

                                        delete=db.delete(DBChat.nom_de_la_table_groupe, DBChat.colonnes_nom_groupe + "= ? and "+DBChat.colonnes_userid_groupe+"= ? "  , new String[]{groupcourant,id})>0;
                                        if(delete==true)
                                        {
                                            Log.d("ndeye", "delete ok ");

                                        }
                                        else
                                            Log.d("ndeye","delete failed");


                                        ContentValues values = new ContentValues();

                                        values.put(DBChat.colonnes_nom_groupe,groupcourant);
                                        values.put(DBChat.colonnes_userid_groupe,id);

                                        createSuccessful = db.insert(DBChat.nom_de_la_table_groupe, null, values) > 0;
                                        db.close();

                                        if(createSuccessful==true)
                                        {
                                            Log.d("ndeye", "insertion ok "  + id);

                                        }
                                        else
                                            Log.d("ndeye","insertion failed");
                                    }
                                }

                            }
                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void iniatiseFields()
    {
        listView = groupview.findViewById(R.id.listview_group);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

    }
}
