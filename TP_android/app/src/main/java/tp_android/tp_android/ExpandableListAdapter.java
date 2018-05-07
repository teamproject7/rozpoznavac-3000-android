package tp_android.tp_android;

import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> egovCollections;
    private List<String> egovs;

    public ExpandableListAdapter(Activity context, List<String> egovs,
                                 Map<String, List<String>> egovCollections) {
        this.context = context;
        this.egovCollections = egovCollections;
        this.egovs = egovs;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return egovCollections.get(egovs.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String egov = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_group_item, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.egov);
        item.setText(egov);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return egovCollections.get(egovs.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return egovs.get(groupPosition);
    }

    public int getGroupCount() {
        return egovs.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String egov = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.egov);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(egov);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}