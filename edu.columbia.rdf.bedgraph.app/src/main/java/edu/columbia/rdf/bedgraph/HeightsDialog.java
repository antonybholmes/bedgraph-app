package edu.columbia.rdf.bedgraph;

import org.jebtk.modern.input.ModernIntInputDialog;
import org.jebtk.modern.window.ModernWindow;

/**
 * User can select how many annotations there are
 * @author Antony Holmes
 *
 */
public class HeightsDialog extends ModernIntInputDialog {
	private static final long serialVersionUID = 1L;
	
	public HeightsDialog(ModernWindow parent, int height) {
		super(parent, 
				"Set BedGraph Height", 
				"Height", 
				height, 
				1, 
				128);
	}
}
