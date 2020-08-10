package com.example.babyneeds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.babyneeds.R;
import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;


    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        Item item = itemList.get(position);
//        viewHolder.itemname.setText(item.getItemName());
//        viewHolder.itemsize.setText(item.getItemSize());
//        viewHolder.itemqnty.setText(item.getItemQuantity());
//        viewHolder.itemcolor.setText(item.getItemColor());
//        viewHolder.itemdateadded.setText(item.getDateItemAdded());
        viewHolder.itemname.setText(MessageFormat.format("Goal: {0}", item.getItemName()));
        viewHolder.itemcolor.setText(MessageFormat.format("Learning Experience: {0}", item.getItemColor()));
        viewHolder.itemqnty.setText(MessageFormat.format("Time to Spend: {0}", String.valueOf(item.getItemQuantity())));
        viewHolder.itemsize.setText(MessageFormat.format("Completion(0-10): {0}", String.valueOf(item.getItemSize())));
        viewHolder.itemdateadded.setText(MessageFormat.format("Goal Added on: {0}", item.getDateItemAdded()));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemname;
        public TextView itemqnty;
        public TextView itemcolor;
        public TextView itemsize;
        public TextView itemdateadded;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            itemname = itemView.findViewById(R.id.item_name);
            itemqnty = itemView.findViewById(R.id.item_quantity);
            itemcolor = itemView.findViewById(R.id.item_color);
            itemsize = itemView.findViewById(R.id.item_size);
            itemdateadded = itemView.findViewById(R.id.item_date);
            editButton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deletebutton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position;
            position=getAdapterPosition();
            Item item=itemList.get(position);
            switch (v.getId()) {
                case R.id.editbutton:
                    editItem(item);
                    break;
                case R.id.deletebutton:
                    deleteItem(item.getId());
                    break;
            }
        }

        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);
            Button yesButton = view.findViewById(R.id.yes_conf);
            Button noButton = view.findViewById(R.id.no_conf);
            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }


        private void editItem(final Item newItem) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);
            Button saveButton;
            final EditText babyItem;
            final EditText itemQuantity;
            final EditText itemColor;
            final EditText itemSize;
            TextView title;

            babyItem = view.findViewById(R.id.baby_item);
            itemQuantity = view.findViewById(R.id.item_quantity);
            itemColor = view.findViewById(R.id.item_color);
            itemSize = view.findViewById(R.id.item_size);
            saveButton = view.findViewById(R.id.save_button);
            title = view.findViewById(R.id.title_tt);

            saveButton.setText(R.string.update_text);
            title.setText(R.string.edit_text);
            babyItem.setText(newItem.getItemName());
            itemQuantity.setText(String.valueOf(newItem.getItemQuantity()));
            itemColor.setText(newItem.getItemColor());
            itemSize.setText(String.valueOf(newItem.getItemSize()));


            builder.setView(view);
            dialog = builder.create();
            dialog.show();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //update item
                    DatabaseHandler databaseHandler=new DatabaseHandler(context);

                    newItem.setItemName(babyItem.getText().toString());
                    newItem.setItemQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                    newItem.setItemColor(itemColor.getText().toString());
                    newItem.setItemSize(Integer.parseInt(itemSize.getText().toString()));

                    if(!babyItem.getText().toString().isEmpty()&&
                            !itemColor.getText().toString().isEmpty()&&
                            !itemQuantity.getText().toString().isEmpty()&&
                            !itemSize.getText().toString().isEmpty()){
                        databaseHandler.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(),newItem);//Important
                    }else {
                        Snackbar.make(view,"Fields Empty",Snackbar.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();

                }
            });
        }
    }
}
