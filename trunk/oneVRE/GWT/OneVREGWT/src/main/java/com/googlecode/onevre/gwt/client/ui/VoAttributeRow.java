package com.googlecode.onevre.gwt.client.ui;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.googlecode.onevre.gwt.client.ag.types.VOAttribute;

public class VoAttributeRow extends Image implements ChangeHandler {

	FlexTable flexTable = null;

	Vector<VOAttribute> voAttributes = null;

	VOAttribute attribute = new VOAttribute();

	ListBox voListBox = new ListBox();

	ListBox groupListBox = new ListBox();

	ListBox roleListBox = new ListBox();

	public VoAttributeRow(FlexTable table) {
		this.flexTable = table;
		int row  = flexTable.getRowCount();
		flexTable.setWidget(row,5,this);
		voListBox.addChangeHandler(this);
		groupListBox.addChangeHandler(this);
		roleListBox.addChangeHandler(this);
		flexTable.setWidget(row, 0, voListBox);
		flexTable.setWidget(row, 1, groupListBox);
		flexTable.setWidget(row, 2, roleListBox);
		voListBox.addItem("NONE");
		groupListBox.addItem("");
		roleListBox.addItem("");
		if (voAttributes!=null) {
			Vector<String> vos = new Vector<String>();
			for (VOAttribute attr : voAttributes){
				String vo = attr.getVo();
				if (!vos.contains(vo)){
					vos.add(vo);
					voListBox.addItem(vo);
				}
			}
		}
	}

	public VOAttribute getAttribute() {
		return attribute;
	}

	public void populateGroup(){
		Vector<String>grps = new Vector<String>();
		groupListBox.clear();
		groupListBox.addItem("");
		for (VOAttribute attr : voAttributes){
			if (attr.getVo().equals(attribute.getVo())){
				String grp = attr.getGroup();
				if (!grps.contains(grp)){
					grps.add(grp);
					groupListBox.addItem(grp);
				}
			}
		}
	}

	public void populateRole(){
		Vector<String>roles = new Vector<String>();
		roleListBox.clear();
		roleListBox.addItem("");
		for (VOAttribute attr : voAttributes){
			if (attr.getVo().equals(attribute.getVo()) &&
				attr.getGroup().equals(attribute.getGroup())){
				String role = attr.getRole();
				if (!roles.contains(role)){
					roles.add(role);
					roleListBox.addItem(role);
				}
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource()==voListBox){
			attribute.setVo(voListBox.getItemText(voListBox.getSelectedIndex()));
			populateGroup();
		}
		if (event.getSource()==groupListBox){
			attribute.setGroup(groupListBox.getItemText(groupListBox.getSelectedIndex()));
			populateRole();
		}
		if (event.getSource()==roleListBox){
			attribute.setRole(roleListBox.getItemText(roleListBox.getSelectedIndex()));
			populateRole();
		}
	}



}
