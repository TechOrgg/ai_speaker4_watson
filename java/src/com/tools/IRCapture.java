package com.tools;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.UIManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.klab.svc.AppsPropertiy;
import com.utils.SqlSessionManager;
import com.utils.SwingWorker;

import javax.swing.JTextArea;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JComboBox;
import java.awt.Dimension;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 리모컨 데이터를 캡처하고 저장하는 UI 기능을 제공한다.
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IRCapture extends JPanel implements MqttCallback
{
	private JTextField txtType;
	private JTextField txtValue;
	private JTextField txtLength;
	private JTextArea taRaw;
	private JLabel lbMessage;
	private MqttClient mqttClient;
	private JsonParser jp = new JsonParser();
	private JComboBox cobKey;
	private DefaultComboBoxModel<String> cobModel;
	private JButton cmdSave;
	private JButton cmdCopy;
	private JButton btnNewButton;
	
	public IRCapture() 
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "IR Data", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblType = new JLabel("Type");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.anchor = GridBagConstraints.EAST;
		gbc_lblType.insets = new Insets(5, 5, 5, 5);
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 0;
		panel_1.add(lblType, gbc_lblType);
		
		txtType = new JTextField();
		GridBagConstraints gbc_txtType = new GridBagConstraints();
		gbc_txtType.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtType.insets = new Insets(5, 0, 5, 5);
		gbc_txtType.gridx = 1;
		gbc_txtType.gridy = 0;
		panel_1.add(txtType, gbc_txtType);
		txtType.setBackground(Color.WHITE);
		txtType.setEditable(false);
		txtType.setColumns(40);
		
		JLabel lblNewLabel = new JLabel("Value");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);
		
		txtValue = new JTextField();
		GridBagConstraints gbc_txtValue = new GridBagConstraints();
		gbc_txtValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValue.insets = new Insets(0, 0, 5, 5);
		gbc_txtValue.gridx = 1;
		gbc_txtValue.gridy = 1;
		panel_1.add(txtValue, gbc_txtValue);
		txtValue.setBackground(Color.WHITE);
		txtValue.setEditable(false);
		txtValue.setColumns(40);
		
		JLabel lblNewLabel_1 = new JLabel("Length");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		panel_1.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		txtLength = new JTextField();
		GridBagConstraints gbc_txtLength = new GridBagConstraints();
		gbc_txtLength.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLength.insets = new Insets(0, 0, 5, 5);
		gbc_txtLength.gridx = 1;
		gbc_txtLength.gridy = 2;
		panel_1.add(txtLength, gbc_txtLength);
		txtLength.setBackground(Color.WHITE);
		txtLength.setEditable(false);
		txtLength.setColumns(40);
		
		JLabel lblNewLabel_2 = new JLabel("Raw");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 3;
		panel_1.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		taRaw = new JTextArea();
		taRaw.setMinimumSize(new Dimension(4, 64));
		taRaw.setPreferredSize(new Dimension(4, 64));
		GridBagConstraints gbc_taRaw = new GridBagConstraints();
		gbc_taRaw.fill = GridBagConstraints.HORIZONTAL;
		gbc_taRaw.insets = new Insets(0, 0, 5, 5);
		gbc_taRaw.gridx = 1;
		gbc_taRaw.gridy = 3;
		panel_1.add(taRaw, gbc_taRaw);
		taRaw.setColumns(40);
		taRaw.setEditable(false);
		taRaw.setRows(4);
		taRaw.setLineWrap(true);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Database", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel_3 = new JLabel("Key");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(5, 5, 0, 5);
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 0;
		panel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		cobModel = new DefaultComboBoxModel<String>();
		cobKey = new JComboBox(cobModel);
		GridBagConstraints gbc_cobKey = new GridBagConstraints();
		gbc_cobKey.insets = new Insets(5, 0, 0, 5);
		gbc_cobKey.fill = GridBagConstraints.HORIZONTAL;
		gbc_cobKey.gridx = 1;
		gbc_cobKey.gridy = 0;
		panel.add(cobKey, gbc_cobKey);
		
		cmdSave = new JButton("Save");
		cmdSave.setEnabled(false);
		cmdSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingWorker work = new SwingWorker() {
					@Override
					public Object construct() {
						cmdSave.setEnabled(false);
						_save();
						cmdSave.setEnabled(true);
						return null;
					}
				};
				work.start();
			}
		});
		GridBagConstraints gbc_cmdSave = new GridBagConstraints();
		gbc_cmdSave.insets = new Insets(5, 0, 0, 5);
		gbc_cmdSave.gridx = 2;
		gbc_cmdSave.gridy = 0;
		panel.add(cmdSave, gbc_cmdSave);
		
		cmdCopy = new JButton("Copy");
		cmdCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				_copy();
			}
		});
		
		btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				_sendIR();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(5, 0, 0, 0);
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 0;
		panel.add(btnNewButton, gbc_btnNewButton);
		GridBagConstraints gbc_cmdCopy = new GridBagConstraints();
		gbc_cmdCopy.insets = new Insets(5, 5, 0, 0);
		gbc_cmdCopy.gridx = 4;
		gbc_cmdCopy.gridy = 0;
		panel.add(cmdCopy, gbc_cmdCopy);
		
		lbMessage = new JLabel("Ready");
		GridBagConstraints gbc_lbMessage = new GridBagConstraints();
		gbc_lbMessage.anchor = GridBagConstraints.WEST;
		gbc_lbMessage.insets = new Insets(0, 5, 0, 0);
		gbc_lbMessage.gridx = 0;
		gbc_lbMessage.gridy = 2;
		add(lbMessage, gbc_lbMessage);
		
		SwingWorker work = new SwingWorker() {
			@Override
			public Object construct() {
				try {
					_loadKey();
					connect();
					lbMessage.setText("Connected.");
					cmdSave.setEnabled(true);
				} catch (Exception e) {
					lbMessage.setText(e.getMessage());
				}
				return null;
			}
		};
		
		work.start();
	}
	
	/**
	 * @throws Exception
	 */
	private void _loadKey() throws Exception
	{
		List<Map> irKey = SqlSessionManager.getSqlMapClient().queryForList("MYHOME.selectIrKeyList");
		for(Map m : irKey)
			cobModel.addElement(m.get("rcntrKey").toString());
	}
	
	/**
	 * 
	 */
	private void connect() throws Exception
	{
		AppsPropertiy conf = AppsPropertiy.getInstance();
		String broker = conf.getProperty("mqtt.server") + ":" + conf.getProperty("mqtt.port");
		MemoryPersistence persistence = new MemoryPersistence();

		mqttClient = new MqttClient(broker, "IR_CAPTURE", persistence);
		mqttClient.setCallback(this);

		MqttConnectOptions connOpts = new MqttConnectOptions();
	
		connOpts.setCleanSession(true);
		connOpts.setUserName(conf.getProperty("mqtt.id"));
		connOpts.setPassword(conf.getProperty("mqtt.pwd").toCharArray());
		
		mqttClient.connect(connOpts);
		mqttClient.subscribe("home/ir/evt/status");
	}
	
	/**
	 * 클립보드로 복사
	 */
	private void _copy()
	{
		StringBuffer str = new StringBuffer();
		
		str.append(txtType.getText()).append("\t");
		str.append(txtValue.getText()).append("\t");
		str.append(txtLength.getText()).append("\t");
		str.append(taRaw.getText());
		
        StringSelection stsel = new StringSelection(str.toString());
        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
        system.setContents(stsel, stsel);
        JOptionPane.showMessageDialog(this, "Copied", "choies@kr.ibm.com", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * 
	 */
	private void _sendIR()
	{
		String len = txtLength.getText();
		String rawText = taRaw.getText();
		
		if ( rawText.length() == 0 ) {
			return;
		}
		
		JsonObject payload = new JsonObject();
		JsonArray cmd = new JsonArray();
		payload.add("cmd", cmd);
		
		JsonObject raw = new JsonObject();
		raw.addProperty("rawLen", Integer.parseInt(len));
		raw.addProperty("rawData", rawText);
		cmd.add(raw);
		
		MqttMessage mm = new MqttMessage();
		mm.setQos(2);
		mm.setPayload(payload.toString().getBytes());
		try {
			mqttClient.publish("home/ir/cmd/control", mm);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "choies@kr.ibm.com", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 
	 */
	private void _save()
	{
		String type = txtType.getText();
		String value = txtValue.getText();
		String len = txtLength.getText();
		String raw = taRaw.getText();
		
		if ( raw.length() == 0 ) {
			JOptionPane.showMessageDialog(this, "NO DATA", "choies@kr.ibm.com", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String key = cobKey.getSelectedItem().toString();
		
		try
		{
			Map parm = new HashMap();
			
			parm.put("rcntrType", type);
			parm.put("rcntrVal", value);
			parm.put("rawDataLen", len);
			parm.put("rawData", raw);
			parm.put("rcntrKey", key);
			
			if (SqlSessionManager.getSqlMapClient().update("MYHOME.updateIrKey", parm) == 1 ) {
				JOptionPane.showMessageDialog(this, "Saved", "choies@kr.ibm.com", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(this, "NOT FOUND", "choies@kr.ibm.com", JOptionPane.INFORMATION_MESSAGE);
			}
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "choies@kr.ibm.com", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		cause.printStackTrace();
		lbMessage.setText(cause.getMessage());
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception
	{
		String payload = new String(message.getPayload());
		System.out.println("[RECV: " + topic + "] " + payload);
		JsonObject raw = jp.parse(payload).getAsJsonObject();
		
		JsonElement je = raw.get("raw");
		if ( je != null ) {
			String rawStr = je.getAsString();
			String [] data = rawStr.split("\\|");
			if ( data.length == 4 && data[2].length() >= 2 ) {
				txtType.setText(data[0]);
				txtValue.setText(data[1]);
				txtLength.setText(data[2]);
				taRaw.setText(data[3]);
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
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
			final IRCapture ct = new IRCapture();
			JFrame main = new JFrame("IR Capture");
			main.getContentPane().add(ct);
			main.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					{
			            System.exit(0);
					}
				}
			});

			main.pack();
			main.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
}
