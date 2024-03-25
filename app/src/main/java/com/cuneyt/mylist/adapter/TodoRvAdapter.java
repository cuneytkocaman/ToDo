package com.cuneyt.mylist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuneyt.mylist.R;
import com.cuneyt.mylist.entities.TodoModel;

import java.util.ArrayList;
import java.util.List;

public class TodoRvAdapter extends RecyclerView.Adapter<TodoRvAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TodoModel> todoModelArrayList = new ArrayList<>();

    public void setTodoModelArrayList(ArrayList<TodoModel> todoModelArrayList) {
        this.todoModelArrayList = todoModelArrayList;
    }

    public TodoRvAdapter(Context context, ArrayList<TodoModel> todoModelArrayList) {
        this.context = context;
        this.todoModelArrayList = todoModelArrayList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{ // Satır tasarımında bulunan görsel öğeler tanıtıldı.
        TextView textRvTodo, textRvDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textRvTodo = itemView.findViewById(R.id.textRvTodo);
            textRvDate = itemView.findViewById(R.id.textRvDate);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // Satır tasarımı bağlandı
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.desing_row_todo, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String id = todoModelArrayList.get(position).getId();
        String todo = todoModelArrayList.get(position).getTodo();
        String creaDate = todoModelArrayList.get(position).getCreationDate();

        holder.textRvTodo.setText(todo);
        holder.textRvDate.setText(creaDate);
        
    }

    @Override
    public int getItemCount() {
        return todoModelArrayList.size();
    }
}
