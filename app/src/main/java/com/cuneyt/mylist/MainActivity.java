package com.cuneyt.mylist;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuneyt.mylist.adapter.TodoRvAdapter;
import com.cuneyt.mylist.assistantclass.DateTime;
import com.cuneyt.mylist.assistantclass.RandomId;
import com.cuneyt.mylist.entities.TodoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTodo;
    private SearchView searchView;
    private ImageButton imgBtAdd;
    private ConstraintLayout main, constBottomBar;
    private RecyclerView rvTodo;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference referenceTodo, referenceUser;
    private TodoModel todoModel;
    private RandomId randomId = new RandomId();
    private DateTime dateTime = new DateTime();
    private TodoRvAdapter todoRvAdapter;
    private ArrayList<TodoModel> todoModelList = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private void visualObjects(){

        editTodo = findViewById(R.id.editTodo);
        imgBtAdd = findViewById(R.id.imgBtAdd);
        rvTodo = findViewById(R.id.rvTodo);
        searchView = findViewById(R.id.searchView);
        main = findViewById(R.id.main);
        constBottomBar = findViewById(R.id.constBottomBar);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            visualObjects();

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            referenceTodo = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.db_todo));
            referenceUser = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.db_user));

            imgBtAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addTodo();
                }
            });

            show();

         //   rvSearch();

            return insets;
        });
    }

    public void addTodo(){

        if (TextUtils.isEmpty(editTodo.getText().toString())){
            Toast.makeText(this, "Yazınız.", Toast.LENGTH_SHORT).show();

        } else {

            referenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String userID = firebaseUser.getUid().toString();
                    String todoNew = editTodo.getText().toString();
                    String creationDate = dateTime.currentlyDateTime("dd.MM.yyyy");
                    String notifi = "";

                    Spannable spTodo = new SpannableString(todoNew);
                    String firstLetter = spTodo.subSequence(0,1).toString();

                    String randId = randomId.randomUUID().toString();
                    String id = firstLetter + "-" +randId;

                    String sort = creationDate + todoNew;

                    todoModel = new TodoModel(id, todoNew, creationDate, notifi, sort);

                    referenceTodo.child(id).setValue(todoModel);

                    editTodo.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void show(){
        rvTodo.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, true); // VERTICAL, true: RecyclerView'e eklenen veri en alttan üste doğru eklenir.
        linearLayoutManager.setStackFromEnd(true); // RecyclerView'e eklenen veri sayfayı otomatik kaydırır.
        rvTodo.setLayoutManager(linearLayoutManager);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.desing_row_todo, null); // Satır tasarımı bağlandı.

        todoRvAdapter = new TodoRvAdapter(this, todoModelList);
        rvTodo.setAdapter(todoRvAdapter);

        String userID = firebaseUser.getUid().toString(); // Online kullanıcı ID'si alındı. Her kullanıcının kendi tablosu, kendi ID'si altında görüntülendi.

        referenceTodo = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.db_todo)).child(userID);

        referenceTodo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todoModelList.clear();

                for (DataSnapshot d : snapshot.getChildren()){
                    TodoModel todoModel1 = d.getValue(TodoModel.class);

                    todoModelList.add(todoModel1);
                }

                Collections.sort(todoModelList, new Comparator<TodoModel>() { //RecyclerView A->Z sıralama
                    @Override
                    public int compare(TodoModel tdModel, TodoModel t1) {
                        return t1.getSort().compareToIgnoreCase(tdModel.getSort());
                    }
                });

                todoRvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /*public void rvSearch() { // RecyclerView Arama İşlemleri

        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                ArrayList<TodoModel> newTodo = new ArrayList<>();

                for (TodoModel todo : todoModelList) {

                    if (todo.getTodo().toLowerCase().contains(s.toLowerCase())) {

                        newTodo.add(todo);
                    }
                }
                todoRvAdapter.setTodoModelArrayList(newTodo);
                todoRvAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }*/
}