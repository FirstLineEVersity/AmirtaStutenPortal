package com.amirta.studentinformation;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amirta.studentinformation.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class FinanceLVAdapter extends BaseExpandableListAdapter {
    private final Context _context;
    private final List<String> _listDataHeader; // header titles
    private final HashMap<String, List<String>> _listDataChild;  // child data in format of header title, child title


    public FinanceLVAdapter(Context context, List<String> listDataHeader,HashMap<String, List<String>> listChildData){
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    //    @override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //    @override
    public View getChildView(int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent){
        final String childText = (String) getChild(groupPosition, childPosition);
        StringTokenizer st = new StringTokenizer(childText,"##");
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.financelistitems, null);
        }
        LinearLayout llItem = convertView.findViewById(R.id.llItem);
        if(childPosition % 2==0){
            llItem.setBackgroundColor(convertView.getContext().getColor(R.color.colorGrey));
        }else{
            llItem.setBackgroundColor(convertView.getContext().getColor(R.color.colorWhite));

        }

        TextView textRecDate = convertView.findViewById(R.id.recdate);
        TextView textFeeHead = convertView.findViewById(R.id.feehead);
        TextView textRecAmt = convertView.findViewById(R.id.amtpaid);
        TextView textPaidMode = convertView.findViewById(R.id.recmode);

        try {
            textRecDate.setText(st.nextToken()); // rec date
            textFeeHead.setText(st.nextToken()); //head name
            textRecAmt.setText(st.nextToken()); //paid amount
            textPaidMode.setText(st.nextToken()); //paid mode

            textRecDate.setTextSize(14); // rec date
            textFeeHead.setTextSize(14); // head name
            textRecAmt.setTextSize(14); // paid amount
            textPaidMode.setTextSize(14); // paid mode
        }catch (Exception e){}
        return convertView;
    }

    //    @override
    public int getChildrenCount(int groupPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    //    @override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    //    @override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    //    @override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //    @override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent){
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.financedetailsgroupitem, null);
        }
        StringTokenizer st = new StringTokenizer(headerTitle,"##");
        TextView lblListHeader1 = convertView.findViewById(R.id.lblFeePaidGroupItem1);
        TextView lblListHeader2 = convertView.findViewById(R.id.lblFeePaidGroupItem2);

        lblListHeader1.setTypeface(null, Typeface.BOLD);
        lblListHeader1.setText(st.nextToken());
        lblListHeader1.setTextSize(16);

        lblListHeader2.setTypeface(null, Typeface.BOLD);
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        lblListHeader2.setText(formatter.format(Double.parseDouble(st.nextToken()+"")));
        lblListHeader2.setTextSize(16);
        return convertView;
    }

    //    @override
    public boolean hasStableIds() {
        return false;
    }

    //    @override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
