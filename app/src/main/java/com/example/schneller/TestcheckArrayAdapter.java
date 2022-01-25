package com.example.schneller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TestcheckArrayAdapter extends ArrayAdapter<Testcheck> implements View.OnClickListener {
    private ArrayList<Testcheck> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtManufacturer;
    }

    public TestcheckArrayAdapter(ArrayList<Testcheck> data, Context context)    {
        super(context, R.layout.row_item, data);
        dataSet = data;
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        Testcheck ts = (Testcheck) obj;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {
        Testcheck tc = getItem(position);
        ViewHolder vw;

        final View result;

        if (convertView == null) {
            vw = new ViewHolder();
            LayoutInflater lif = LayoutInflater.from(getContext());
            convertView = lif.inflate(R.layout.row_item, parent, false);
            vw.txtName = (TextView) convertView.findViewById(R.id.name);
            vw.txtManufacturer = (TextView) convertView.findViewById(R.id.manufacturer);

            result = convertView;

            convertView.setTag(vw);
        } else {
            vw = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        vw.txtName.setText(tc.getName());
        vw.txtManufacturer.setText(tc.getManufacturer());

        return convertView;
    }
}
