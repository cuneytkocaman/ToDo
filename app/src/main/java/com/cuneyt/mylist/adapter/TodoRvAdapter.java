package com.cuneyt.mylist.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cuneyt.mylist.R;
import com.cuneyt.mylist.entities.TodoModel;

import java.util.ArrayList;
import java.util.Calendar;
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
        ConstraintLayout constRow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textRvTodo = itemView.findViewById(R.id.textRvTodo);
            textRvDate = itemView.findViewById(R.id.textRvDate);
            constRow = itemView.findViewById(R.id.constRow);
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

        holder.constRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.alert_alarm, null);

                TimePicker timePicker = view.findViewById(R.id.timePicker);
                Calendar calendar = Calendar.getInstance();
                TextView textBtAlertYes = view.findViewById(R.id.textBtAlertYes);
                TextView textBtAlertNo = view.findViewById(R.id.textBtAlertNo);

                alarm(calendar, timePicker);

                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                textBtAlertYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                textBtAlertNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return todoModelArrayList.size();
    }

    public void alarm(Calendar c, TimePicker tp){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        c.set(Calendar.HOUR_OF_DAY, tp.getHour());
        c.set(Calendar.MINUTE, tp.getMinute());

        int hour = tp.getHour();
        int min = tp.getMinute();

        String sHour = String.valueOf(hour);
        String sMin = String.valueOf(min);

    }
}
