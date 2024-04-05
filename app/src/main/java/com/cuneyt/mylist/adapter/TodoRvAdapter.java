package com.cuneyt.mylist.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cuneyt.mylist.AlarmReceiver;
import com.cuneyt.mylist.R;
import com.cuneyt.mylist.ToDoDetailActivity;
import com.cuneyt.mylist.assistantclass.DateTime;
import com.cuneyt.mylist.entities.TodoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TodoRvAdapter extends RecyclerView.Adapter<TodoRvAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TodoModel> todoModelArrayList = new ArrayList<>();
    /* String timetoNotify;
    public static int broadcastCode = 0; // Ayarlanan alarmın ilk broadcast değeri. Bu değer Adapter'deki btAlarm butonu ile 1, 1 artış gösterir ve son kurulan alarm diğerinin üzerine yazmaz.
*/
    private DateTime dateTime = new DateTime();
    private DatabaseReference referenceTodo, referenceUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public void setTodoModelArrayList(ArrayList<TodoModel> todoModelArrayList) {
        this.todoModelArrayList = todoModelArrayList;
    }

    public TodoRvAdapter(Context context, ArrayList<TodoModel> todoModelArrayList) {
        this.context = context;
        this.todoModelArrayList = todoModelArrayList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder { // Satır tasarımında bulunan görsel öğeler tanıtıldı.
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
        String fiveLetterTodo = todoModelArrayList.get(position).getShowLetter() + "...";

        holder.textRvTodo.setText(fiveLetterTodo);
        holder.textRvDate.setText(creaDate);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUser = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.db_user));
        referenceTodo = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.db_todo));

        holder.textRvTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showUpdate(v, creaDate, todo, id);

            }
        });

        /*  holder.constRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.alert_alarm, null);

               // Calendar calendar = Calendar.getInstance();
                TextView textBtAlertYes = view.findViewById(R.id.textBtAlertYes);
                TextView textBtAlertNo = view.findViewById(R.id.textBtAlertNo);
                TextView textBtSelectTime = view.findViewById(R.id.textBtSelectTime);
                TextView textBtSelectDate = view.findViewById(R.id.textBtSelectDate);

                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                textBtSelectTime.setOnClickListener(new View.OnClickListener() { // Saat seçici
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timetoNotify = hourOfDay + ":" + minute;
                                textBtSelectTime.setText(formatTime(hourOfDay, minute));
                            }
                        }, hour, minute, true);

                        timePickerDialog.show();
                    }
                });

                textBtSelectDate.setOnClickListener(new View.OnClickListener() { //Tarih seçici
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                textBtSelectDate.setText(dayOfMonth + "-" + month + "-" + year);
                            }
                        }, year, month, day);

                        datePickerDialog.show();
                    }
                });

                textBtAlertYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        String date = textBtSelectDate.getText().toString();
                        String time = textBtSelectTime.getText().toString();

                        Intent intent = new Intent(context, AlarmReceiver.class);
                        intent.putExtra("time", date);
                        intent.putExtra("date", time);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                        String dateandTime = date + " " + timetoNotify;
                        DateFormat format = new SimpleDateFormat("d-M-yyyy hh:mm");

                        try {
                            Date date1 = format.parse(dateandTime);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

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
        });*/
    }

    @Override
    public int getItemCount() {
        return todoModelArrayList.size();
    }

    public void showUpdate(View v, String date, String todo, String id){
        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String currentUser = snapshot.child(firebaseUser.getUid()).child("id").getValue().toString(); // Online kullanıcı Id'si.

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.alert_todo_detail, null);

                TextView editAlertDate = view.findViewById(R.id.textAlertDate); // Alert dialog görsel objeleri
                EditText editAlertTodo = view.findViewById(R.id.editAlertTodo);
                /*TextView textBtAlertUpdate = view.findViewById(R.id.textBtAlertUpdate);
                TextView textBtAlertClose = view.findViewById(R.id.textBtAlertClose);*/
                ImageButton imgBtUpdate = view.findViewById(R.id.imgBtUpdate);
                ImageButton imgBtClose = view.findViewById(R.id.imgBtClose);

                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                editAlertTodo.setText(todo);
                editAlertDate.setText(date);

                String dt = dateTime.currentlyDateTime("dd.MM.yyyy");
                String sort = dateTime.currentlyDateTime("yyyy.MM.dd HH:MM:ss");
                imgBtUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Spannable spTodo = new SpannableString(editAlertTodo.getText().toString());
                        String fiveLetter = spTodo.subSequence(0,5).toString();

                        HashMap<String, Object> updateTodo = new HashMap<>();
                        updateTodo.put("todo", editAlertTodo.getText().toString());
                        updateTodo.put("creationDate", dt);
                        updateTodo.put("sort",sort);
                        updateTodo.put("showLetter", fiveLetter);

                        referenceTodo.child(currentUser).child(id).updateChildren(updateTodo);

                        dialog.dismiss();
                    }
                });

                imgBtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*public String formatTime(int hour, int minute) {

        String time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }

        if (hour == 0) {
            time = "12" + ":" + formattedMinute + "ÖÖ";

        } else if (hour < 12) {
            time = "12" + ":" + formattedMinute + "ÖÖ";

        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + "ÖS";

        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + "ÖS";
        }

        return time;
    }*/

    /*public void setAlarm(View v, String date, String time) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(v.getContext()).inflate(R.layout.alert_alarm, null);

        Calendar calendar = Calendar.getInstance();
        TextView textBtAlertYes = view.findViewById(R.id.textBtAlertYes);
        TextView textBtAlertNo = view.findViewById(R.id.textBtAlertNo);
        TextView textBtSelectTime = view.findViewById(R.id.textBtSelectTime);
        TextView textBtSelectDate = view.findViewById(R.id.textBtSelectDate);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textBtSelectTime.setOnClickListener(new View.OnClickListener() { // Saat seçici
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timetoNotify = hourOfDay + ":" + minute;
                        textBtSelectTime.setText(formatTime(hourOfDay, minute));
                    }
                }, hour, minute, false);

                timePickerDialog.show();
            }
        });

        textBtSelectDate.setOnClickListener(new View.OnClickListener() { //Tarih seçici
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        textBtSelectDate.setText(dayOfMonth + "." + month + "." + year);
                    }
                }, year, month, day);
            }
        });

        textBtAlertYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("time", date);
                intent.putExtra("date", time);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                String dateandTime = date + " " + timetoNotify;
                DateFormat format = new SimpleDateFormat("d-M-yyyy hh:mm");

                try {
                    Date date1 = format.parse(dateandTime);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

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
    }*/
}
