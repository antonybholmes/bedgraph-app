package edu.columbia.rdf.bedgraph;

import org.jebtk.bioinformatics.ext.ucsc.UCSCTrack;
import org.jebtk.modern.list.ModernList;

public class TrackList extends ModernList<UCSCTrack> {

	private static final long serialVersionUID = 1L;

	public TrackList() {
		setCellRenderer(new TrackListRenderer());
		
		setDragReorderEnabled(true);
		
		setRowHeight(42);
	}

	

	
}
