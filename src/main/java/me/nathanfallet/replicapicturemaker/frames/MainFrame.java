package me.nathanfallet.replicapicturemaker.frames;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import me.nathanfallet.replicapicturemaker.panels.MainPane;

public class MainFrame extends JFrame {

	public MainFrame() {
		setTitle("ReplicaPictureMaker by ZabriCraft");
		setSize(514, 675);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setContentPane(new MainPane());
		addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 83) {
					new ResultFrame();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		setVisible(true);
	}

}
