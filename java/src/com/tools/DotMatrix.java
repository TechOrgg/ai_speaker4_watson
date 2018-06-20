package com.tools;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * DOT MATRIX 데이터를 생성하는 UI를 제공한다.
 *
 */
public class DotMatrix extends JPanel {
	private JTextField txtValue;
	private OneRow row1;
	private OneRow row2;
	private OneRow row3;
	private OneRow row4;
	private OneRow row5;
	private OneRow row6;
	private OneRow row7;
	private OneRow row8;
	public DotMatrix() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Dot Matrix", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		row1 = new OneRow();
		GridBagConstraints gbc_row1 = new GridBagConstraints();
		gbc_row1.insets = new Insets(0, 0, 5, 0);
		gbc_row1.fill = GridBagConstraints.VERTICAL;
		gbc_row1.gridx = 0;
		gbc_row1.gridy = 0;
		panel.add(row1, gbc_row1);
		
		row2 = new OneRow();
		GridBagLayout gbl_row2 = (GridBagLayout) row2.getLayout();
		gbl_row2.rowWeights = new double[]{0.0};
		gbl_row2.rowHeights = new int[]{0};
		gbl_row2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_row2.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		GridBagConstraints gbc_row2 = new GridBagConstraints();
		gbc_row2.insets = new Insets(0, 0, 5, 0);
		gbc_row2.fill = GridBagConstraints.BOTH;
		gbc_row2.gridx = 0;
		gbc_row2.gridy = 1;
		panel.add(row2, gbc_row2);
		
		row3 = new OneRow();
		GridBagLayout gbl_row3 = (GridBagLayout) row3.getLayout();
		gbl_row3.rowWeights = new double[]{0.0};
		gbl_row3.rowHeights = new int[]{0};
		gbl_row3.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_row3.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		GridBagConstraints gbc_row3 = new GridBagConstraints();
		gbc_row3.insets = new Insets(0, 0, 5, 0);
		gbc_row3.fill = GridBagConstraints.BOTH;
		gbc_row3.gridx = 0;
		gbc_row3.gridy = 2;
		panel.add(row3, gbc_row3);
		
		row4 = new OneRow();
		GridBagLayout gbl_row4 = (GridBagLayout) row4.getLayout();
		gbl_row4.rowWeights = new double[]{0.0};
		gbl_row4.rowHeights = new int[]{0};
		gbl_row4.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_row4.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		GridBagConstraints gbc_row4 = new GridBagConstraints();
		gbc_row4.insets = new Insets(0, 0, 5, 0);
		gbc_row4.fill = GridBagConstraints.BOTH;
		gbc_row4.gridx = 0;
		gbc_row4.gridy = 3;
		panel.add(row4, gbc_row4);
		
		row5 = new OneRow();
		GridBagLayout gbl_row5 = (GridBagLayout) row5.getLayout();
		gbl_row5.rowWeights = new double[]{0.0};
		gbl_row5.rowHeights = new int[]{0};
		gbl_row5.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_row5.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		GridBagConstraints gbc_row5 = new GridBagConstraints();
		gbc_row5.insets = new Insets(0, 0, 5, 0);
		gbc_row5.fill = GridBagConstraints.BOTH;
		gbc_row5.gridx = 0;
		gbc_row5.gridy = 4;
		panel.add(row5, gbc_row5);
		
		row6 = new OneRow();
		GridBagLayout gbl_row6 = (GridBagLayout) row6.getLayout();
		gbl_row6.rowWeights = new double[]{0.0};
		gbl_row6.rowHeights = new int[]{0};
		gbl_row6.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_row6.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		GridBagConstraints gbc_row6 = new GridBagConstraints();
		gbc_row6.insets = new Insets(0, 0, 5, 0);
		gbc_row6.fill = GridBagConstraints.BOTH;
		gbc_row6.gridx = 0;
		gbc_row6.gridy = 5;
		panel.add(row6, gbc_row6);
		
