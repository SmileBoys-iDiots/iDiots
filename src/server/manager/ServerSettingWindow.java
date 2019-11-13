package server.manager;
import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import l1j.server.Config;

@SuppressWarnings("serial")
public class ServerSettingWindow extends JInternalFrame {
	
	private JLabel lbl_MaxUser = null;
	private JTextField txt_MaxUser = null;
	private JLabel lbl_Exp = null;
	private JTextField txt_Exp = null;
		
	private	JLabel lbl_Item = null;
	private	JTextField txt_Item = null;
	private	JLabel lbl_Lawful = null;
	private	JTextField txt_Lawful = null;

	private	JLabel lbl_BalanceEnchant = null;
	private	JTextField txt_BalanceEnchant = null;
	private	JLabel lbl_ArmorEnchant = null;
	private	JTextField txt_ArmorEnchant = null;

	private	JLabel lbl_EarthEnchant = null;
	private	JTextField txt_EarthEnchant = null;
	private	JLabel lbl_FireEnchant = null;
	private	JTextField txt_FireEnchant = null;

	private	JLabel lbl_FeatherTime = null;
	private	JTextField txt_FeatherTime = null;
	private	JLabel lbl_FeatherClanNumber = null;
	private	JTextField txt_FeatherClanNumber = null;

	//
	public ServerSettingWindow() {
		super();
		
		initialize();
	}
	
