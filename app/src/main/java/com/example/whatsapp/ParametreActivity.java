package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParametreActivity extends AppCompatActivity {
    Button parametre_send;
    EditText parametre_username,parametre_status;
    CircleImageView parametre_profile;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    String currentuserid;
    String email;
    private static final int Gallerypick = 1;
    private StorageReference imageRef;
    private ProgressDialog loading;
    private String imageprofilurl="";
    DataBaseHelper dataBaseHelper;
    private SharedPreferences sp;
    Uri resultUri;
    private Toolbar home_app_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre);
        sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );
        dataBaseHelper=new DataBaseHelper(this);
        //email = getIntent().getExtras().get("email").toString();

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(currentuserid);
        imageRef = FirebaseStorage.getInstance().getReference().child("Mes images");

        initialisation();
        getUserInfo();
        parametre_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galerie = new Intent();
                galerie.setAction(Intent.ACTION_GET_CONTENT);
                galerie.setType("image/*");
                startActivityForResult(galerie,Gallerypick);
            }
        });


        parametre_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallerypick && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                loading.setTitle("Photo de profil");
                loading.setMessage("Veuillez patientez...");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                resultUri = result.getUri();
                final StorageReference path = imageRef.child(currentuserid + ".jpg");
                path.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   Task<Uri> taskresult = task.getResult().getStorage().getDownloadUrl();
                                    taskresult.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageprofilurl = uri.toString();
                                            Picasso.get().load(imageprofilurl).into(parametre_profile);
                                            reference.child("profile").setValue(imageprofilurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(ParametreActivity.this,"insertion de l'image avec succes",Toast.LENGTH_LONG).show();
                                                        loading.dismiss();
                                                    }
                                                    else
                                                    {
                                                        String erreur = task.getException().toString();
                                                        Toast.makeText(ParametreActivity.this,"echec de l'insertion du profile: "+erreur,Toast.LENGTH_LONG).show();
                                                        loading.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            String erreur = task.getException().toString();
                            Toast.makeText(ParametreActivity.this,"Echec de l'insertion du profil: "+erreur,Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }
                });
            }
        }
    }

    private void updateUser()
    {
        String username = parametre_username.getText().toString();
        String status = parametre_status.getText().toString();
        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(ParametreActivity.this,"Veuillez renseigner votre username",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(status))
        {
            Toast.makeText(ParametreActivity.this,"Veuillez renseigner votre status",Toast.LENGTH_LONG).show();
        }

        else
        {
            boolean createSuccessful,delete=false;
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            delete=db.delete(DBChat.nom_de_la_table, DBChat.colonnes_userid + "= ?"  , new String[]{currentuserid})>0;
            if(delete==true)
            {
                Log.d("ndeye", "delete ok ");

            }
            else
                Log.d("ndeye","delete failed");


            ContentValues values = new ContentValues();

            values.put(DBChat.colonnes_userid,currentuserid);
            values.put(DBChat.colonnes_username,username);
            values.put(DBChat.colonnes_profile,imageprofilurl);

            createSuccessful = db.insert(DBChat.nom_de_la_table, null, values) > 0;
            db.close();

            if(createSuccessful==true)
            {
                Log.d("ndeye", "insertion ok " + username + " " + currentuserid);

            }
            else
                Log.d("ndeye","insertion failed");

            HashMap<String,String> map = new HashMap<>();
            map.put("id",currentuserid);
            map.put("username",username);
            map.put("status",status);
            if(!imageprofilurl.equals(""))
            {
                map.put("profile",imageprofilurl);
            }

            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ParametreActivity.this,"Mise à  jour reussie !",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ParametreActivity.this,ActivityUser.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(ParametreActivity.this,"Echec de la mise à jour !",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void initialisation()
    {
        home_app_bar = findViewById(R.id.mom_app_bar2);
        setSupportActionBar(home_app_bar);
        getSupportActionBar().setTitle("Informations personnelles");
        parametre_profile = findViewById(R.id.parametre_profile);
        parametre_username = findViewById(R.id.parametre_username);
        parametre_status = findViewById(R.id.parametre_status);
        parametre_send = findViewById(R.id.parametre_send);
        loading = new ProgressDialog(this);
    }

    public void getUserInfo()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("username"))
                        && (dataSnapshot.hasChild("status")) && (dataSnapshot.hasChild("profile")))
                {
                    String nom = dataSnapshot.child("username").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String profileimg = dataSnapshot.child("profile").getValue().toString();
                    Picasso.get().load(profileimg).into(parametre_profile);
                    parametre_username.setText(nom);
                    parametre_status.setText(status);
                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("username"))
                        && (dataSnapshot.hasChild("status")))
                {
                    String nom = dataSnapshot.child("username").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    parametre_username.setText(nom);
                    parametre_status.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            Intent intent = new Intent(ParametreActivity.this,ParametreActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.deconnexion)
        {
            sp.edit().remove("login").apply();
            mAuth.signOut();
            Intent intent = new Intent(ParametreActivity.this,MainActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ParametreActivity.this, R.style.AlertDialog);
        builder.setTitle("Nom du groupe");
        final EditText nomgroupfield = new EditText(ParametreActivity.this);
        builder.setView(nomgroupfield);
        builder.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomgroup = nomgroupfield.getText().toString();
                if(TextUtils.isEmpty(nomgroup))
                {
                    Toast.makeText(ParametreActivity.this,"Donner un nom de groupe svp",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(ParametreActivity.this,GroupMembre.class);
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
}