		row7 = new OneRow();
		GridBagLayout gbl_row7 = (GridBagLayout) row7.getLayout();
		gbl_row7.rowWeights = new double[]{0.0};
		gbl_row7.rowHeights = new int[]{0};
		gbl_row7.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_row7.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		GridBagConstraints gbc_row7 = new GridBagConstraints();
		gbc_row7.insets = new Insets(0, 0, 5, 0);
		gbc_row7.fill = GridBagConstraints.BOTH;
		gbc_row7.gridx = 0;
		gbc_row7.gridy = 6;
		panel.add(row7, gbc_row7);
		
		row8 = new OneRow();
		GridBagLayout gbl_row8 = (GridBagLayout) row8.getLayout();
		gbl_row8.rowWeights = new double[]{0.0};
		gbl_row8.rowHeights = new int[]{0};
		gbl_row8.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_row8.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		GridBagConstraints gbc_row8 = new GridBagConstraints();
		gbc_row8.fill = GridBagConstraints.BOTH;
		gbc_row8.gridx = 0;
		gbc_row8.gridy = 7;
		panel.add(row8, gbc_row8);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Control", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		add(panel_1, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JButton cmdRead = new JButton("Read");
		cmdRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//getValue();
				getValueRotate();
			}
		});
		GridBagConstraints gbc_cmdRead = new GridBagConstraints();
		gbc_cmdRead.insets = new Insets(0, 0, 0, 5);
		gbc_cmdRead.anchor = GridBagConstraints.WEST;
		gbc_cmdRead.gridx = 0;
		gbc_cmdRead.gridy = 0;
		panel_1.add(cmdRead, gbc_cmdRead);
		
		JButton cmdClear = new JButton("Clear");
		cmdClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		GridBagConstraints gbc_cmdClear = new GridBagConstraints();
		gbc_cmdClear.gridx = 1;
		gbc_cmdClear.gridy = 0;
		panel_1.add(cmdClear, gbc_cmdClear);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_2, BorderLayout.NORTH);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		txtValue = new JTextField();
		GridBagConstraints gbc_txtValue = new GridBagConstraints();
		gbc_txtValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValue.gridx = 0;
		gbc_txtValue.gridy = 0;
		panel_2.add(txtValue, gbc_txtValue);
		txtValue.setColumns(10);
	}
	
	private void clear()
	{
		row1.clear();
		row2.clear();
		row3.clear();
		row4.clear();
		row5.clear();
		row6.clear();
		row7.clear();
		row8.clear();
	}
	
	private void getValue()
	{
		StringBuffer str = new StringBuffer();
		
		str.append(String.format("0x%x", row1.getValue())).append(",");
		str.append(String.format("0x%x", row2.getValue())).append(",");
		str.append(String.format("0x%x", row3.getValue())).append(",");
		str.append(String.format("0x%x", row4.getValue())).append(",");
		str.append(String.format("0x%x", row5.getValue())).append(",");
		str.append(String.format("0x%x", row6.getValue())).append(",");
		str.append(String.format("0x%x", row7.getValue())).append(",");
		str.append(String.format("0x%x", row8.getValue()));
		
		txtValue.setText(str.toString());
	}
	
	/**
	 * 
	 */
	private void getValueRotate()
	{
		StringBuffer str = new StringBuffer();
		List<boolean []> all = new ArrayList<boolean []>();
		
		all.add(row1.getFlag());
		all.add(row2.getFlag());
		all.add(row3.getFlag());
		all.add(row4.getFlag());
		all.add(row5.getFlag());
		all.add(row6.getFlag());
		all.add(row7.getFlag());
		all.add(row8.getFlag());

		for(int i = 0; i < 8; i++)
		{
			byte value = 0x00;

			for(int x = 7; x >= 0; x--) {
				if ( all.get(x)[i] ) {
					value = (byte)(value | (byte)(1 << x));
				}
			}

			str.append(String.format("0x%x", value));
			if ( i < 7 )
				str.append(",");
		}

		txtValue.setText(str.toString());
	}

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}catch(Exception ig) {}
		
		try {
			final DotMatrix ct = new DotMatrix();
			JFrame main = new JFrame("8x8");
			main.getContentPane().add(ct);
			main.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			main.pack();
			main.setSize(main.getSize().width+60, main.getSize().height);
			main.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}		
}
