package com.example.babyneeds;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;
import com.example.babyneeds.ui.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Item> itemList;
    private DatabaseHandler databaseHandler;
    private FloatingActionButton fab;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Button saveButton;
    private EditText babyItem;
    private EditText itemQuantity;
    private EditText itemColor;
    private EditText itemSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        recyclerView=findViewById(R.id.recyclerview);
        fab=findViewById(R.id.fab);
        databaseHandler=new DatabaseHandler(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList=new ArrayList<>();
        //get items from db

        itemList=databaseHandler.getAllItems();

        for(Item item:itemList){
            Log.d("listact","onCreate "+item.getItemName());
        }
        recyclerViewAdapter=new RecyclerViewAdapter(this,itemList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopDialog();
            }
        });
    }

    private void createPopDialog() {
        builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.popup,null);

        babyItem=view.findViewById(R.id.baby_item);
        itemQuantity=view.findViewById(R.id.item_quantity);
        itemColor=view.findViewById(R.id.item_color);
        itemSize=view.findViewById(R.id.item_size);
        saveButton=view.findViewById(R.id.save_button);

        builder.setView(view);
        alertDialog=builder.create();
        alertDialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!babyItem.getText().toString().isEmpty() && !itemQuantity.getText().toString().isEmpty() &&
                        !itemColor.getText().toString().isEmpty() && !itemSize.getText().toString().isEmpty())
                    saveItem(v);
                else {
                    Snackbar.make(v, "Empty fields not allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveItem(View view) {
        Item item=new Item();
        String newIitem=babyItem.getText().toString().trim();
        String color=itemColor.getText().toString().trim();
        int qty= Integer.parseInt(itemQuantity.getText().toString().trim());
        int size= Integer.parseInt(itemSize.getText().toString().trim());

        item.setItemName(newIitem);
        item.setItemColor(color);
        item.setItemQuantity(qty);
        item.setItemSize(size);
        databaseHandler.addItem(item);
        Snackbar.make(view,"Item saved",Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                //next screen
                startActivity(new Intent(ListActivity.this, ListActivity.class));
                finish();
            }
        },1200);
    }
}
