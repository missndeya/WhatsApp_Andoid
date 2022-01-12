package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Inscription extends AppCompatActivity {
    private EditText email,mdp,confmdp;
    private Button valider;
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    private ProgressDialog progressDialog;
    private SharedPreferences sp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );

        //initialisation de la base de donnees
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        email = findViewById(R.id.inscriptionemail);
        mdp = findViewById(R.id.inscriptionmdp);
        confmdp = findViewById(R.id.inscriptionconfmdp);
        valider = findViewById(R.id.inscription_login);
        progressDialog = new ProgressDialog(this);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String password = mdp.getText().toString();
                String confpassword = confmdp.getText().toString();
                if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confpassword))
                {
                    Toast.makeText(Inscription.this,"Veuillez renseigner tous les champs",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confpassword))
                {
                    Toast.makeText(Inscription.this,"Les deux mots de passe ne sont pas  conformes",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    createUser(mail,password);
                }

            }
        });
    }

    private void createUser(final String mail, String password)
    {
        progressDialog.setTitle("Création du compte !");
        progressDialog.setMessage("Veuillez patienter...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("login",mail);
                            editor.apply();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            Toast.makeText(Inscription.this, "Authentication reussie  ",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Inscription.this,ParametreActivity.class);
                            startActivity(intent);

                          FirebaseUser user = mAuth.getCurrentUser();
                            //creation du noeud Users
                            assert user != null;
                            String userid = user.getUid();
                            reference.child(userid).setValue("");

                            progressDialog.dismiss();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Inscription.this, "Echec de l'inscription ",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }

                        // ...
                    }
                });
    }
}
