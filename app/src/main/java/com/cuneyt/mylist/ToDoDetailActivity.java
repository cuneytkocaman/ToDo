package com.cuneyt.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cuneyt.mylist.entities.TodoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ToDoDetailActivity extends AppCompatActivity {

    private EditText editTodoDetail;
    private DatabaseReference referenceTodo, referenceUser;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private TodoModel todoModel = new TodoModel();
    private static final String TAG = "MainActivity";

    private void visualObjects(){
        editTodoDetail = findViewById(R.id.editTodoDetail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_do_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            visualObjects();

            String incomingTodo = getIntent().getExtras().getString("todo", null);
            editTodoDetail.setText(incomingTodo);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userID = firebaseUser.getUid().toString();

            referenceTodo = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.db_todo)).child(userID);

            editTodoDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

              //      Toast.makeText(ToDoDetailActivity.this, firebaseUser.getUid().toString(), Toast.LENGTH_SHORT).show();

                    referenceTodo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d: snapshot.getChildren()){
                                TodoModel tdModel = d.getValue(TodoModel.class);

                                String key = d.getKey();
                                String id = tdModel.getId();

                                Log.d(TAG, tdModel.getTodo().toString() + " / " + key + " / " + id);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            return insets;
        });
    }
}