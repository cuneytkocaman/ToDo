package com.cuneyt.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cuneyt.mylist.assistantclass.DateTime;
import com.cuneyt.mylist.entities.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private EditText editLoginName, editLoginPass;
    private TextView textBtLogin, textBtRegisterPage;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserModel userModel = new UserModel();
    private DatabaseReference referenceUser;
    private DateTime dateTime = new DateTime();

    private void visualObject(){
        textBtLogin = findViewById(R.id.textBtLogin);
        editLoginName = findViewById(R.id.editLoginName);
        editLoginPass = findViewById(R.id.editLoginPass);
        textBtRegisterPage = findViewById(R.id.textBtRegisterPage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            visualObject();

            textBtLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUser();
                }
            });

            textBtRegisterPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });

            return insets;
        });
    }

    @Override
    protected void onStart() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onStart();
    }

    private void loginUser() {
        String inputName = editLoginName.getText().toString() + "@todo.com";
        String inputPass = editLoginPass.getText().toString();

        userModel.setName(inputName);
        userModel.setPassword(inputPass);

        if (TextUtils.isEmpty(editLoginName.getText().toString()) || TextUtils.isEmpty(editLoginPass.getText().toString())){
            Toast.makeText(this, "Bilgileri giriniz.", Toast.LENGTH_SHORT).show();

        } else {

            firebaseAuth.signInWithEmailAndPassword(userModel.getName(), userModel.getPassword())
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Kullanıcı adı veya parola hatalı.", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}