	public void initialize() {
		title = "서버설정";
		closable = true;      
	    isMaximum = false;	
	    maximizable = false;
	    resizable = false;
        iconable = true;
	    isIcon = false;		
	    setSize(800, 410);
		setBounds(10, 10, 800, 410);
		setVisible(true);
		frameIcon = new ImageIcon("");
		setRootPaneCheckingEnabled(true);
	    updateUI();
	    
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
			
		//
		lbl_MaxUser = new JLabel("최대유저");
		txt_MaxUser = new JTextField();	
		txt_MaxUser.setForeground(Color.white);
		lbl_Exp = new JLabel("경험치");
		txt_Exp = new JTextField();
		txt_Exp.setForeground(Color.white);
		
		lbl_Item = new JLabel("아이템");
		txt_Item = new JTextField();	
		txt_Item.setForeground(Color.white);
		lbl_Lawful = new JLabel("라우풀");
		txt_Lawful = new JTextField();
		txt_Lawful.setForeground(Color.white);
		
		lbl_BalanceEnchant = new JLabel("밸런스인챈");
		txt_BalanceEnchant = new JTextField();	
		txt_BalanceEnchant.setForeground(Color.white);
		lbl_ArmorEnchant = new JLabel("아머인챈");
		txt_ArmorEnchant = new JTextField();
		txt_ArmorEnchant.setForeground(Color.white);
		
		lbl_EarthEnchant = new JLabel("땅속성인챈");
		txt_EarthEnchant = new JTextField();	
		txt_EarthEnchant.setForeground(Color.white);
		lbl_FireEnchant = new JLabel("불속성인챈");
		txt_FireEnchant = new JTextField();
		txt_FireEnchant.setForeground(Color.white);
		
		lbl_FeatherTime = new JLabel("깃털지급시간");
		txt_FeatherTime = new JTextField();	
		txt_FeatherTime.setForeground(Color.white);
		lbl_FeatherClanNumber = new JLabel("깃털혈맹지급수");
		txt_FeatherClanNumber = new JTextField();
		txt_FeatherClanNumber.setForeground(Color.white);
		
		txt_MaxUser.setText(Config.MAX_ONLINE_USERS + "");
		txt_Exp.setText(Config.RATE_XP + "");
		txt_Item.setText(Config.RATE_DROP_ITEMS + "");
		txt_Lawful.setText(Config.RATE_LAWFUL + "");
		//txt_BalanceEnchant.setText(Config.ENCHANT_CHANCE_BALANCE + "");
		txt_ArmorEnchant.setText(Config.ENCHANT_CHANCE_ARMOR + "");
		
		JButton btn_ok = new JButton("설정저장");
		btn_ok.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					Config.MAX_ONLINE_USERS = Short.parseShort(txt_MaxUser.getText());	
					Config.RATE_XP = Double.parseDouble(txt_Exp.getText());
					Config.RATE_DROP_ITEMS = Double.parseDouble(txt_Item.getText());
					Config.RATE_LAWFUL = Double.parseDouble(txt_Lawful.getText());
					//Config.ENCHANT_CHANCE_BALANCE = Integer.parseInt(txt_BalanceEnchant.getText());
					Config.ENCHANT_CHANCE_ARMOR = Integer.parseInt(txt_ArmorEnchant.getText());
				
					eva.jSystemLogWindow.append("최대유저 : " + Config.MAX_ONLINE_USERS + "\n", "White");
					eva.jSystemLogWindow.append("채팅레벨 : " + Config.GLOBAL_CHAT_LEVEL + "\n", "White");
					eva.jSystemLogWindow.append("경험치 : " + Config.RATE_XP + "\n", "White");
					eva.jSystemLogWindow.append("아데나 : " + Config.RATE_DROP_ADENA + "\n", "White");
					eva.jSystemLogWindow.append("아이템 : " + Config.RATE_DROP_ITEMS + "\n", "White");
					eva.jSystemLogWindow.append("우호도 : " + Config.RATE_KARMA + "\n", "White");
					eva.jSystemLogWindow.append("라우풀 : " + Config.RATE_LAWFUL + "\n", "White");
					eva.jSystemLogWindow.append("무게 : " + Config.RATE_WEIGHT_LIMIT + "\n", "White");
					//eva.jSystemLogWindow.append("밸런스인챈 : " + Config.ENCHANT_CHANCE_BALANCE + "\n", "White");
					eva.jSystemLogWindow.append("무기인챈 : " + Config.ENCHANT_CHANCE_WEAPON + "\n", "White");
					eva.jSystemLogWindow.append("아머인챈 : " + Config.ENCHANT_CHANCE_ARMOR + "\n", "White");
					eva.jSystemLogWindow.append("신규보호레벨 : " + Config.MAX_LEVEL  + "\n", "White");
					eva.jSystemLogWindow.append("귓속말레벨 : " + Config.WHISPER_CHAT_LEVEL  + "\n", "White");
					
					eva.jSystemLogWindow.append("정상적으로 배율이 변경되었습니다." + "\n", "White");
					JOptionPane.showMessageDialog(null, "정상적으로 배율이 변경되었습니다.", " Server Message", JOptionPane.INFORMATION_MESSAGE);
					setClosed(true);
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
		JButton btn_cancel = new JButton("닫기");
		btn_cancel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					setClosed(true);					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
		//Main
		GroupLayout.SequentialGroup main_horizontal_grp = layout.createSequentialGroup();
		
		GroupLayout.SequentialGroup horizontal_grp = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vertical_grp   = layout.createSequentialGroup();
		
		//Main
		GroupLayout.ParallelGroup main = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		// 레이블이 들어갈 열
		GroupLayout.ParallelGroup col1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		// 텍스트필드
		GroupLayout.ParallelGroup col2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		// 레이블이 들어갈 열
	
		main.addGroup(horizontal_grp);
		main_horizontal_grp.addGroup(main);
			
		layout.setHorizontalGroup(main_horizontal_grp);
		layout.setVerticalGroup(vertical_grp);
		
		
		col1.addComponent(lbl_MaxUser)
			.addComponent(lbl_Exp)
			.addComponent(lbl_Item)
			.addComponent(lbl_Lawful)
			.addComponent(lbl_BalanceEnchant)
			.addComponent(lbl_ArmorEnchant)
			.addComponent(lbl_EarthEnchant)
			.addComponent(lbl_FireEnchant)
			.addComponent(lbl_FeatherTime)
			.addComponent(lbl_FeatherClanNumber);
		
		col2.addComponent(txt_MaxUser)
			.addComponent(txt_Exp)
			.addComponent(txt_Item)
			.addComponent(txt_Lawful)
			.addComponent(txt_BalanceEnchant)
			.addComponent(txt_ArmorEnchant)
			.addComponent(txt_EarthEnchant)
			.addComponent(txt_FireEnchant)
			.addComponent(txt_FeatherTime)
			.addComponent(txt_FeatherClanNumber);
		
		horizontal_grp.addGap(10).addContainerGap().addGroup(col1).addContainerGap();
		horizontal_grp.addGap(10).addContainerGap().addGroup(col2).addContainerGap();
				
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_MaxUser).addComponent(txt_MaxUser));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Exp).addComponent(txt_Exp));				
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Item).addComponent(txt_Item));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Lawful).addComponent(txt_Lawful));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_BalanceEnchant).addComponent(txt_BalanceEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_ArmorEnchant).addComponent(txt_ArmorEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_EarthEnchant).addComponent(txt_EarthEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_FireEnchant).addComponent(txt_FireEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_FeatherTime).addComponent(txt_FeatherTime));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_FeatherClanNumber).addComponent(txt_FeatherClanNumber));
		
		main.addGroup(layout.createSequentialGroup().addGap(130, 130, 130).addComponent(btn_ok).addGap(10).addComponent(btn_cancel));	
		vertical_grp.addGap(15).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addGap(19, 19, 19).addComponent(btn_ok).addComponent(btn_cancel));

	}
}
