package com.example.studentinformationshasun;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class FeeDueLVAdapter extends BaseExpandableListAdapter {
    private final Context _context;
    private final List<String> _listDataHeader; // header titles
    private final HashMap<String, List<String>> _listDataChild;  // child data in format of header title, child title

//    private ArrayList<String> feedue_list = new ArrayList<String>();
//    private ProgressDialog p;
//    Context context;

    public FeeDueLVAdapter(Context context, List<String> listDataHeader,HashMap<String, List<String>> listChildData){
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    //    @override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //    @override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        StringTokenizer st = new StringTokenizer(childText,"##");
        if (convertView == null){
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.feeduelistitems, null);
        }
        LinearLayout llFee = convertView.findViewById(R.id.llFee);
        if(childPosition % 2 == 0){
            llFee.setBackgroundResource(R.color.colorGrey);
        }else{
            llFee.setBackgroundResource(R.color.colorWhite);

        }
        TextView textDueDate = convertView.findViewById(R.id.duedate);
        TextView textFeeHead = convertView.findViewById(R.id.duename);
        TextView textDueAmt = convertView.findViewById(R.id.dueamt);

        try {
            textDueDate.setText(st.nextToken()); // due date
            textFeeHead.setText(st.nextToken()); //due name
            textDueAmt.setText(st.nextToken()); //due amount

            textDueDate.setTextSize(14); // due date
            textFeeHead.setTextSize(14); // due name
            textDueAmt.setTextSize(14); // due amount
        }catch (Exception e){}
        return convertView;
    }

    //    @override
    public int getChildrenCount(int groupPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
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
            convertView = infalInflater.inflate(R.layout.feeduegroupitem, null);
        }
        StringTokenizer st = new StringTokenizer(headerTitle,"##");
        TextView lblListHeader1 = convertView.findViewById(R.id.lblFeeDueGroupItem1);
        TextView lblListHeader2 = convertView.findViewById(R.id.lblFeeDueGroupItem2);
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

//    public FeeDueLVAdapter(Context context, int vg, ArrayList<String> feedue_list){
//        super(context,vg,feedue_list);
//
//        this.context=context;
//        groupid=vg;
//        this.feedue_list=feedue_list;
//        this.p=new ProgressDialog(context);
//    }
    // Hold views of the ListView to improve its scrolling performance
//    static class ViewHolder {
//        private TextView textTerm;
//        private TextView textDueDate;
//        private TextView textFeeHead;
//        private TextView textDueAmt;
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        View rowView = convertView;
//        // Inflate the rowlayout.xml file if convertView is null
//        if(rowView==null){
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            rowView= inflater.inflate(R.layout.feeduelistitems, parent, false);
//            FeeDueLVAdapter.ViewHolder viewHolder = new FeeDueLVAdapter.ViewHolder();
//            viewHolder.textTerm= (TextView) rowView.findViewById(R.id.term);
//            viewHolder.textDueDate= (TextView) rowView.findViewById(R.id.duedate);
//            viewHolder.textFeeHead= (TextView) rowView.findViewById(R.id.duename);
//            viewHolder.textDueAmt= (TextView) rowView.findViewById(R.id.dueamt);
//            rowView.setTag(viewHolder);
//        }
//        FeeDueLVAdapter.ViewHolder holder = (FeeDueLVAdapter.ViewHolder) rowView.getTag();
//        try {
//            StringTokenizer st = new StringTokenizer(feedue_list.get(position), "##");
//            holder.textTerm.setText(st.nextToken());
////            holder.textTerm.setWidth(200);
//            holder.textDueDate.setText(st.nextToken());
////            holder.textDueDate.setWidth(220);
//            holder.textFeeHead.setText(st.nextToken());
////            holder.textFeeHead.setWidth(200);
//            holder.textDueAmt.setText(st.nextToken());
//
//            holder.textTerm.setTextSize(16);
//            holder.textDueDate.setTextSize(16);
//            holder.textFeeHead.setTextSize(16);
//            holder.textDueAmt.setTextSize(16);
//
//            holder.textDueAmt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
//        }catch(Exception e){
//
//        }
//        return rowView;
//    }
}