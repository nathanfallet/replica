package me.nathanfallet.replicapicturemaker.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import me.nathanfallet.replicapicturemaker.ReplicaPictureMaker;

public class MainPane extends JPanel {

	private int color = 0;

	public MainPane() {
		setPreferredSize(new Dimension(513, 641));
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getY() < 512) {
					int x = 0, y = 0;
					for (int current = e.getX(); current >= 64; current -= 64) {
						x++;
					}
					for (int current = e.getY(); current >= 64; current -= 64) {
						y++;
					}
					ReplicaPictureMaker.getCurrent().setBlock(color, x, y);
				} else if (e.getY() > 515) {
					int x = 0, y = 0, i = 0;
					for (int current = e.getX(); current >= 64; current -= 64) {
						x++;
					}
					for (int current = e.getY() - 516; current >= 64; current -= 64) {
						y++;
					}
					for (int x2 = 0; x2 < 8; x2++) {
						for (int y2 = 0; y2 < 2; y2++) {
							if (x == x2 && y == y2) {
								color = i;
							}
							i++;
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				g.setColor(ReplicaPictureMaker.getColor(ReplicaPictureMaker.getCurrent().getBlock(x, y)));
				g.fillRect(x * 64, y * 64, 64, 64);
			}
		}
		int i = 0;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 2; y++) {
				g.setColor(ReplicaPictureMaker.getColor(i));
				g.fillRect(x * 64, y * 64 + 516, 64, 64);
				if (color == i) {
					g.setColor(Color.RED);
					g.drawRect(x * 64 + 1, y * 64 + 517, 61, 60);
				}
				i++;
			}
		}
		g.setColor(Color.BLACK);
		for (i = 1; i < 9; i++) {
			g.drawLine(0, i * 64, 512, i * 64);
		}
		for (i = 1; i < 9; i++) {
			g.drawLine(i * 64, 0, i * 64, 643);
		}
		g.fillRect(0, 513, 512, 3);
		for (i = 9; i < 11; i++) {
			g.drawLine(0, i * 64 + 3, 512, i * 64 + 3);
		}
		repaint();
	}

}
