package server.manager;
import java.awt.Color;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


@SuppressWarnings("serial")
public class ServerEventLogWindow extends JInternalFrame {
	private JTextPane textPane = null;
	private JScrollPane pScroll = null;

	public ServerEventLogWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
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
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
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
						eva.flush(textPane, "[" + eva.time + "] " + title + " Window", eva.date);
						sTemp = null;
						eva.date = null;
						eva.time = null;							
					}
					
					textPane.setText("");
				} catch (Exception ex) {
					// TODO: handle exception
				}
			}
		});
		
	    updateUI();
	    
	    textPane = new JTextPane();
	    pScroll = new JScrollPane(textPane);
	    textPane.setEditable(false);		
		pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pScroll.setAutoscrolls(true);
		add(pScroll);
		

		
		Style style = null;
		style = textPane.addStyle("Black", null);
		StyleConstants.setForeground(style, Color.black);
		style = textPane.addStyle("Red", null);
		StyleConstants.setForeground(style, Color.red);
		style = textPane.addStyle("Orange", null);
		StyleConstants.setForeground(style, Color.orange);
		style = textPane.addStyle("Yellow", null);
		StyleConstants.setForeground(style, Color.yellow);
		style = textPane.addStyle("Green", null);
		StyleConstants.setForeground(style, Color.green);
		style = textPane.addStyle("Blue", null);
		StyleConstants.setForeground(style, Color.blue);
		style = textPane.addStyle("DarkGray", null);
		StyleConstants.setForeground(style, Color.darkGray);
		style = textPane.addStyle("Pink", null);
		StyleConstants.setForeground(style, Color.pink);
		style = textPane.addStyle("Cyan", null);
		StyleConstants.setForeground(style, Color.cyan);
		style = textPane.addStyle("Purple", null);
		StyleConstants.setForeground(style, Color.magenta);
	}
	
	public void append(String msg, String color) {
		StyledDocument doc = textPane.getStyledDocument();	

		try {
		    doc.insertString(doc.getLength(), msg, textPane.getStyle(color));		    		    
		    //pScroll.getVerticalScrollBar().setValue(pScroll.getVerticalScrollBar().getMaximum());
		   // textPane.setCaretPosition(textPane.getDocument().getLength());이벤트로그오류
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void savelog() {
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
				eva.flush(textPane, "[" + eva.time + "] 0.던전모니터", eva.date);
				sTemp = null;
				eva.date = null;
				eva.time = null;							
			}
			
			textPane.setText("");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}