package com.tools;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;

import java.awt.GridBagConstraints;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemPanel extends JPanel
{
	public static class KeyValue
	{
		public String key;
		public String value;
		
		public String toString()
		{
			return value;
		}
	}
	
	protected EventListenerList listenerList = new EventListenerList();
	private JList lstItem;
	private DefaultListModel<KeyValue> model;
	
	public ItemPanel()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		
		lstItem = new JList();
		lstItem.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				{//if ( e.getValueIsAdjusting() == false ) {
					int ix = e.getFirstIndex();
					if ( ix != -1 && model.isEmpty() == false )
						fireEvent("select", model.get(ix).key);
				}
			}
		});
		scrollPane.setViewportView(lstItem);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		
		JButton btnNewButton = new JButton("Add");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEvent("add", null);
			}
		});
		panel.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Delete");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEvent("delete", null);
			}
		});
		panel.add(btnNewButton_1);
		
		init();
	}
	
	private void init()
	{
		model = new DefaultListModel<KeyValue>();
		lstItem.setModel(model);
		lstItem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void addMyEventListener(ClickEventListener listener) {
		listenerList.add(ClickEventListener.class, listener);
	}

	public void removeMyEventListener(ClickEventListener listener) {
		listenerList.remove(ClickEventListener.class, listener);
	}
	
	private void fireEvent(String cmd, Object data)
	{
		ClickEventObject evt = new ClickEventObject(this, cmd, data);
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i + 2) {
			if (listeners[i] == ClickEventListener.class) {
				((ClickEventListener)listeners[i + 1]).click(evt);
			}
		}
	}
	
	public void clear()
	{
		model.clear();
	}

	/**
	 * @param keyJ
	 * @param value
	 */
	public void addItem(String key, String value)
	{
		KeyValue v = new KeyValue();
		v.key = key;
		v.value = value;
		
		model.addElement(v);
	}
	
	/**
	 * @return
	 */
	public KeyValue getSelectedItem()
	{
		int ix = lstItem.getSelectedIndex();
		if ( ix == -1 )
			return null;
		
		return model.get(ix);
	}
	
	/**
	 * @param key
	 */
	public void deleteItem(KeyValue item)
	{
		model.removeElement(item);
	}

}
