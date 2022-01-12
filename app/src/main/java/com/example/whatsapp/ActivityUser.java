package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ActivityUser extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FramentUser framentUser;
    private FragmentGroups fragmentGroup;
    private Toolbar  mom_app_bar ;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    private SharedPreferences sp ;
    DataBaseHelper dataBaseHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );

        try
        {
            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );
            if( currentUser!=null)
                sp.edit().putString("id", currentUser).apply();
        }
        catch (Exception e)
        {
            Log.d("ndeye","pas de connexion " );
        }
        dataBaseHelper=new DataBaseHelper(this);
        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_page);
        mom_app_bar = findViewById(R.id.mom_app_bar1);
        setSupportActionBar(mom_app_bar);
        getSupportActionBar().setTitle("WhatsApp");
        //listView = findViewById(R.id.ma_listview);

        fragmentGroup = new FragmentGroups();
        framentUser = new FramentUser();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addfragment(framentUser,"Utilisateurs");
        viewPagerAdapter.addfragment(fragmentGroup,"groupes");
        viewPager.setAdapter(viewPagerAdapter);
        /*tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_group_work_black_24dp);*/

    }

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
             Intent intent = new Intent(ActivityUser.this,ParametreActivity.class);
             startActivity(intent);
         }
        if(item.getItemId() == R.id.deconnexion)
        {
            sp.edit().remove("login").apply();
            mAuth.signOut();
            Intent intent = new Intent(ActivityUser.this,MainActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUser.this, R.style.AlertDialog);
        builder.setTitle("Nom du groupe");
        final EditText nomgroupfield = new EditText(ActivityUser.this);
        builder.setView(nomgroupfield);
        builder.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomgroup = nomgroupfield.getText().toString();
                if(TextUtils.isEmpty(nomgroup))
                {
                    Toast.makeText(ActivityUser.this,"Donner un nom de groupe svp",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(ActivityUser.this,GroupMembre.class);
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
    private void creationGroup(String nom)
    {
        reference.child("groupes").child(nom).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ActivityUser.this,"Groupe créé avec succés !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
