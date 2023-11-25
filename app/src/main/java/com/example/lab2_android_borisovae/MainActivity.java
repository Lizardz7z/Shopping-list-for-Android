package com.example.lab2_android_borisovae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String m_Text="";
    SharedPreferences sPref;
    List<String> items;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadData();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DemoAdapter adapter = new DemoAdapter(items);
        recyclerView.setAdapter(adapter);

        context = this;

        findViewById(R.id.add).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Введите название товара:");

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    items.add(m_Text);
                    adapter.notifyItemInserted(items.size()-1);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });

        findViewById(R.id.deleteall).setOnClickListener(view -> {
            items.clear();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onStop(){
        super.onStop();

        SaveData();
    }
    private void SaveData() {
        sPref = getSharedPreferences("shared prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("products", json);
        editor.apply();
    }

    private void LoadData(){
        sPref = getSharedPreferences("shared prefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sPref.getString("products", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        items = gson.fromJson(json, type);

        if (items == null) {
            items = new LinkedList<>();
            items.add("Редактируйте свой список покупок");
        }
    }

    public void Edit(String value, DemoAdapter adapter){

    }
    public class DemoAdapter extends RecyclerView.Adapter<DemoVH> {

        List<String> items;

        public DemoAdapter(List<String> items) {
            this.items = items;
        }
        @NonNull
        @Override
        public DemoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view, parent, false);
            return new DemoVH(view).linkadapter(this);
        }

        @Override
        public void onBindViewHolder(@NonNull DemoVH holder, int position) {
            holder.textView.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    class DemoVH extends RecyclerView.ViewHolder{

        TextView textView;
        private DemoAdapter adapter;
        public DemoVH(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text);
            itemView.findViewById(R.id.delete).setOnClickListener(view -> {
                adapter.items.remove(getAbsoluteAdapterPosition());
                adapter.notifyItemRemoved(getAbsoluteAdapterPosition());
            });
            itemView.findViewById(R.id.edit).setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Режим редактирования");

                final EditText input = new EditText(context);
                input.setText(adapter.items.get(getAbsoluteAdapterPosition()));

                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        adapter.items.set(getAbsoluteAdapterPosition(), m_Text);
                        adapter.notifyItemChanged(getAbsoluteAdapterPosition());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            });

        }

        public DemoVH linkadapter(DemoAdapter adapter){
            this.adapter=adapter;
            return this;
        }
    }
}