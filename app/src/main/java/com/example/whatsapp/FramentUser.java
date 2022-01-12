package com.example.whatsapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FramentUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FramentUser extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView textView;
    private ListView user_fragment_listview;
    private ArrayList<Users> mesusers;
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    UserAdapter userAdapter;
    DataBaseHelper dataBaseHelper;
    private SharedPreferences sp ;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FramentUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FramentUser.
     */
    // TODO: Rename and change types and number of parameters
    public static FramentUser newInstance(String param1, String param2) {
        FramentUser fragment = new FramentUser();
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

    public void creategroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        builder.setTitle("Nom du groupe");
        final EditText nomgroupfield = new EditText(getContext());
        builder.setView(nomgroupfield);
        builder.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomgroup = nomgroupfield.getText().toString();
                if(TextUtils.isEmpty(nomgroup))
                {
                    Toast.makeText(getContext(),"Donner un nom de groupe svp",Toast.LENGTH_LONG).show();
                }
                else
                {
                    creationGroup(nomgroup);
                }
            }
        });
        builder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void creationGroup(String nom)
    {
        reference.child("groupes").child(nom).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getContext()," Créer avec succés !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frament_user, container, false);
        sp = view.getContext().getSharedPreferences( "connexion" , Context.MODE_PRIVATE );
        user_fragment_listview = view.findViewById(R.id.user_fragment_listview);

        mesusers = new ArrayList<>();

        dataBaseHelper=new DataBaseHelper(view.getContext());
        SQLiteDatabase db=dataBaseHelper.getWritableDatabase();
        Cursor cursor=db.query(DBChat.nom_de_la_table,new String[]
                {
                        DBChat.colonnes_userid,DBChat.colonnes_username,DBChat.colonnes_profile
                },null,null,null,null,null);


        while (cursor.moveToNext())
        {
            int indiceUsername=cursor.getColumnIndex(DBChat.colonnes_username);
            int indiceUserid=cursor.getColumnIndex(DBChat.colonnes_userid);
            int indiceUserprofile=cursor.getColumnIndex(DBChat.colonnes_profile);
            String nom=cursor.getString(indiceUsername);
            String id=cursor.getString(indiceUserid);
            String profile=cursor.getString(indiceUserprofile);

            Users users = new Users(profile, nom);
            users.setId(id);
            String fuser=sp.getString ( "id" , "");
            if (!users.getId().equals(fuser))
                mesusers.add(users);
            userAdapter = new UserAdapter(getContext(),mesusers);
            user_fragment_listview.setAdapter(userAdapter);
        }

        userAdapter = new UserAdapter(getContext(),mesusers);

        //recuperation des users de firebase
        getUser();
        inserlocaluser();

        user_fragment_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(),Home.class);
                final  Users user=mesusers.get(position);
                i.putExtra("userid",user.getId());
                i.putExtra("username",user.getNom());
                startActivity(i);
            }
        });


        return view;
    }

    public void getUser()
    {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mesusers.clear();
                if(dataSnapshot.hasChildren())
                {
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        String nom = (String) ds.child("username").getValue();
                        String id = (String) ds.child("id").getValue();
                        String profile = (String) ds.child("profile").getValue();
                        if(nom!=null)
                        {
                            Users users = new Users(profile, nom);
                            users.setId(id);
                            Log.d("das", "id : " + users.getId());
                            System.out.println("id : " + users.getId());
                            if (!users.getId().equals(firebaseUser.getUid()))
                            {
                                mesusers.add(users);
                                boolean createSuccessful,delete=false;
                                SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

                                delete=db.delete(DBChat.nom_de_la_table, DBChat.colonnes_userid + "= ?"  , new String[]{id})>0;
                                if(delete==true)
                                {
                                    Log.d("ndeye", "delete ok ");

                                }
                                else
                                    Log.d("ndeye","delete failed");


                                ContentValues values = new ContentValues();

                                values.put(DBChat.colonnes_userid,id);
                                values.put(DBChat.colonnes_username,nom);
                                values.put(DBChat.colonnes_profile,profile);

                                createSuccessful = db.insert(DBChat.nom_de_la_table, null, values) > 0;
                                db.close();

                                if(createSuccessful==true)
                                {
                                    Log.d("ndeye", "insertion ok " + nom + " " + id);

                                }
                                else
                                    Log.d("ndeye","insertion failed");
                            }


                        }


                    }
                    userAdapter = new UserAdapter(getContext(),mesusers);
                    user_fragment_listview.setAdapter(userAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("USERS",databaseError.toException());
            }
        });
    }

    public void inserlocaluser()
    {

    }
}
