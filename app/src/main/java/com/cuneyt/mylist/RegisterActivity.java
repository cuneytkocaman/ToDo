package com.cuneyt.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText editRegName, editRegPass, editRegPassAgain;

    private TextView textBtRegister;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference referenceUser;
    private UserModel userModel = new UserModel();
    private DateTime dateTime = new DateTime();

    private void visualObjects(){
        editRegName = findViewById(R.id.editRegName);
        editRegPass = findViewById(R.id.editRegPass);
        editRegPassAgain = findViewById(R.id.editRegName);
        textBtRegister = findViewById(R.id.textBtRegister);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            visualObjects();

            firebaseAuth = FirebaseAuth.getInstance();

            textBtRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerUser();
                }
            });

            return insets;
        });
    }

    private void registerUser() {

        String regName = editRegName.getText().toString() + "@todo.com";
        String pass = editRegPass.getText().toString();
        String passAgain = editRegPassAgain.getText().toString();
        String date = dateTime.currentlyDateTime("dd.MM.yyyy HH:mm:ss").toString();

        userModel.setName(regName);
        userModel.setPassword(pass);
        userModel.setMemberDateTime(date);

        checkUserName(regName); // Aynı isimli kullanıcı var mı yok mu kontrol edildi.

        int passLenght = pass.length();
        int regNameLehght = regName.length(); // İsmin ve parolanın uzunlukları alındı.

        if (TextUtils.isEmpty(editRegName.getText().toString()) ||
                TextUtils.isEmpty(editRegPass.getText().toString()) ||
                TextUtils.isEmpty(editRegPass.getText().toString())) {

            Toast.makeText(RegisterActivity.this, "Alanlar boş geçilemez.", Toast.LENGTH_SHORT).show();

        } else if (passLenght < 6) {

            Toast.makeText(RegisterActivity.this, "Parola en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show();

        } /*else if (!pass.equals(passAgain)) {

            Toast.makeText(RegisterActivity.this, "Parolalar aynı değil.", Toast.LENGTH_SHORT).show();

        } */else if (regNameLehght <= 2) {

            Toast.makeText(RegisterActivity.this, "Kullanıcı adı 3 karakterden uzun olmalıdır.", Toast.LENGTH_SHORT).show();

        } else {

            firebaseAuth.createUserWithEmailAndPassword(userModel.getName(), userModel.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {  // Kayıt başarılı ise çalışır ve kullanıcı kaydı tamamlanır.
                                firebaseUser = firebaseAuth.getCurrentUser();
                                referenceUser = FirebaseDatabase.getInstance().getReference().child("User");

                                referenceUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String userName = editRegName.getText().toString();
                                        String id = firebaseUser.getUid().toString();
                                        String member = userModel.getMemberDateTime().toString();

                                        userModel = new UserModel(id, userName, member);

                                        referenceUser.child(id).setValue(userModel);

                                        Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intentLogin);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                Toast.makeText(RegisterActivity.this, "Kayıt başarılı.", Toast.LENGTH_LONG).show();


                            }
                        }
                    });
        }
    }

    private void checkUserName(String againName){
        firebaseAuth.fetchSignInMethodsForEmail(againName).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()){

                    boolean check = !task.getResult().getSignInMethods().isEmpty();

                    if (!check){
                        //     Toast.makeText(RegisterActivity.this, "Başarılı", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegisterActivity.this, "Böyle bir kullanıcı mevcut.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}