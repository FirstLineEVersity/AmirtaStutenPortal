package com.amirta.studentinformation;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amirta.studentinformation.R;

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
        View grid = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid  = inflater.inflate(R.layout.gridsingle, null);

        }
            TextView textView = grid.findViewById(R.id.grid_text);
            imageView = grid.findViewById(R.id.grid_image);
            imageView.getLayoutParams().height = 120;
            imageView.getLayoutParams().width = 120;
            imageView.setImageResource(mThumbIds[position]);
            imageView.setBackground(grid.getContext().getDrawable(R.drawable.grid_corner_radious));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            textView.setText(web[position]);
            textView.setTypeface(Typeface.DEFAULT);
            textView.setTextSize(14);
        textView.setTextColor(mContext.getColor(R.color.colorBlack));


        return grid;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
    public Integer[] mThumbIds = {
            R.drawable.icon_personaldetails,
            R.drawable.icon_timetable,
            R.drawable.icon_feedetails,
            R.drawable.icon_financedetails,
            R.drawable.icon_studentsubjects,
            R.drawable.icon_internalmarkdetails,
            R.drawable.icon_examdetails,
         //   R.drawable.icon_librarytransaction,
            R.drawable.icon_notification,
          //  R.drawable.icon_enotice,
           // R.drawable.icon_lms,
            R.drawable.icon_subjectattendance,
            R.drawable.icon_hourattendance,
            R.drawable.icon_cumulativeattendance,
           // R.drawable.icon_leaveentry,
          //  R.drawable.icon_helpdesk,
            R.drawable.icon_changepassword,
            //  R.drawable.icon_others,
           // R.drawable.icon_canteen,
            R.drawable.icon_exit
    };

    public String[] web = {
            "Profile",
            "Timetable",
            "Fee Dues",
            "Fee Paid",
            "Subjects",
            "Internal Marks",
            "Exam Details",
        //    "Library Trans.",
            "Notifications",
           // "E-Notice",
            //"LMS",
            "Attendance",
            "Hour Attendance",
            "Cumulative Attendance",
            //"Leave Entry",
            //"Help Desk",
            "Change Password",
            //   "Other Details",
           // "Canteen",
            "Logout"
    };

}

