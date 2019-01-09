import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import xobot.script.methods.GameObjects;
import xobot.script.methods.Packets;
import xobot.script.methods.Widgets;
import xobot.script.methods.tabs.Skills;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.interactive.GameObject;

@Manifest(authors = { "Neo" }, name = "Guard Maker")
public class GuardMaker extends ActiveScript implements PaintListener{

	private final int[] guardIds = {13366,13367,13368,13372};
	private int interfaceId = -1;
	private int startXP;
	
	private Timer timer;
	
	@Override
	public boolean onStart() {
		startXP = Skills.CONSTRUCTION.getCurrentExp();
		timer = new Timer();
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
		combo.addItem("Skeleton");
		combo.addItem("Dog");
		combo.addItem("Hobgoblin");
		combo.addItem("Baby red dragon");
		
		JButton button = new JButton("Start");
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(60,32));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch((String)combo.getSelectedItem()) {
				case "Skeleton":
					interfaceId = 39600;
					break;
				case "Dog":
					interfaceId = 39601;
					break;
				case "Hobgoblin":
					interfaceId = 39602;
					break;
				case "Baby red dragon":
					interfaceId = 39603;
					break;
				}
				frame.dispose();
			}
			
		});
		
		frame.add(combo);
		frame.add(button);
		frame.setTitle("Guard Maker");

		frame.pack();
		frame.setVisible(true);
		while(frame.isVisible()) {
			Time.sleep(500);
		}
		return interfaceId != -1;
	}
	
	@Override
	public int loop() {
		GameObject space = GameObjects.getNearest(15337);
		if(space != null) {
			if(Widgets.getOpenInterface() == 39550) {
				Packets.sendAction(632, 0, 0, interfaceId, 1);
				Time.sleep(() -> GameObjects.getNearest(15337) == null, 3000);
				return 150;
			}else {
				space.interact("build");
				Time.sleep(() -> Widgets.getOpenInterface() == 39550, 3000);
				return 150;
			}
		}else {
			if(Widgets.getBackDialogId() == 2459) {
				Packets.sendAction(315, -1, 0, 2461, 1);
				Time.sleep(() -> GameObjects.getNearest(guardIds) == null, 3000);
				return 150;
			}
			GameObject guard = GameObjects.getNearest(guardIds);
			if(guard != null) {
				guard.interact("remove");
				Time.sleep(() -> Widgets.getBackDialogId() == 2459, 3000);
				return 150;
			}
		}
		return 150;
	}

    private final Color color1 = new Color(255, 255, 255, 84);
    private final Color color2 = new Color(0, 0, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 23);
    private final Font font2 = new Font("Arial", 0, 16);
	
	@Override
	public void repaint(Graphics g1) {
		int xp = Skills.CONSTRUCTION.getCurrentExp() - startXP;
		int ph = (int) ((xp) * 3600000D / (timer.getElapsed()));
		
        Graphics2D g = (Graphics2D)g1;
        g.setColor(color1);
        g.fillRect(343, 205, 171, 133);//155
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRect(343, 205, 171, 133);
        g.setFont(font1);
        g.drawString("Guard maker", 367, 234);
        g.setFont(font2);
        g.drawString("Time: " + timer.toElapsedString(), 352, 269);
        g.drawString("XP: " + format(xp), 352, 299);
        g.drawString("XP(h): " + format(ph), 352, 315);
        g.drawString("Neo", 481, 334);
	}
	
	private String format(int i) {
		if(i > 1000000) {
			return (i / 1000000) + "M";
		}else if(i > 1000) {
			return (i / 1000) + "K";
		}
		return String.valueOf(i);
	}
	
}
