package com.tools;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;

import com.klab.svc.AppsPropertiy;
import com.tools.ItemPanel.KeyValue;
import com.tools.SttUtility.ModelEntry;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.filechooser.FileFilter;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * STT를 학습시키거나 학습 정보를 삭제한다.
 */
public class SttTraining extends JPanel {
	private ItemPanel itmModel;
	private ItemPanel itmCorpus;
	private ItemPanel itmWord;
	private JTextField txtUser;
	private JTextField txtPwd;
	
	private String selectCustomizationId;
	private String selectCorpus;
	private SttUtility sttUtil;
    private JFileChooser chooser = new JFileChooser();
	
	public SttTraining() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Credentials", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("Username");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		txtUser = new JTextField();
		txtUser.setText(AppsPropertiy.getInstance().getProperty("stt.watson.user"));
		
		GridBagConstraints gbc_txtUser = new GridBagConstraints();
		gbc_txtUser.insets = new Insets(5, 0, 5, 5);
		gbc_txtUser.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtUser.gridx = 1;
		gbc_txtUser.gridy = 0;
		panel.add(txtUser, gbc_txtUser);
		txtUser.setColumns(10);
		
		JButton cmdRead = new JButton("Read");
		cmdRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readLanguageModel();
			}
		});
		GridBagConstraints gbc_cmdRead = new GridBagConstraints();
		gbc_cmdRead.fill = GridBagConstraints.VERTICAL;
		gbc_cmdRead.gridheight = 2;
		gbc_cmdRead.insets = new Insets(5, 0, 5, 5);
		gbc_cmdRead.gridx = 2;
		gbc_cmdRead.gridy = 0;
		panel.add(cmdRead, gbc_cmdRead);
		
		JLabel lblNewLabel_1 = new JLabel("Password");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		txtPwd = new JTextField();
		txtPwd.setText(AppsPropertiy.getInstance().getProperty("stt.watson.passwd"));
		GridBagConstraints gbc_txtPwd = new GridBagConstraints();
		gbc_txtPwd.insets = new Insets(0, 0, 5, 5);
		gbc_txtPwd.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPwd.gridx = 1;
		gbc_txtPwd.gridy = 1;
		panel.add(txtPwd, gbc_txtPwd);
		txtPwd.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(1, 3, 5, 5));
		
		itmModel = new ItemPanel();
		itmModel.setBorder(new TitledBorder(null, "Language", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		itmModel.addMyEventListener(new ClickEventListener() {
			public void click(ClickEventObject obj)
			{
				if ( "add".equals(obj.getCommand()) ) {
					addLanguageModel();
				}
				else if ( "delete".equals(obj.getCommand()) ) {
					deleteLanguageModel();
				}
				else if ( "select".equals(obj.getCommand()) ) {
					selectCustomizationId = obj.getData().toString();
					readCorpusWord(selectCustomizationId);
				}
			}
		});
		panel_1.add(itmModel);
		
		itmCorpus = new ItemPanel();
		itmCorpus.setBorder(new TitledBorder(null, "Corpus", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		itmCorpus.addMyEventListener(new ClickEventListener() {
			public void click(ClickEventObject obj)
			{
				if ( "add".equals(obj.getCommand()) ) {
					addCorpus();
				}
				else if ( "delete".equals(obj.getCommand()) ) {
					deleteCorpus();
				}
				else if ( "select".equals(obj.getCommand()) ) {
					selectCorpus = obj.getData().toString();
				}
			}
		});
		GridBagLayout gbl_itmCorpus = (GridBagLayout) itmCorpus.getLayout();
		gbl_itmCorpus.rowWeights = new double[]{1.0, 0.0};
		gbl_itmCorpus.rowHeights = new int[]{0, 0};
		gbl_itmCorpus.columnWeights = new double[]{1.0};
		gbl_itmCorpus.columnWidths = new int[]{0};
		panel_1.add(itmCorpus);
		
		itmWord = new ItemPanel();
		itmWord.setBorder(new TitledBorder(null, "Word", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		itmWord.addMyEventListener(new ClickEventListener() {
			public void click(ClickEventObject obj) {
			}
		});
		GridBagLayout gbl_itmWord = (GridBagLayout) itmWord.getLayout();
		gbl_itmWord.rowWeights = new double[]{1.0, 0.0};
		gbl_itmWord.rowHeights = new int[]{0, 0};
		gbl_itmWord.columnWeights = new double[]{1.0};
		gbl_itmWord.columnWidths = new int[]{0};
		panel_1.add(itmWord);
	}
	
	/**
	 * 
	 */
	private void deleteCorpus()
	{
		if ( selectCustomizationId == null )
			return;
		
		KeyValue item = itmCorpus.getSelectedItem();
		if ( item != null ) {
			getSttUtility().deleteCorpus(selectCustomizationId, item.key);
			itmCorpus.deleteItem(item);
		}
	}
	
	/**
	 * 
	 */
	private void addCorpus()
	{
		if ( selectCustomizationId == null )
			return;
		
		String file = showOpenDialog(getRoot(this), null);
		if ( file != null && file.length() != 0 ) {
			String name = JOptionPane.showInputDialog("이름을 입력하세요");
			if ( name != null && name.length() > 0 ) {
				try {
					getSttUtility().addCorpus(selectCustomizationId, name, file);
					readCorpusWord(selectCustomizationId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void addLanguageModel()
	{
		String name = JOptionPane.showInputDialog("이름을 입력하세요");
		if ( name != null && name.length() > 0 ) {
			String custId = getSttUtility().createModel(name, name);
			itmModel.addItem(custId, name);
		}
	}
	
	/**
	 * 
	 */
	private void deleteLanguageModel()
	{
		KeyValue item = itmModel.getSelectedItem();
		if ( item != null ) {
			getSttUtility().deleteModel(item.key);
			itmModel.deleteItem(item);
		}
	}
	
	/**
	 * 
	 */
	private void readLanguageModel()
	{
		itmModel.clear();
		
		List<ModelEntry> list = getSttUtility().listModel();
		for(ModelEntry me : list) {
			itmModel.addItem(me.customizationId, me.modelName);
		}
	}
	
	/**
	 * @return
	 */
	private SttUtility getSttUtility()
	{
		if ( sttUtil == null ) {
			sttUtil = new SttUtility(txtUser.getText(), txtPwd.getText());
		}
		
		return sttUtil;
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
			final SttTraining ct = new SttTraining();
			JFrame main = new JFrame("STT");
			main.getContentPane().add(ct);
			main.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			main.pack();
			main.setSize(800, 500);
			main.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	

	/**
	 * @param custId
	 */
	private void readCorpusWord(String custId)
	{
		itmCorpus.clear();
		
		// Corpus
		List<String> list = sttUtil.listCorpora(custId);
		for(String s : list)
			itmCorpus.addItem(s, s);
	}
	
    /**
     * @param comp
     * @return
     */
    private javax.swing.JFrame getRoot(java.awt.Component comp)
    {
        javax.swing.JFrame frm = null;

        while(comp != null) {
            if(comp instanceof javax.swing.JFrame)  {
                frm = (javax.swing.JFrame)comp;
                break;
            }else{
                comp = comp.getParent();
            }
        }

        if( frm == null) frm = new javax.swing.JFrame();

        return frm;
    }
    
    /**
     * @param parent
     * @param ff
     * @return
     */
    private String showOpenDialog(Component parent, FileFilter ff)
    {
		chooser.setFileFilter(ff);

        if ( chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION )
            return null;

        return chooser.getSelectedFile().getPath();
    }
}
