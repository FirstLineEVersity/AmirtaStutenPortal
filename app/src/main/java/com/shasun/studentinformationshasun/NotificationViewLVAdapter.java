package com.shasun.studentinformationshasun;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shasun.studentinformationshasun.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class NotificationViewLVAdapter extends BaseExpandableListAdapter {
    private final Context _context;
    private final List<String> _listDataHeader; // header titles
    private final HashMap<String, List<String>> _listDataChild;  // child data in format of header title, child title

//    private ArrayList<String> feedue_list = new ArrayList<String>();
//    private ProgressDialog p;
//    Context context;

    public NotificationViewLVAdapter(Context context, List<String> listDataHeader,HashMap<String, List<String>> listChildData){
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
            convertView = infalInflater.inflate(R.layout.notificationlistitems, null);
        }
        TextInputEditText textSender = convertView.findViewById(R.id.txtSendingEmployee);
        TextInputEditText textDepartment = convertView.findViewById(R.id.txtDepartment);
        TextInputEditText textMessage = convertView.findViewById(R.id.txtMessage);

        try {
            textSender.setText(st.nextToken() + " On " + st.nextToken()); // from Employee on notification time
            textDepartment.setText(st.nextToken() + ", " + st.nextToken()); //Designation, Department
            textMessage.setText(st.nextToken()); //Message
            textSender.setTextSize(14); // from Employee
            textDepartment.setTextSize(14); // DEpartment
            textMessage.setTextSize(14); // Message
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
            convertView = infalInflater.inflate(R.layout.notificationgroupitem, null);
        }
        StringTokenizer st = new StringTokenizer(headerTitle,"##");
        RelativeLayout rlView = convertView.findViewById(R.id.rlView);
        TextView lblListHeader = convertView.findViewById(R.id.lblNotificationGroupItem);
        //TextView lblListHeader2 = (TextView) convertView.findViewById(R.id.lblFeeDueGroupItem2);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(st.nextToken());
        lblListHeader.setTextSize(16);
        rlView.setBackgroundResource(R.color.cardColorp);
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