package com.talk.talkna.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.talk.talkna.R;
import com.talk.talkna.ModalClass.Users;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterAcitvity extends AppCompatActivity {

    CircleImageView profile_image;
    EditText register_name, register_email, register_pass, register_confirm_pass;
    TextView txt_signin, btn_signup;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri image_uri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitvity);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        btn_signup = findViewById(R.id.btn_signup);
        profile_image = findViewById(R.id.profile_image);
        register_name = findViewById(R.id.register_name);
        register_email = findViewById(R.id.register_email);
        register_pass = findViewById(R.id.register_pass);
        register_confirm_pass = findViewById(R.id.register_confirm_pass);
        txt_signin = findViewById(R.id.txt_signin);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = register_name.getText().toString();
                String email = register_email.getText().toString();
                String pass = register_pass.getText().toString();
                String c_pass = register_confirm_pass.getText().toString();
                String status = "Hey There!!!";

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(name) || TextUtils.isEmpty(c_pass)) {
                    Toast.makeText(RegisterAcitvity.this, "Enter Data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterAcitvity.this, "1", Toast.LENGTH_SHORT).show();
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(RegisterAcitvity.this, "2", Toast.LENGTH_SHORT).show();
                                DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

                                if (image_uri != null) {
                                    storageReference.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    startActivity(new Intent(RegisterAcitvity.this, HomeActivity.class));
                                                                } else {
                                                                    Toast.makeText(RegisterAcitvity.this, "Error In Creation", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    String status = "Hey There!!!";
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/talk-na-e0eee.appspot.com/o/profile_image.png?alt=media&token=5709c86b-f31c-41ae-b838-29aedf641e1e";
                                    Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(RegisterAcitvity.this, HomeActivity.class));
                                            } else {
                                                Toast.makeText(RegisterAcitvity.this, "Error In Creation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            } else {
//                                Log.e("ErrorHere", "Error");
                                Toast.makeText(RegisterAcitvity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(RegisterAcitvity.this, "Something Went Wrong Here", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterAcitvity.this, LoginActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
                image_uri = data.getData();
                profile_image.setImageURI(image_uri);
            }
        }
    }
}