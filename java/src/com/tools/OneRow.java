package com.tools;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Insets;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class OneRow extends JPanel
{
	private boolean [] flagDot = {false,false,false,false,false,false,false,false};
	
	public OneRow() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel led1 = new JLabel("");
		led1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 0);
			}
		});
		led1.setOpaque(true);
		led1.setBackground(Color.WHITE);
		led1.setMaximumSize(new Dimension(16, 16));
		led1.setMinimumSize(new Dimension(16, 16));
		led1.setPreferredSize(new Dimension(16, 16));
		GridBagConstraints gbc_led1 = new GridBagConstraints();
		gbc_led1.insets = new Insets(0, 0, 0, 5);
		gbc_led1.gridx = 0;
		gbc_led1.gridy = 0;
		add(led1, gbc_led1);
		
		JLabel led2 = new JLabel("");
		led2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 1);
			}
		});
		led2.setPreferredSize(new Dimension(16, 16));
		led2.setOpaque(true);
		led2.setMinimumSize(new Dimension(16, 16));
		led2.setMaximumSize(new Dimension(16, 16));
		led2.setBackground(Color.WHITE);
		GridBagConstraints gbc_led2 = new GridBagConstraints();
		gbc_led2.insets = new Insets(0, 0, 0, 5);
		gbc_led2.gridx = 1;
		gbc_led2.gridy = 0;
		add(led2, gbc_led2);
		
		JLabel led3 = new JLabel("");
		led3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 2);
			}
		});
		led3.setPreferredSize(new Dimension(16, 16));
		led3.setOpaque(true);
		led3.setMinimumSize(new Dimension(16, 16));
		led3.setMaximumSize(new Dimension(16, 16));
		led3.setBackground(Color.WHITE);
		GridBagConstraints gbc_led3 = new GridBagConstraints();
		gbc_led3.insets = new Insets(0, 0, 0, 5);
		gbc_led3.gridx = 2;
		gbc_led3.gridy = 0;
		add(led3, gbc_led3);
		
		JLabel led4 = new JLabel("");
		led4.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 3);
			}
		});
		led4.setPreferredSize(new Dimension(16, 16));
		led4.setOpaque(true);
		led4.setMinimumSize(new Dimension(16, 16));
		led4.setMaximumSize(new Dimension(16, 16));
		led4.setBackground(Color.WHITE);
		GridBagConstraints gbc_led4 = new GridBagConstraints();
		gbc_led4.insets = new Insets(0, 0, 0, 5);
		gbc_led4.gridx = 3;
		gbc_led4.gridy = 0;
		add(led4, gbc_led4);
		
		JLabel led5 = new JLabel("");
		led5.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 4);
			}
		});
		led5.setPreferredSize(new Dimension(16, 16));
		led5.setOpaque(true);
		led5.setMinimumSize(new Dimension(16, 16));
		led5.setMaximumSize(new Dimension(16, 16));
		led5.setBackground(Color.WHITE);
		GridBagConstraints gbc_led5 = new GridBagConstraints();
		gbc_led5.insets = new Insets(0, 0, 0, 5);
		gbc_led5.gridx = 4;
		gbc_led5.gridy = 0;
		add(led5, gbc_led5);
		
		JLabel led6 = new JLabel("");
		led6.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 5);
			}
		});
		led6.setPreferredSize(new Dimension(16, 16));
		led6.setOpaque(true);
		led6.setMinimumSize(new Dimension(16, 16));
		led6.setMaximumSize(new Dimension(16, 16));
		led6.setBackground(Color.WHITE);
		GridBagConstraints gbc_led6 = new GridBagConstraints();
		gbc_led6.insets = new Insets(0, 0, 0, 5);
		gbc_led6.gridx = 5;
		gbc_led6.gridy = 0;
		add(led6, gbc_led6);
		
		JLabel led7 = new JLabel("");
		led7.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 6);
			}
		});
		led7.setPreferredSize(new Dimension(16, 16));
		led7.setOpaque(true);
		led7.setMinimumSize(new Dimension(16, 16));
		led7.setMaximumSize(new Dimension(16, 16));
		led7.setBackground(Color.WHITE);
		GridBagConstraints gbc_led7 = new GridBagConstraints();
		gbc_led7.insets = new Insets(0, 0, 0, 5);
		gbc_led7.gridx = 6;
		gbc_led7.gridy = 0;
		add(led7, gbc_led7);
		
		JLabel led8 = new JLabel("");
		led8.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle((JLabel)e.getSource(), 7);
			}
		});
		led8.setPreferredSize(new Dimension(16, 16));
		led8.setOpaque(true);
		led8.setMinimumSize(new Dimension(16, 16));
		led8.setMaximumSize(new Dimension(16, 16));
		led8.setBackground(Color.WHITE);
		GridBagConstraints gbc_led8 = new GridBagConstraints();
		gbc_led8.gridx = 7;
		gbc_led8.gridy = 0;
		add(led8, gbc_led8);
	}

	/**
	 * @param src
	 * @param idx
	 */
	private void toggle(JLabel src, int idx)
	{
		if ( flagDot[idx] ) {
			flagDot[idx] = false;
			src.setBackground(Color.WHITE);
		}
		else {
			flagDot[idx] = true;
			src.setBackground(Color.RED);
		}
	}
	
	public void clear()
	{
		for(int i = 0; i < getComponentCount(); i++)
		{
			Component c = getComponent(i);
			if ( c instanceof JLabel ) {
				((JLabel)c).setBackground(Color.WHITE);
			}
		}
		
		for(int i = 0; i < flagDot.length; i++)
			flagDot[i] = false;
	}
	
	/**
	 * @return
	 */
	public byte getValue()
	{
		byte value = 0x00;

		for(int i = 0; i < flagDot.length; i++) {
			if ( flagDot[flagDot.length-i-1] ) {
				value = (byte)(value | (byte)(1 << i));
			}
		}
		
		return value;
	}
	
	/**
	 * @return
	 */
	public boolean [] getFlag()
	{
		return flagDot;
	}
}
