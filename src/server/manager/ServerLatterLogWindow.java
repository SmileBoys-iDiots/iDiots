package server.manager;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;




import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;


@SuppressWarnings("serial")

public class ServerLatterLogWindow extends JInternalFrame {
	
	private JTable jJTable = null;
	
	private JScrollPane pScroll = null;
	
	private DefaultTableModel model = null;
	
	private JButton btn_Clear = null;

	private JButton btn_Eclips = null;
	
	private JButton btn_Sniper = null;
	
	private JButton btn_IE = null;
	
	private JButton btn_FireWall = null;
	
	private JButton btn_NateOn = null;
	
	private JButton btn_LogOpen = null;
	
	private JButton btn_Refresh = null;
	
	private JButton btn_Navi = null;
	
	private JButton btn_Note = null;
	
	private ServerLetterSendWindow jServerLetterSendWindow = null;
	

	public ServerLatterLogWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
		super();
		
		initialize(windowName, x, y, width, height, resizable, closable);
	}
	
	public void initialize(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
		this.title = windowName;
		this.closable = closable;      
		this.isMaximum = false;	   
		this.maximizable = true;
		this.resizable = resizable;
		this.iconable = true;   
		this.isIcon = false;			  
	    setSize(width, height);
		setBounds(x, y, width, height);
		setVisible(true);
		frameIcon = new ImageIcon("");
		setRootPaneCheckingEnabled(true);
		
	    updateUI();
	    
	    String[] modelColName = { "번호", "보낸이", "제목", "내용", "보낸날짜", "확인", "삭제" };
		
		model = new DefaultTableModel(modelColName, 0);
		
		jJTable = new JTable(model);
		
		
		jJTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		jJTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		jJTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		jJTable.getColumnModel().getColumn(3).setPreferredWidth(250);	
		jJTable.getColumnModel().getColumn(4).setPreferredWidth(60);
		jJTable.getColumnModel().getColumn(5).setPreferredWidth(30);
		jJTable.getColumnModel().getColumn(6).setPreferredWidth(50);
		
		jJTable.addMouseListener(new MouseListenner());
		
	    pScroll = new JScrollPane(jJTable);
	    
		pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pScroll.setAutoscrolls(true);
		
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from letter where receiver in ('메티스', '미소피아', '카시오페아') ORDER BY item_object_id DESC");
			rs = pstm.executeQuery();
			
			DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
			Object[] letter = new Object[6];
			while (rs.next()) {
				letter[0] = String.valueOf(rs.getInt("item_object_id"));
				letter[1] = rs.getString("sender");
				letter[2] = rs.getString("subject");
				letter[3] = rs.getString("content");
				letter[4] = rs.getString("date");
				letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
				tModel.addRow(letter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);			
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		
		
		JCheckBox checkBox = new JCheckBox();
		jJTable.getColumn("확인").setCellRenderer(new CheckRowCellEdior());
		jJTable.getColumn("확인").setCellEditor(new DefaultCellEditor(checkBox));
		jJTable.getColumn("삭제").setCellRenderer(new ButtonRowCellEdior());
		btn_Refresh = new JButton(new ImageIcon("img\\rfresh.gif" , "이클립스"));
		//btn_Refresh.setBorderPainted(false);
		//btn_Refresh.setContentAreaFilled(false);
		btn_Refresh.setToolTipText("수신된 편지 정보를 갱신합니다.");
		btn_Refresh.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				Connection con = null;
				PreparedStatement pstm = null;
				ResultSet rs = null;
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					pstm = con.prepareStatement("select * from letter where receiver in ('메티스', '미소피아','카시오페아') ORDER BY item_object_id DESC");
					rs = pstm.executeQuery();
					
					DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
					
					for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
						tModel.removeRow(i);					
					}					
					
					Object[] letter = new Object[6];
					while (rs.next()) {
						letter[0] = String.valueOf(rs.getInt("item_object_id"));
						letter[1] = rs.getString("sender");
						letter[2] = rs.getString("subject");
						letter[3] = rs.getString("content");
						letter[4] = rs.getString("date");
						letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
						tModel.addRow(letter);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					SQLUtil.close(rs);			
					SQLUtil.close(pstm);
					SQLUtil.close(con);
				} 
			}
	    });
		btn_Clear = new JButton(new ImageIcon("img\\save.gif" , "로그 폴더 열기"));
	    btn_Clear.setToolTipText("메니저창의 모든 로그를 저장하고 초기화합니다.");
	    btn_Clear.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					File f = null;
					String sTemp = "";
					synchronized (eva.lock) {
						sTemp = eva.getDate();
						StringTokenizer s = new StringTokenizer(sTemp, " ");
						eva.date = s.nextToken();
						eva.time = s.nextToken();
						f = new File("ServerLog/" + eva.date);
						if (!f.exists()) {
							f.mkdir();
						}
						eva.jSystemLogWindow.savelog();
						eva.jTradeLogWindow.savelog();
						eva.jWareHouseLogWindow.savelog();
						eva.jEnchantLogWindow.savelog();
						eva.jEventLogWindow.savelog();
						eva.jBossLogWindow.savelog();
						eva.jObserveLogWindow.savelog();
						eva.jAccountLogWindow.savelog();
						eva.jCommandLogWindow.savelog();
						eva.jRStatLogWindow.savelog();
						eva.jWhisperChatLogWindow.savelog();
						eva.jClanChatLogWindow.savelog();
						eva.jServerMultiChatLogWindow.savelog();
						sTemp = null;
						eva.date = null;
						eva.time = null;							
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
	    });
	//	ImageIcon icon = new ImageIcon("c:\\icon.git")
		  btn_LogOpen = new JButton(new ImageIcon("img\\log.gif" , "로그 폴더 열기"));
		//  btn_LogOpen.setBorderPainted(false);
		//  btn_LogOpen.setContentAreaFilled(false);
		  btn_LogOpen.setToolTipText("로그 폴더 열기");
		  btn_LogOpen.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent paramActionEvent) {
						btn_LogOpenActionPerformed(paramActionEvent);
				   }
			    });
		  btn_Eclips = new JButton(new ImageIcon("img\\icon3.gif" , "이클립스"));
		//  btn_Eclips.setBorderPainted(false);
		 // btn_Eclips.setContentAreaFilled(false);
		  btn_Eclips.setToolTipText("이클립스 실행");
		  btn_Eclips.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent paramActionEvent) {
						btn_EclipsActionPerformed(paramActionEvent);
				   }
			    });
		   btn_Sniper = new JButton(new ImageIcon("img\\sniper.gif" , "스니퍼"));
		  // btn_Sniper.setBorderPainted(false);
		 //  btn_Sniper.setContentAreaFilled(false);
		   btn_Sniper.setToolTipText("스마트 스니퍼 실행");
		   btn_Sniper.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent paramActionEvent) {
					btn_SniperActionPerformed(paramActionEvent);
			   }
		    });
		   btn_IE = new JButton(new ImageIcon("img\\expl.gif" , "익스플로러"));
		 //  btn_IE.setBorderPainted(false);
		  // btn_IE.setContentAreaFilled(false);
		   btn_IE.setToolTipText("인터넷 익스플로러 실행");
		   btn_IE.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent paramActionEvent) {
						btn_IEActionPerformed(paramActionEvent);
				   }
			    });
		  
		   btn_FireWall = new JButton(new ImageIcon("img\\firewall.gif" , "서버방어기"));
		  // btn_FireWall.setBorderPainted(false);
		//   btn_FireWall.setContentAreaFilled(false);
		   btn_FireWall.setToolTipText("서버방어기 실행");
		   btn_FireWall.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent paramActionEvent) {
						btn_FireWallActionPerformed(paramActionEvent);
				   }
			    });
		   btn_NateOn = new JButton(new ImageIcon("img\\nate.gif" , "네이트온"));
		//   btn_NateOn.setBorderPainted(false);
		 //  btn_NateOn.setContentAreaFilled(false);
		   btn_NateOn.setToolTipText("네이트온 실행");
		   btn_NateOn.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent paramActionEvent) {
						btn_NateOnActionPerformed(paramActionEvent);
				   }
			    });
		   btn_Navi = new JButton(new ImageIcon("img\\navi.gif" , "나비켓"));
			//   btn_NateOn.setBorderPainted(false);
			 //  btn_NateOn.setContentAreaFilled(false);
			   btn_Navi.setToolTipText("나비켓 실행");
			   btn_Navi.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent paramActionEvent) {
							btn_NaviActionPerformed(paramActionEvent);
					   }
				    });
			   btn_Note = new JButton(new ImageIcon("img\\note.gif" , "메모장"));
				//   btn_NateOn.setBorderPainted(false);
				 //  btn_NateOn.setContentAreaFilled(false);
				   btn_Note.setToolTipText("메모장 실행");
				   btn_Note.addActionListener(new ActionListener() {
						 public void actionPerformed(ActionEvent paramActionEvent) {
								btn_NoteActionPerformed(paramActionEvent);
						   }
					    });
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		GroupLayout.SequentialGroup main_horizontal_grp = layout.createSequentialGroup();
		
		GroupLayout.SequentialGroup horizontal_grp = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vertical_grp   = layout.createSequentialGroup();
		
		GroupLayout.ParallelGroup main = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col4 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col5 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col6 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col7 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col8 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col9 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col10 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col11 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col12 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col13 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col14 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col15 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
			
		
		main.addGroup(horizontal_grp);
		main_horizontal_grp.addGroup(main);	
		
		layout.setHorizontalGroup(main_horizontal_grp);
		layout.setVerticalGroup(vertical_grp);
		
		col1.addComponent(btn_Refresh, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE);
		col7.addComponent(btn_Clear, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col8.addComponent(btn_LogOpen, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col9.addComponent(btn_Navi, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col10.addComponent(btn_Note, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col11.addComponent(btn_Eclips, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col12.addComponent(btn_Sniper, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col13.addComponent(btn_IE, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col14.addComponent(btn_FireWall, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		col15.addComponent(btn_NateOn, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE);
		
		
		
		
		horizontal_grp.addGap(5).addGroup(col1).addGap(75);
		horizontal_grp.addGroup(col2).addGap(5);
		horizontal_grp.addGroup(col3).addGap(5);
		horizontal_grp.addGroup(col4).addGap(5);
		horizontal_grp.addGroup(col5).addGap(5);
		horizontal_grp.addGroup(col6).addGap(5);
		horizontal_grp.addGroup(col7).addGap(5);
		horizontal_grp.addGroup(col8).addGap(5);
		horizontal_grp.addGroup(col9).addGap(5);
		horizontal_grp.addGroup(col10).addGap(5);
		horizontal_grp.addGroup(col11).addGap(5);
		horizontal_grp.addGroup(col12).addGap(5);
		horizontal_grp.addGroup(col13).addGap(5);
		horizontal_grp.addGroup(col14).addGap(5);
		horizontal_grp.addGroup(col15).addGap(5);
		
		main.addGroup(layout.createSequentialGroup().addComponent(pScroll));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(btn_Refresh)
				.addComponent(btn_Clear)
				.addComponent(btn_LogOpen)
				.addComponent(btn_Note)
				.addComponent(btn_Navi)
				.addComponent(btn_Eclips)
					.addComponent(btn_IE)
					.addComponent(btn_Sniper)
					.addComponent(btn_FireWall)
					.addComponent(btn_NateOn)).addGap(5);	 
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(pScroll));	 
		
	}
	

	
	private class MouseListenner extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == 1) {
				int column = ((JTable)e.getSource()).getSelectedColumn();
				int row = ((JTable)e.getSource()).getSelectedRow();
				
				if (column < 5) {
					if (jServerLetterSendWindow != null && jServerLetterSendWindow.isClosed()) {
						jServerLetterSendWindow = null;
					}
					if (jServerLetterSendWindow == null) {
						jServerLetterSendWindow = new ServerLetterSendWindow("편지", 0, 0, eva.width - 190, eva.height + 110, true, true);						
						eva.jJDesktopPane.add(jServerLetterSendWindow, 0);
						jServerLetterSendWindow.txt_To.setText(String.valueOf(((JTable)e.getSource()).getValueAt(row, 1)));
						jServerLetterSendWindow.txt_ReciveMsg.setText(String.valueOf(((JTable)e.getSource()).getValueAt(row, 3)));
						jServerLetterSendWindow.txt_From.setText("미소피아");
						jServerLetterSendWindow.setLocation((eva.jJFrame.getContentPane().getSize().width / 2) - (jServerLetterSendWindow.getContentPane().getSize().width / 2), (eva.jJFrame.getContentPane().getSize().height / 2) - (jServerLetterSendWindow.getContentPane().getSize().height / 2));
					}
					if (((Boolean)((JTable)e.getSource()).getValueAt(row, 5)).booleanValue() == false) {					
						Connection con = null;
						PreparedStatement pstm = null;
						ResultSet rs = null;
						try {
							con = L1DatabaseFactory.getInstance().getConnection();
							pstm = con.prepareStatement("update letter set isCheck = ? where item_object_id = ?");
							pstm.setInt(1, 1);
							pstm.setInt(2, Integer.parseInt((String)((JTable)e.getSource()).getValueAt(row, 0)));
							pstm.execute();
							pstm = con.prepareStatement("select * from letter where receiver in ('메티스', '미소피아', '카시오페아') ORDER BY item_object_id DESC");
							rs = pstm.executeQuery();
							DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
							for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
								tModel.removeRow(i);					
							}					
							Object[] letter = new Object[6];
							while (rs.next()) {
								letter[0] = String.valueOf(rs.getInt("item_object_id"));
								letter[1] = rs.getString("sender");
								letter[2] = rs.getString("subject");
								letter[3] = rs.getString("content");
								letter[4] = rs.getString("date");
								letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
								tModel.addRow(letter);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {		
							SQLUtil.close(rs);			
							SQLUtil.close(pstm);
							SQLUtil.close(con);
						} 
					}
				} else if (column == 6) { 
					Connection con = null;
					PreparedStatement pstm = null;
					ResultSet rs = null;
					try {
						con = L1DatabaseFactory.getInstance().getConnection();
						pstm = con.prepareStatement("delete from letter where item_object_id = ?");
						pstm.setInt(1, Integer.parseInt((String)((JTable)e.getSource()).getValueAt(row, 0)));
						pstm.execute();
						pstm = con.prepareStatement("select * from letter where receiver in ('메티스', '미소피아', '카시오페아') ORDER BY item_object_id DESC");
						rs = pstm.executeQuery();
						DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
						for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
							tModel.removeRow(i);					
						}					
						Object[] letter = new Object[6];
						while (rs.next()) {
							letter[0] = String.valueOf(rs.getInt("item_object_id"));
							letter[1] = rs.getString("sender");
							letter[2] = rs.getString("subject");
							letter[3] = rs.getString("content");
							letter[4] = rs.getString("date");
							letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
							tModel.addRow(letter);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {		
						SQLUtil.close(rs);			
						SQLUtil.close(pstm);
						SQLUtil.close(con);
					}					
				}
			}
		}
	}
	
	private void btn_LogOpenActionPerformed(ActionEvent paramActionEvent) {
		new Thread() {
			public void run() {
				try {
					Runtime.getRuntime().exec("C:\\Users\\ysh\\Desktop\\초롱서버\\ServerLog\\");
					
				} catch (Exception localException) {
				}
			}
		}.start();
	}

	private void btn_SniperActionPerformed(ActionEvent paramActionEvent) {
			new Thread() {
				public void run() {
					try {
						Runtime.getRuntime().exec("C:\\Users\\ysh\\Desktop\\초롱서버\\smsniff\\smsniff.exe");
					} catch (Exception localException) {
					}
				}
			}.start();
	}

	private void btn_FireWallActionPerformed(ActionEvent paramActionEvent) {
			new Thread() {
				public void run() {
					try {
						Runtime.getRuntime().exec("C:\\Users\\ysh\\Desktop\\초롱서버\\SimpleFirewall\\SimpleFirewall.exe");
					} catch (Exception localException) {
					}
				}
			}.start();
	}
	
	private void btn_IEActionPerformed(ActionEvent paramActionEvent) {
		new Thread() {
			public void run() {
				try {
					Runtime.getRuntime().exec("C:\\Program Files (x86)\\Internet Explorer\\iexplore.exe");
				} catch (Exception localException) {
				}
			}
		}.start();
	}
	
	private void btn_NateOnActionPerformed(ActionEvent paramActionEvent) {
		new Thread() {
			public void run() {
				try {
					Runtime.getRuntime().exec("C:\\Program Files (x86)\\SK Communications\\NATEON\\BIN\\NateOn.exe");
				} catch (Exception localException) {
				}
			}
		}.start();
	}
	
	private void btn_EclipsActionPerformed(ActionEvent paramActionEvent) {
		new Thread() {
			public void run() {
				try {
					Runtime.getRuntime().exec("C:\\Users\\ysh\\AppData\\Local\\Eclipse Standard 4.3.0\\eclipse.exe");
				} catch (Exception localException) {
				}
			}
		}.start();
	}
	private void btn_NoteActionPerformed(ActionEvent paramActionEvent) {
		new Thread() {
			public void run() {
				try {
					Runtime.getRuntime().exec("C:\\Windows\\System32\\notepad.exe");
				} catch (Exception localException) {
				}
			}
		}.start();
	}
	private void btn_NaviActionPerformed(ActionEvent paramActionEvent) {
		new Thread() {
			public void run() {
				try {
					Runtime.getRuntime().exec("E:\\Navicat_8.0.18_MySQL_Kr\\navicat.exe");
				} catch (Exception localException) {
				}
			}
		}.start();
	}
	
	public void errorMessage(String paramString) {
		JOptionPane.showMessageDialog(this, paramString, "경고", 0);
	}
	
	private class CheckRowCellEdior extends JCheckBox implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
			setSelected(((Boolean)value).booleanValue());
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}	 

	private class ButtonRowCellEdior extends JButton implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
			setText("확인");
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}
}
