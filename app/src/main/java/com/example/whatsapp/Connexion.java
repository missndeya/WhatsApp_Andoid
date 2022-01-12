package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class Connexion extends AppCompatActivity {
    private EditText connexionemail, connexionmdp;
    private Button valider;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    String monemail;
    private SharedPreferences sp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        connexionemail = findViewById(R.id.connexionemail);
        connexionmdp = findViewById(R.id.connexionmdp);
        valider = findViewById(R.id.connexionlogin);
        progressDialog = new ProgressDialog(this);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monemail = connexionemail.getText().toString();
                String monpass = connexionmdp.getText().toString();
                if(TextUtils.isEmpty(monemail) || TextUtils.isEmpty(monpass))
                {
                    Toast.makeText(Connexion.this,"Veuillez renseigner tous les champs",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getUser(monemail, monpass);
                }
            }
        });
    }


    public void getUser(final String monemail, String monpass) {
        progressDialog.setTitle("Connexion ");
        progressDialog.setMessage("Veuillez patienter...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(monemail, monpass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("login",monemail);
                            editor.apply();

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("sara", "signInWithEmail:success");
                            Toast.makeText(Connexion.this, "Connexion reussie", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String id = user.getUid();

                            //methode de verification du username s'il est vide ou pas
                            checkusername(id);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("sara", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Connexion.this, "Echec de l'authentication ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });
    }

    private void checkusername(String id) {
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                    if(dataSnapshot.child("username").exists())
                    {
                        Intent intent = new Intent(Connexion.this,ActivityUser.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(Connexion.this,ParametreActivity.class);
                        intent.putExtra("email",monemail);
                        startActivity(intent);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {


            }
        });
    }

}