
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

import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.Game;
import xobot.script.methods.GameObjects;
import xobot.script.methods.Players;
import xobot.script.methods.Walking;
import xobot.script.methods.input.KeyBoard;
import xobot.script.methods.tabs.Inventory;
import xobot.script.methods.tabs.Skills;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.interactive.GameObject;
import xobot.script.wrappers.interactive.Item;

@Manifest(authors = { "Neo" }, name = "NeoPrayer")
public class NeoPrayer extends ActiveScript implements PaintListener{

	private Timer t;
	private int startxp = 0;
	private int boneid = 536; //536
	private long lastchange = 0;
	public boolean onStart() {
		t = new Timer(System.currentTimeMillis());
		startxp = Skills.PRAYER.getCurrentExp();
		JDialog x = new JDialog();
		x.setTitle("Slot Machine");
		x.setPreferredSize(new Dimension(240,100));
		x.setLocationRelativeTo(null);
		x.setAlwaysOnTop(true);
		x.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JComboBox<String> como = new JComboBox<String>();
		
		como.addItem("Bone - 526");
		como.addItem("BD bones - 534");
		como.addItem("Dragon bones - 536");
		como.addItem("FD bones - 18830");
		
		como.setFocusable(false);
		como.setPreferredSize(new Dimension(150,100));
		x.add(como, BorderLayout.WEST);
		
		JButton button = new JButton("Start");
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boneid = Integer.valueOf(como.getSelectedItem().toString().split(" - ")[1]);
				x.dispose();
			}
			
		});
		x.add(button, BorderLayout.EAST);
		
		
		x.pack();
		x.setVisible(true);
		
		while(x.isVisible()) {
			Time.sleep(1000);
		}
		return boneid != 0;
		
	}
	
	@Override
	public int loop() {
		try {
			Item i = Inventory.getItem(boneid);
			GameObject altar = GameObjects.getNearest(409);
			
			if(System.currentTimeMillis() - lastchange < 6000 && Inventory.Contains(boneid)) {
				return 1500;
			}
			
			if(i != null) {
				if(altar != null) {
					if(Game.getInputState() == 1) {
						KeyBoard.typeWord("28",0, true);
						return 5000;
					}else {
						Walking.walkTo(Players.getMyPlayer().getLocation());
						i.interact("use");
						Time.sleep(250);
						altar.interact("use-with");
						return 2000;
					}
				}
			}else {
				if(Bank.isOpen()) {
					if(Bank.getItem(boneid) == null) {
						return -1;
					}
					if(!Inventory.isEmpty()) {
						Bank.depositAll();
					}
					Bank.withdraw(boneid, 28);
					return 1000;
				}else {
					GameObject bank = GameObjects.getNearest(26972);
					if(bank != null) {
						bank.interact("use-q");
						return 1000;
					}
				}
			}
		}catch(Exception e) {}
		return 1000;
	}
	
    private final Color color1 = new Color(255, 255, 255, 84);
    private final Color color2 = new Color(0, 0, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 23);
    private final Font font2 = new Font("Arial", 0, 16);

    int prevxp = 0;
	@Override
	public void repaint(Graphics g1) {
		int xp = Skills.PRAYER.getCurrentExp() - startxp;
		if(prevxp != xp) {
			lastchange = System.currentTimeMillis();
			prevxp = xp;
		}
		int ph = (int) ((xp) * 3600000D / (t.getElapsed()));
		Item i = Bank.getItem(boneid);
		int bones = 0;
		if(i != null) {
			bones = i.getStack();
		}
		
        Graphics2D g = (Graphics2D)g1;
        g.setColor(color1);
        g.fillRect(343, 155, 171, 183);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRect(343, 155, 171, 183);
        g.setFont(font1);
        g.drawString("NeoPrayer", 367, 184);
        g.setFont(font2);
        g.drawString("Time: " + t.toElapsedString(), 352, 219);
        g.drawString("XP: " + xp, 352, 249);
        g.drawString("XP(h): " + ph, 352, 305);
        g.drawString("Bones: " + bones, 352, 277);
        g.drawString("Neo", 481, 334);
	}
	
}
