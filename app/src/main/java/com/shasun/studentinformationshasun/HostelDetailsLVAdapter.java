package com.shasun.studentinformationshasun;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shasun.studentinformationshasun.R;

import java.util.ArrayList;

public class HostelDetailsLVAdapter extends RecyclerView.Adapter<HostelDetailsLVAdapter.ViewHolder> {
    private ArrayList<String> hostel_list=new ArrayList<String>();
    private final int itemLayout;

    public HostelDetailsLVAdapter(ArrayList<String> hostel_list, int itemLayout) {
        this.hostel_list = hostel_list;
        this.itemLayout = itemLayout;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = hostel_list.get(position);
        String[] strColumns = item.split("##");
        String strAcaHTML="<font color='#2e76b2'><b>Academic Year: </b></font>"+strColumns[0];
        String strAlloHTML="<font color='#2e76b2'><b>Allotted Date: </b></font>"+strColumns[1];
        String strHosHTML="<font color='#2e76b2'><b>Hostel: </b></font>"+strColumns[2];
        String strRooHTML="<font color='#2e76b2'><b>Room Name: </b></font>"+strColumns[3];
        String strRooTypeHTML="<font color='#2e76b2'><b>Room Type: </b></font>"+strColumns[4];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            holder.textAcademicYear.setText(Html.fromHtml(strAcaHTML,Html.FROM_HTML_MODE_LEGACY));
            holder.textAllotedDate.setText(Html.fromHtml(strAlloHTML,Html.FROM_HTML_MODE_LEGACY));
            holder.textHostelName.setText(Html.fromHtml(strHosHTML,Html.FROM_HTML_MODE_LEGACY));
            holder.textRoomName.setText(Html.fromHtml(strRooHTML,Html.FROM_HTML_MODE_LEGACY));
            holder.textRoomType.setText(Html.fromHtml(strRooTypeHTML,Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            holder.textAcademicYear.setText(Html.fromHtml(strAcaHTML));
            holder.textAllotedDate.setText(Html.fromHtml(strAlloHTML));
            holder.textHostelName.setText(Html.fromHtml(strHosHTML));
            holder.textRoomName.setText(Html.fromHtml(strRooHTML));
            holder.textRoomType.setText(Html.fromHtml(strRooTypeHTML));
        }
        holder.textAcademicYear.setTextSize(14);
        holder.textAllotedDate.setTextSize(14);
        holder.textHostelName.setTextSize(14);
        holder.textRoomName.setTextSize(14);
        holder.textRoomType.setTextSize(14);
    }

    @Override public int getItemCount() {
        return hostel_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textAcademicYear;
        private final TextView textAllotedDate;
        private final TextView textHostelName;
        private final TextView textRoomName;
        private final TextView textRoomType;

        public ViewHolder(View itemView) {
            super(itemView);
            textAcademicYear = itemView.findViewById(R.id.txtAcademicYear);
            textAllotedDate = itemView.findViewById(R.id.txtAllotedDate);
            textHostelName = itemView.findViewById(R.id.txtHostelName);
            textRoomName = itemView.findViewById(R.id.txtRoomName);
            textRoomType = itemView.findViewById(R.id.txtRoomType);
        }
    }
}
