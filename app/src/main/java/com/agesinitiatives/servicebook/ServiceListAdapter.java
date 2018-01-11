package com.agesinitiatives.servicebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class ServiceListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupTitles;
    private HashMap<String, List<String>> expandableListDetails;

    public ServiceListAdapter(Context context,
                              List<String> groupTitles,
                              HashMap<String, List<String>> expandableListDetails) {
        this.context = context;
        this.groupTitles = groupTitles;
        this.expandableListDetails = expandableListDetails;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetails.get(
                this.groupTitles.get(listPosition)
        ).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition,
                             final int expandedListPosition,
                             boolean isLastChild,
                             View convertView,
                             ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.listItemText);
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.groupTitles.get(listPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetails.get(
                this.groupTitles.get(listPosition)
        ).size();
    }

    @Override
    public int getGroupCount() {
        return this.groupTitles.size();
    }

    @Override
    public View getGroupView(int listPosition,
                             boolean isExpanded,
                             View convertView,
                             ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listGroupText);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
