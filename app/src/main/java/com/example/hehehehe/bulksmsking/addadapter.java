package com.example.hehehehe.bulksmsking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehehehe on 2/1/2018.
 */

public class addadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<addressname> arrayList;
    Context context;

    public addadapter(List<addressname> paths) {
        this.arrayList = paths;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.check,parent,false);
        return new checkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        checkViewHolder viewHolder = (checkViewHolder)holder;

        String fileName = arrayList.get(position).getFileName();
        viewHolder.textView1.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class checkViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        TextView textView1;
        public checkViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            textView1 = (TextView) itemView.findViewById(R.id.filename);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btretrieveall bt = new btretrieveall(context);
                    Log.e("get",String.valueOf(getLayoutPosition()));
                    bt.execute("delete", String.valueOf(arrayList.get(getLayoutPosition()).getCounter()));
                    delete(getLayoutPosition());

                }
            });
        }
    }
    public void notifyData(List<addressname> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.arrayList = myList;
        notifyDataSetChanged();
    }

    public void delete(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

}
