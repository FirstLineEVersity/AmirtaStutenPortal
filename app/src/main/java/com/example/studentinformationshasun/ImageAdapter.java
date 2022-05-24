package com.example.studentinformationshasun;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private final Context mContext;

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
        //return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        View grid;
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
//            grid = new View(mContext);
//            grid  = inflater.inflate(R.layout.gridsingle, null);
//            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
//            imageView = (ImageView)grid.findViewById(R.id.grid_image);
//            textView.setText(web[position]);
//            imageView.setImageResource(mThumbIds[position]);
//            imageView.getLayoutParams().height = 200;
//            imageView.getLayoutParams().width = 200;

            grid = new View(mContext);
            grid  = inflater.inflate(R.layout.gridsingle, null);
            TextView textView = grid.findViewById(R.id.grid_text);
            imageView = grid.findViewById(R.id.grid_image);
            imageView.getLayoutParams().height = 150;
            imageView.getLayoutParams().width = 150;
            imageView.setImageResource(mThumbIds[position]);
            imageView.setBackground(grid.getContext().getDrawable(R.drawable.grid_corner_radious));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            textView.setText(web[position]);
            textView.setTypeface(Typeface.DEFAULT);
            textView.setTextSize(14);

        } else {
            grid = convertView;
        }
        return grid;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.icon_personaldetails,R.drawable.icon_feedetails,
            R.drawable.icon_financedetails,R.drawable.icon_studentsubjects,
           // R.drawable.icon_hosteldetails,
            R.drawable.icon_subjectattendance,
            R.drawable.icon_cumulativeattendance,R.drawable.icon_hourattendance,R.drawable.icon_internalmarkdetails,
            R.drawable.icon_examdetails,R.drawable.icon_librarytransaction,
            //R.drawable.icon_others,
            R.drawable.icon_notification,
            R.drawable.icon_changepassword,R.drawable.icon_exit
    };

    public String[] web = {
            "Profile",
            "Fee Dues",
            "Fee Paid",
            "Subjects",
            //"Hostel Details",
            "Attendance",
            "Cummulative Attendance",
            "Hour Attendance",
            "Internal Marks",
            "Exam Details",
            "Library Trans.",
            //"Other Details",
            "Notifications",
            "Change Password",
            "Logout"
    };
}

