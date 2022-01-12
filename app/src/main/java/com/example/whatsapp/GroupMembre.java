package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupMembre extends AppCompatActivity {
    private ListView group_membre_listview;
    private TextView user_membre;
    private Button group_membre_annuller,group_membre_ajouter;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference,goupidref;
    private ArrayList<Users> mesusers;
    UserAdapter userAdapter;
    ArrayList<String> listID;
    String groupname;
    String curentuserid;
    private Toolbar home_app_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_membre);
        mAuth = FirebaseAuth.getInstance();
        curentuserid = mAuth.getCurrentUser().getUid();
        groupname = getIntent().getExtras().getString("groupname");
        Toast.makeText(GroupMembre.this,""+groupname,Toast.LENGTH_LONG).show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        goupidref = FirebaseDatabase.getInstance().getReference().child("groupes").child(groupname);
        listID = new ArrayList<>();
        initialiser();
        mesusers = new ArrayList<>();

        userAdapter = new UserAdapter(GroupMembre.this,mesusers);

        getUser();

        group_membre_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final  Users user=mesusers.get(position);
                int v = 1;
                for(int i=0; i<listID.size(); i++)
                {
                    if(listID.get(i) == user.getId())
                    {
                        v = -1;
                        break;
                    }

                }
                if(v != -1)
                {
                    listID.add(user.getId());
                    user_membre.append(user.getNom()+" ");
                }

            }
        });

        group_membre_annuller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupMembre.this,ActivityUser.class);
                startActivity(i);
            }
        });

        group_membre_ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupeID();
                Intent i = new Intent(GroupMembre.this,ActivityUser.class);
                startActivity(i);
            }
        });
    }

    private void groupeID() {
        HashMap<String,String> map = new HashMap<>();
        listID.add(curentuserid);
        for(int i=0; i<listID.size(); i++)
        {
            //map.put("id",listID.get(i));
            goupidref.child("userid").child(listID.get(i)).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        groupMessage();
                    }
                }
            });
        }
    }

    private void groupMessage()
    {
        goupidref.child("groupmessages").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                /*if(task.isSuccessful())
                {
                    Toast.makeText(GroupMembre.this,"message ajouter",Toast.LENGTH_LONG).show();
                }*/
            }
        });
    }

    private void initialiser() {
        home_app_bar = findViewById(R.id.mom_app_bar2);
        setSupportActionBar(home_app_bar);
        getSupportActionBar().setTitle("Ajouter des membres");
        group_membre_listview = findViewById(R.id.group_membre_listview);
        user_membre = findViewById(R.id.user_membre);
        group_membre_annuller = findViewById(R.id.group_membre_annuller);
        group_membre_ajouter = findViewById(R.id.group_membre_ajouter);
    }

    public void getUser()
    {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
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
                            Users users = new Users(profile,nom);
                            users.setId(id);
                            if (!users.getId().equals(curentuserid))
                            {
                                mesusers.add(users);
                            }
                        }

                    }
                    userAdapter = new UserAdapter(GroupMembre.this,mesusers);
                    group_membre_listview.setAdapter(userAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("USERS",databaseError.toException());
            }
        });
    }
}
