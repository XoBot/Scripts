import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.Game;
import xobot.script.methods.GameObjects;
import xobot.script.methods.Players;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.Tile;
import xobot.script.wrappers.interactive.GameObject;

@Manifest(authors = { "Neo" }, name = "NeoMiner")
public class NeoMiner extends ActiveScript implements PaintListener{

	public Timer t = null;
	
	int mined = 0;
	int id1 = 0;
	int id2 = 0;
	String oretype = "Loading..";
	String status = "Loading...";
	
	public boolean onStart() {
		t = new Timer(System.currentTimeMillis());
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
		
		JButton button = new JButton("Start");
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(60,32));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				oretype = (String)combo.getSelectedItem();
				switch(oretype) {
				case "Tin":
					id1 = 21293;
					id2 = 21294;
					break;
				case "Copper":
					id1 = 21284;
					id2 = 21285;
					break;
				case "Iron":
					id1 = 21281;
					id2 = 21282;
					break;
				case "Coal":
					id1 = 21288;
					id2 = 21287;
					break;
				case "Mithril":
					id1 = 21280;
					id2 = 21278;
					break;
				case "Adamant":
					id1 = 21277;
					id2 = 21276;
					break;
				}
				frame.dispose();
			}
			
		});
		
		frame.add(combo);
		frame.add(button);
		frame.setTitle("XoBot - NeoMiner");

		frame.pack();
		frame.setVisible(true);
		while(frame.isVisible()) {
			Time.sleep(500);
		}
		return id1 != 0 && id2 != 0;
	}
	
	final Tile tt = new Tile(3034, 9737);
	
	@Override
	public int loop() {

		GameObject o = GameObjects.getNearest(26972);
		if(Inventory.isFull()) {
			if(o != null) {
				status = "Banking..";
				if(Bank.isOpen()) {
					Bank.depositAll();
					mined+=28;
					Time.sleep(() -> Inventory.isEmpty(), 1500);
					return 150;
				}else {
					o.interact("use-q");
					Time.sleep(() -> Players.getMyPlayer().getAnimation() != -1, 5000);
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
			if(Players.getMyPlayer().getAnimation() != -1) {
				status = "Mining..";
				return 1000;
			}
			GameObject ore = GameObjects.getNearest(id1,id2);
			GameObject bridge = GameObjects.getNearest(21310);
			if(ore != null) {
				if (Players.getMyPlayer().getLocation().getY() > 3846) {
					this.status = "Mining..";
		        	ore.interact("mine");
		        	Time.sleep(() -> Players.getMyPlayer().getAnimation() != -1, 5000);
					return 150;
				}
				if(bridge != null) {
					this.status = "Crossing bridge..";
				      bridge.interact("cross");
				      Time.sleep(() -> Players.getMyPlayer().getLocation().getY() > 3846, 9000);
				      return 150;
				}
			}else {
				if(o != null) {
					status = "Teleporting..";
					if(Game.teleport("neitiznot mine")) {
						Time.sleep(() -> GameObjects.getNearest(21310) != null, 6500);
					}
					return 150;
				}
			}
		}
		
		return 1000;
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

}
