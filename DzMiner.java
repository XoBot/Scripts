import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;//
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import xobot.client.callback.listeners.MessageListener;
import xobot.client.callback.listeners.PaintListener;
import xobot.client.events.MessageEvent;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.Game;
import xobot.script.methods.GameObjects;
import xobot.script.methods.NPCs;
import xobot.script.methods.Players;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.Tile;
import xobot.script.wrappers.interactive.GameObject;
import xobot.script.wrappers.interactive.NPC;

@Manifest(authors = { "Neo" }, name = "DzMiner")
public class DzMiner extends ActiveScript implements PaintListener, MessageListener{

	public Timer t = null;
	
	int mined = 0;
	int oreid;
	int[] ids;
	String oretype = "Loading..";
	String status = "Loading...";
	
	public boolean onStart() {
		t = new Timer();
		JDialog frame = new JDialog();
		frame.setPreferredSize(new Dimension(250,90));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		FlowLayout layout = new FlowLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		frame.setLayout(layout);
		
		JComboBox<String> combo = new JComboBox<String>();
		combo.setPreferredSize(new Dimension(150,30));
		combo.setFocusable(false);
		combo.addItem("Tin");
		combo.addItem("Copper");
		combo.addItem("Iron");
		combo.addItem("Coal");
		combo.addItem("Mithril");
		combo.addItem("Adamant");
		combo.addItem("Rune");
		
		JButton button = new JButton("Start");
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(60,32));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				oretype = (String)combo.getSelectedItem();
				switch(oretype) {
				case "Tin":
					ids = new int[]{2090};
					oreid = 438;
					break;
				case "Copper":
					ids = new int[]{2094};
					oreid = 436;
					break;
				case "Iron":
					ids = new int[]{2092};
					oreid = 440;
					break;
				case "Coal":
					ids = new int[]{2096, 2097};
					oreid = 453;
					break;
				case "Mithril":
					ids = new int[]{2102};
					oreid = 447;
					break;
				case "Adamant":
					ids = new int[]{2105};
					oreid = 449;
					break;
				case "Rune":
					ids = new int[]{2107};
					oreid = 451;
					break;
				}
				frame.dispose();
			}
			
		});
		
		frame.add(combo);
		frame.add(button);
		frame.setTitle("DzMiner");

		frame.pack();
		frame.setVisible(true);
		while(frame.isVisible()) {
			Time.sleep(500);
		}
		return ids != null && ids.length > 0;
	}
	
	final Tile tt = new Tile(3034, 9737);
	
	@Override
	public int loop() {
		if(Inventory.isFull()) {
			NPC banker = NPCs.getNearest(494);
			if(banker != null) {
				status = "Banking..";
				if(Bank.isOpen()) {
					Bank.depositAll();
					Time.sleep(() -> Inventory.isEmpty(), 1500);
					return 150;
				}else {
					banker.interact("bank");
					Time.sleep(() -> Bank.isOpen(), 2500);
					return 150;
				}
				
			}else {
				status = "Teleporting home..";
				if(Game.teleport("Edgeville")) {
					Time.sleep(() -> GameObjects.getNearest(26972) != null, 6500);
				}
				return 150;
			}
		}else {
			if(Time.sleep(() -> Players.getMyPlayer().getAnimation() != -1, 2000)) {
				status = "Mining..";
				return 1000;
			}
			GameObject ore = GameObjects.getNearest(ids);
			if(ore != null) {
				this.status = "Mining..";
	        	ore.interact("mine");
	        	Time.sleep(() -> Players.getMyPlayer().getAnimation() != -1, 5000);
				return 150;
			}else {
				return -1; //No ore was found
			}
		}
	}
	
	
    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    private final Color color1 = new Color(0, 0, 0);

    private final Font font1 = new Font("Arial", 0, 17);
    private final Font font2 = new Font("Arial", 2, 17);

    private final Image img1 = getImage("http://i.imgur.com/wPwcFdz.png");


	
	@Override
	public void repaint(Graphics render) {
		int ph = (int) ((mined) * 3600000D / (t.getElapsed()));
		
        Graphics2D g = (Graphics2D)render;
        g.drawImage(img1, 1, 224, null);
        g.setFont(font1);
        g.setColor(color1);
        g.drawString("Time: " + t.toElapsedString(), 16, 272);
        g.drawString("Ores: " + mined, 16, 297);
        g.drawString("Ores(h) " + ph, 135, 298);
        g.drawString("Mining: " + oretype, 135, 271);
        g.setFont(font2);
        g.drawString("Status: " + status, 50, 320);

	}


	@Override
	public void MessageRecieved(MessageEvent event) {
		if(event.getType() == 19 && event.getMessage().toLowerCase().contains("you manage to mine some")) {
			mined++;
		}
	}

}
