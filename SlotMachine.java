
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import xobot.client.callback.listeners.MessageListener;
import xobot.client.callback.listeners.PaintListener;
import xobot.client.events.MessageEvent;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.GameObjects;
import xobot.script.methods.Packets;
import xobot.script.methods.Players;
import xobot.script.methods.Widgets;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.interactive.GameObject;
import xobot.script.wrappers.interactive.Item;

@Manifest(authors = { "Neo" }, name = "Slot Machine")
public class SlotMachine extends ActiveScript implements PaintListener, MessageListener{

	private Timer t;
	private int gold = 0;
	private int itemid = 0;
	public boolean onStart() {
		t = new Timer(System.currentTimeMillis());
		JDialog x = new JDialog();
		x.setTitle("Slot Machine");
		x.setPreferredSize(new Dimension(240,100));
		x.setLocationRelativeTo(null);
		x.setAlwaysOnTop(true);
		x.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JComboBox<String> como = new JComboBox<String>();
		for(Item i : Bank.getItems()) {
			como.addItem(i.getDefinition().getName() + " - " + i.getID());
		}
		como.setFocusable(false);
		como.setPreferredSize(new Dimension(150,100));
		x.add(como, BorderLayout.WEST);
		
		JButton button = new JButton("Start");
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				itemid = Integer.valueOf(como.getSelectedItem().toString().split(" - ")[1]);
				x.dispose();
			}
			
		});
		x.add(button, BorderLayout.EAST);
		
		
		x.pack();
		x.setVisible(true);
		
		while(x.isVisible()) {
			Time.sleep(1000);
		}
		return itemid != 0;
		
	}
	
	@Override
	public int loop() {
		if(Inventory.Contains(itemid) && !Inventory.isFull()) {
			GameObject o = GameObjects.getNearest(20040);
			if(o != null) {
				if(Widgets.getBackDialogId() == 2459) {
					Packets.sendAction(315, 0, 0, 2461, 1);
					return 250;
				}
				Item i = Inventory.getItem(itemid);
				if(i != null) {
					i.interact("use");
				}
				Time.sleep(1000);
				o.interact("use-with");
				Time.sleep(() -> Players.getMyPlayer().getAnimation() != -1, 3500);
				Time.sleep(() -> Players.getMyPlayer().getAnimation() == -1, 3500);
				return 150;
			}
		}else {
			
			if(Bank.isOpen()) {
				if(Inventory.Contains(995) || Inventory.isFull()) {
					Bank.depositAll();
					Time.sleep(150);
				}
				
				Item i = Bank.getItem(itemid);
				if(i != null) {
					Bank.withdraw(itemid, 27);
					Time.sleep(50);
				}else {
					return -1;
				}
			}else {
				GameObject o = GameObjects.getNearest(26972);
				if(o != null) {
					o.interact("use-q");
					Time.sleep(1200);
				}
			}

		}
		return 1000;
	}
	
    private final Color color1 = new Color(255, 255, 255, 84);
    private final Color color2 = new Color(0, 0, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 23);
    private final Font font2 = new Font("Arial", 0, 16);

	
	@Override
	public void repaint(Graphics g1) {
		if(itemid == 0) return;
		int ph = (int) ((gold) * 3600000D / (t.getElapsed()) / 1000000);
		int item = Bank.getItem(itemid) != null ? Bank.getItem(itemid).getStack() : 0;
        Graphics2D g = (Graphics2D)g1;
        g.setColor(color1);
        g.fillRect(343, 155, 171, 183);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRect(343, 155, 171, 183);
        g.setFont(font1);
        g.drawString("Slot Machine", 367, 184);
        g.setFont(font2);
        g.drawString("Time: " + t.toElapsedString(), 352, 219);
        g.drawString("Profit: " + (gold / 1000000) + "M", 352, 249);
        g.drawString("Items: " + item, 352, 305);
        g.drawString("Profit(h): " + ph + "M", 352, 277);
        g.drawString("Neo", 481, 334);
	}

	@Override
	public void MessageRecieved(MessageEvent e) {
		if(e.getType() == 0 && e.getMessage().toLowerCase().contains("congratulations! you won")) {
			gold+=500000;
		}
	}
	
}
