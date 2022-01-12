package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    private  String currentgroup, currentusername,currentuserid,currenttime,currentdate;
    private FirebaseAuth mAuth;
    private DatabaseReference reference, groupref,groupsmskeyref;
    private EditText group_chat_message;
    private ImageButton group_chat_send;
    private Toolbar mom_app_bar;
    private RecyclerView recycle_view;
    private MessageGroupAdapter messageGroupAdapter;
    private final List<MessageGroup> messageGrouplist = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private final List<MessageGroup> chatsListLocalGroup = new ArrayList<>();

    DataBaseHelper dataBaseHelper;
    private SharedPreferences sp;
    private  String localId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );
        dataBaseHelper=new DataBaseHelper(this);
        localId=sp .getString ( "id" , "");

        currentgroup = getIntent().getExtras().get("currentgroup").toString();
        //Toast.makeText(GroupChatActivity.this,""+currentgroup,Toast.LENGTH_LONG).show();

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        groupref = FirebaseDatabase.getInstance().getReference().child("groupes").child(currentgroup).child("groupmessages");

        currentuserid = mAuth.getCurrentUser().getUid();


        initialise();

        messageGroupAdapter = new MessageGroupAdapter(messageGrouplist);
        recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setAdapter(messageGroupAdapter);

        userinfo();

        group_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sauvegardesms();
                group_chat_message.setText("");
            }
        });
    }

    //display messages


    @Override
    protected void onStart() {
        super.onStart();
        select();
        groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageGrouplist.clear();
                chatsListLocalGroup.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String id = (String) ds.child("id").getValue();
                    String date = (String) ds.child("date").getValue();
                    String message = (String) ds.child("message").getValue();
                    String name = (String) ds.child("name").getValue();
                    String time = (String) ds.child("time").getValue();
                    MessageGroup messageGroup = new MessageGroup(date,id,message,name,time);
                    messageGrouplist.add(messageGroup);
                    messageGroupAdapter.notifyDataSetChanged();
                    recycle_view.smoothScrollToPosition(recycle_view.getAdapter().getItemCount());

                    MessageGroup messageGroupLocal = new MessageGroup(date,id,message,name,currentgroup);
                    chatsListLocalGroup.add(messageGroupLocal);
                }
                insert();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sauvegardesms()
    {
        String mssagekey = groupref.push().getKey().toString();
        String sms = group_chat_message.getText().toString();
        if(TextUtils.isEmpty(sms))
        {
            Toast.makeText(GroupChatActivity.this,"ecrivez un sms ",Toast.LENGTH_LONG).show();
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            currentdate = simpleDateFormat.format(calendar.getTime());

            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("hh:mm a");
            currenttime = simpleDateFormat1.format(calendar1.getTime());

            HashMap<String,Object> map = new HashMap<>();
            groupref.updateChildren(map);
            groupsmskeyref = groupref.child(mssagekey);

            HashMap<String,Object> smsmap = new HashMap<>();
            smsmap.put("id",currentuserid);
            smsmap.put("name",currentusername);
            smsmap.put("message",sms);
            smsmap.put("date",currentdate);
            smsmap.put("time",currenttime);
            groupsmskeyref.updateChildren(smsmap);
        }
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

    private void initialise() {
        mom_app_bar = findViewById(R.id.mom_app_bar2);
        setSupportActionBar(mom_app_bar);
        getSupportActionBar().setTitle(currentgroup);


        group_chat_send = findViewById(R.id.group_chat_send);
        group_chat_message = findViewById(R.id.group_chat_message);
        /*group_chat_text_display = findViewById(R.id.group_chat_text_display);
        my_scrolview = findViewById(R.id.my_scrolview);
*/

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
            Intent intent = new Intent(GroupChatActivity.this,ParametreActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.deconnexion)
        {
            sp.edit().remove("login").apply();
            mAuth.signOut();
            Intent intent = new Intent(GroupChatActivity.this,MainActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this, R.style.AlertDialog);
        builder.setTitle("Nom du groupe");
        final EditText nomgroupfield = new EditText(GroupChatActivity.this);
        builder.setView(nomgroupfield);
        builder.setPositiveButton("créer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomgroup = nomgroupfield.getText().toString();
                if(TextUtils.isEmpty(nomgroup))
                {
                    Toast.makeText(GroupChatActivity.this,"Donner un nom de groupe svp",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(GroupChatActivity.this,GroupMembre.class);
                    intent.putExtra("groupname",nomgroup);
                    startActivity(intent);
                    //creationGroup(nomgroup);
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

    public  void insert(){

        boolean createSuccessful,delete=false;
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        delete=db.delete(DBChat.nom_de_la_table_chatGroup, DBChat.colonnes_group_time+ "= ?"  , new String[]{currentgroup})>0;
        if(delete==true)
        {
            Log.d("ndeye", "delete message  ok ");

        }
        else
            Log.d("ndeye","delete failed");




        for(MessageGroup a :chatsListLocalGroup){
            values.put(DBChat.colonnes_group_date, a.getCurrentdate());
            values.put(DBChat.colonnes_group_id, a.getCurrentid());
            values.put(DBChat.colonnes_group_message,a.getMessage());
            values.put(DBChat.colonnes_group_name, a.getCurrentusername());
            values.put(DBChat.colonnes_group_time, a.getCurrenttime());


            createSuccessful = db.insert(DBChat.nom_de_la_table_chatGroup, null, values) > 0;


            if (createSuccessful == true) {
                Log.d("ndeye", "insertion message groupe  ok " + a.getMessage());

            } else {
                Log.d("ndeye", "insertion message groupe failed");
            }
            // db.close();
        }


    }

    public void select(){


        SQLiteDatabase db=dataBaseHelper.getWritableDatabase();
        Cursor cursor=db.query(DBChat.nom_de_la_table_chatGroup,new String[]
                {
                        DBChat.colonnes_group_date,
                        DBChat.colonnes_group_id,
                        DBChat.colonnes_group_message,
                        DBChat.colonnes_group_name,
                        DBChat.colonnes_group_time
                },null,null,null,null,null);


        while (cursor.moveToNext())
        {
            int indiceId=cursor.getColumnIndex(DBChat.colonnes_group_id);
            int indiceDate=cursor.getColumnIndex(DBChat.colonnes_group_date);
            int indiceTime=cursor.getColumnIndex(DBChat.colonnes_group_time);
            int indiceMsg=cursor.getColumnIndex(DBChat.colonnes_group_message);
            int indiceName=cursor.getColumnIndex(DBChat.colonnes_group_name);


            String id=cursor.getString(indiceId).toString();
            String date=cursor.getString(indiceDate).toString();
            String msg=cursor.getString(indiceMsg).toString();
            String time=cursor.getString(indiceTime).toString();
            String name=cursor.getString(indiceName).toString();

            MessageGroup messageGroup = new MessageGroup(date,id,msg,name,time);
            if(time.equals(currentgroup))
                messageGrouplist.add(messageGroup);
            messageGroupAdapter.notifyDataSetChanged();
            recycle_view.smoothScrollToPosition(recycle_view.getAdapter().getItemCount());

        }




    }
}
