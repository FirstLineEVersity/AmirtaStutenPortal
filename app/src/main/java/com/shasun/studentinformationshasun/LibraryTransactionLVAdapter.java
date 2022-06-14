package com.shasun.studentinformationshasun;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shasun.studentinformationshasun.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class LibraryTransactionLVAdapter extends RecyclerView.Adapter<LibraryTransactionLVAdapter.ViewHolder> {
    private ArrayList<String> libtrans_list=new ArrayList<String>();
    private final int itemLayout;

    public LibraryTransactionLVAdapter(ArrayList<String> libtrans_list, int itemLayout) {
        this.libtrans_list = libtrans_list;
        this.itemLayout = itemLayout;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = libtrans_list.get(position);
        String[] strColumns = item.split("##");

            holder.textIssueDate.setText(strColumns[0]);
            holder.textAccessionNo.setText(strColumns[1]);
            holder.textTitle.setText(strColumns[2]);
            holder.textDueDate.setText(strColumns[3]);
            holder.textReturnDate.setText(strColumns[4]);
            holder.textStatus.setText(strColumns[5]);
            holder.textFine.setText(strColumns[6]);

    }

    @Override public int getItemCount() {
        return libtrans_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText textIssueDate;
        private final TextInputEditText textAccessionNo;
        private final TextInputEditText textTitle;
        private final TextInputEditText textDueDate;
        private final TextInputEditText textReturnDate;
        private final TextInputEditText textStatus;
        private final TextInputEditText textFine;

        public ViewHolder(View itemView) {
            super(itemView);
            textIssueDate = itemView.findViewById(R.id.txtIssueDate);
            textAccessionNo = itemView.findViewById(R.id.txtAccessionNo);
            textTitle = itemView.findViewById(R.id.txtTitle);
            textDueDate = itemView.findViewById(R.id.txtDueDate);
            textReturnDate = itemView.findViewById(R.id.txtReturnDate);
            textStatus = itemView.findViewById(R.id.txtStatus);
            textFine = itemView.findViewById(R.id.txtFine);
        }
    }
}
