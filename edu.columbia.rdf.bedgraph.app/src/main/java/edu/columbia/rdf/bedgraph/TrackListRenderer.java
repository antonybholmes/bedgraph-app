package edu.columbia.rdf.bedgraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;

import org.jebtk.bioinformatics.ext.ucsc.UCSCTrack;
import org.jebtk.core.ColorUtils;
import org.jebtk.core.text.TextUtils;
import org.jebtk.modern.list.ModernList;
import org.jebtk.modern.list.ModernListCellRenderer;
import org.jebtk.modern.theme.ThemeService;



/**
 * Renders a file as a list item.
 * 
 * @author Antony Holmes
 *
 */
public class TrackListRenderer extends ModernListCellRenderer {
	private static final long serialVersionUID = 1L;
	private static final int ORB_WIDTH = 8;
	
	private String mText = "";
	private Color mColor = Color.RED;
	private Color mFillColor;
	private int mRow;

	public static final Color LINE_COLOR = 
			ThemeService.getInstance().getColors().getGray(2);
	
	public static final Color COLOR = 
			ThemeService.getInstance().getColors().getGray(6);
	
	
	private static final int NUM_WIDTH = 20;
	

	@Override
	public void drawForegroundAA(Graphics2D g2) {
		int x = DOUBLE_PADDING;
		int y = getHeight() / 2;
		int x2 = getWidth() - ORB_WIDTH - DOUBLE_PADDING;
		
		//fill(g2, mFillColor);
		
		//g2.setColor(COLOR);
		g2.setColor(TEXT_COLOR);
		
		String t = mRow + ".";
		
		g2.drawString(t, 
				x + NUM_WIDTH - g2.getFontMetrics().stringWidth(t) - PADDING, 
				getTextYPosCenter(g2, getHeight()));
		
		x += NUM_WIDTH;

		int tw = x2 - x - PADDING;

		

		// Keep truncating the text until it fits into the available space.
		for (int i = mText.length(); i >= 0; --i) {
			t = TextUtils.truncate(mText, i);

			if (g2.getFontMetrics().stringWidth(t) <= tw) {
				break;
			}
		}

		
		//g2.setColor(mFillColor);
		g2.drawString(t, x, getTextYPosCenter(g2, getHeight()));


		g2.setColor(mFillColor);
		y = (getHeight() - ORB_WIDTH) / 2;
		
		g2.fillOval(x2, y, ORB_WIDTH, ORB_WIDTH);
		
		/*
		g2.setColor(mFillColor);
		
		g2.drawLine(x2, y, x2 + ORB_WIDTH, y);
		
		y -= 2;
		
		g2.drawLine(x2, y, x2 + ORB_WIDTH, y);
		
		y += 4;
		
		g2.drawLine(x2, y, x2 + ORB_WIDTH, y);

		y = getHeight() - 1;
		g2.setColor(LINE_COLOR);
		//g2.setColor(mFillColor);
		g2.drawLine(0, y, getWidth(), y);
		*/
		
	}

	@Override
	public Component getCellRendererComponent(ModernList<?> list,
			Object value,
			boolean highlight,
			boolean isSelected,
			boolean hasFocus,
			int row) {

		UCSCTrack track = (UCSCTrack)value;
		
		mText = track.getName();
		mColor = track.getColor(); 
		mFillColor = ColorUtils.tint(mColor, 0.5);
		mRow = row + 1;

		return super.getCellRendererComponent(list, 
				value, 
				highlight, 
				isSelected, 
				hasFocus, 
				row);
	}
}