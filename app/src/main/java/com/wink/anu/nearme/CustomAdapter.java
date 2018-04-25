package com.wink.anu.nearme;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Place>{

    public CustomAdapter(@NonNull Context context, ArrayList<Place> resource) {
        super(context,0,resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView =convertView;
        if(convertView==null)
        {
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_view,parent,false);

        }
        Place currentPlace=getItem(position);
        TextView text_name=(TextView)listItemView.findViewById(R.id.tv_hospital_name);
        TextView text_location=(TextView)listItemView.findViewById(R.id.tv_location);
       text_name.setText(currentPlace.getName());
        text_location.setText(currentPlace.getVicinity());
        return listItemView;
    }
}
