package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {
    private EditText home_chat_message;
    private ImageButton home_chat_send;
    private FirebaseAuth mAuth;
    DatabaseReference reference, messageref;
    private Toolbar home_app_bar;
    private RecyclerView recycle_view;
    LinearLayoutManager linearLayoutManager;
    private ChatsAdapter chatsAdapter;
    String idreceiver;
    String username;
    String currentuserid, currentusername;
    private final List<Chats> chatsList = new ArrayList<>();
    private final List<Chats> chatsListLocal = new ArrayList<>();

    DataBaseHelper dataBaseHelper;
    private SharedPreferences sp;
    private  String localId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );
        dataBaseHelper=new DataBaseHelper(this);
        localId=sp .getString ( "id" , "");
        idreceiver = getIntent().getExtras().get("userid").toString();
        username = getIntent().getExtras().get("username").toString();

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        messageref = FirebaseDatabase.getInstance().getReference().child("messages");

        initialiser();
        userinfo();
        select();

        home_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = home_chat_message.getText().toString();
                if(TextUtils.isEmpty(msg))
                {
                    Toast.makeText(Home.this,"veuillez saisir un message svp",Toast.LENGTH_LONG).show();
                }
                else
                {
                    sendmessage(currentuserid,idreceiver,msg);
                    home_chat_message.setText("");
                }
            }
        });
    }

    private void userinfo()
    {
        reference.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentusername = dataSnapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void sendmessage(String sender,String receiver,String message)
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("sender",sender);
        map.put("receiver",receiver);
        map.put("message",message);

        messageref.push().setValue(map);
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();
                chatsListLocal.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                    String sender = (String) ds.child("sender").getValue();
                    String receiver = (String) ds.child("receiver").getValue();
                    String message = (String) ds.child("message").getValue();
                    Chats chats = new Chats(sender,receiver,message);
                    if((chats.getIdsender().equals(currentuserid) && chats.getIdreceiver().equals(idreceiver)) ||
                            (chats.getIdsender().equals(idreceiver) && chats.getIdreceiver().equals(currentuserid)))
                    {
                        chatsList.add(chats);
                    }

                    chatsAdapter.notifyDataSetChanged();
                    recycle_view.smoothScrollToPosition(recycle_view.getAdapter().getItemCount());
                    chatsListLocal.add(chats);
                }
                insert();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialiser() {
        home_app_bar = findViewById(R.id.mom_app_bar2);
        setSupportActionBar(home_app_bar);
        getSupportActionBar().setTitle(username);

        home_chat_send = findViewById(R.id.home_chat_send);
        home_chat_message = findViewById(R.id.home_chat_message);
        recycle_view = findViewById(R.id.home_recycle_view);

        chatsAdapter = new ChatsAdapter(this,chatsList);
        recycle_view.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setAdapter(chatsAdapter);
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_option,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.parametre)
        {
            Intent intent = new Intent(Home.this,ParametreActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.deconnexion)
        {
            sp.edit().remove("login").apply();
            mAuth.signOut();
            Intent intent = new Intent(Home.this,MainActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.creer_group)
        {
            creategroup();
        }
        return true;
    }

    public void creategroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.AlertDialog);
        builder.setTitle("Nom du groupe");
        final EditText nomgroupfield = new EditText(Home.this);
        builder.setView(nomgroupfield);
        builder.setPositiveButton("creer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomgroup = nomgroupfield.getText().toString();
                if(TextUtils.isEmpty(nomgroup))
                {
                    Toast.makeText(Home.this,"donner un nom de groupe svp",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(Home.this,GroupMembre.class);
                    intent.putExtra("groupname",nomgroup);
                    startActivity(intent);
                    //creationGroup(nomgroup);
                }
            }
        });
        builder.setNegativeButton("fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public  void insert(){

        boolean createSuccessful,delete=false;
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        delete=db.delete(DBChat.nom_de_la_table_chat, DBChat.colonnes_message+ "!= ?"  , new String[]{""})>0;
        if(delete==true)
        {
            Log.d("ndeye", "delete message  ok ");

        }
        else
            Log.d("ndeye","delete failed");




        for(Chats a :chatsListLocal){
            values.put(DBChat.colonnes_idSender, a.getIdsender());
            values.put(DBChat.colonnes_idReceiver, a.getIdreceiver());
            values.put(DBChat.colonnes_message,a.getMessage());


            createSuccessful = db.insert(DBChat.nom_de_la_table_chat, null, values) > 0;


            if (createSuccessful == true) {
                Log.d("ndeye", "insertion message ok " + a.getMessage());

            } else {
                Log.d("ndeye", "insertion failed");
            }
            // db.close();
        }


    }

    public void select(){


        SQLiteDatabase db=dataBaseHelper.getWritableDatabase();
        Cursor cursor=db.query(DBChat.nom_de_la_table_chat,new String[]
                {
                        DBChat.colonnes_idSender,DBChat.colonnes_idReceiver,DBChat.colonnes_message
                },null,null,null,null,null);


        while (cursor.moveToNext())
        {
            int indiceIdSender=cursor.getColumnIndex(DBChat.colonnes_idSender);
            int indiceIdReceiver=cursor.getColumnIndex(DBChat.colonnes_idReceiver);
            int indiceMessage=cursor.getColumnIndex(DBChat.colonnes_message);

            String idsender=cursor.getString(indiceIdSender).toString();
            String idReceiver=cursor.getString(indiceIdReceiver).toString();
            String message=cursor.getString(indiceMessage).toString();

            Chats chats=new Chats(idsender,idReceiver,message);


            if((chats.getIdsender().equals(localId) && chats.getIdreceiver().equals(idreceiver)) ||
                    (chats.getIdsender().equals(idreceiver) && chats.getIdreceiver().equals(localId)))
            {
                chatsList.add(chats);
            }

            chatsAdapter.notifyDataSetChanged();
            recycle_view.smoothScrollToPosition(recycle_view.getAdapter().getItemCount());

        }




    }
}
