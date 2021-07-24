package edu.columbia.rdf.bedgraph;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.bioinformatics.ext.ucsc.BedGraph;
import org.jebtk.bioinformatics.ext.ucsc.UCSCTrack;
import org.jebtk.modern.ModernComponent;
import org.jebtk.modern.AssetService;
import org.jebtk.modern.button.ModernButton;
import org.jebtk.modern.contentpane.HTabToolbar;
import org.jebtk.modern.dialog.DialogEvent;
import org.jebtk.modern.dialog.DialogEventListener;
import org.jebtk.modern.dialog.MessageDialogType;
import org.jebtk.modern.dialog.ModernDialogStatus;
import org.jebtk.modern.dialog.ModernMessageDialog;
import org.jebtk.modern.event.ModernClickEvent;
import org.jebtk.modern.event.ModernClickListener;
import org.jebtk.modern.event.ModernSelectionListener;
import org.jebtk.modern.ribbon.ToolbarButton;
import org.jebtk.modern.scrollpane.ModernScrollPane;
import org.jebtk.modern.scrollpane.ScrollBarPolicy;
import org.jebtk.modern.window.ModernRibbonWindow;

public class BedGraphsPanel extends ModernComponent implements ModernClickListener {
	private static final long serialVersionUID = 1L;

	private TrackList mTrackList = new TrackList();

	private TrackListModel mListModel = new TrackListModel();

	private ModernButton mDeleteButton = 
			new ToolbarButton(AssetService.getInstance().loadIcon("trash_bw", 16));

	private ModernRibbonWindow mParent;

	private class DeleteEvents implements DialogEventListener {
		@Override
		public void statusChanged(DialogEvent e) {
			if (e.getStatus() == ModernDialogStatus.OK) {
				ArrayList<Integer> indices = new ArrayList<Integer>();

				for (int i : mTrackList.getSelectionModel()) {
					indices.add(i);
				}

				mListModel.removeValuesAt(indices);
			}
		}
	}

	public BedGraphsPanel(ModernRibbonWindow parent) {

		mParent = parent;

		setup();

		createUi();

		// Sync ui
		mTrackList.getModel().fireDataChanged();
	}

	public void createUi() {
		HTabToolbar toolbar = new HTabToolbar("Tracks");

		mDeleteButton.setToolTip("Delete", "Delete selected tracks.");
		toolbar.add(mDeleteButton);

		setHeader(toolbar);

		ModernScrollPane scrollPane = new ModernScrollPane(mTrackList);
		scrollPane.setHorizontalScrollBarPolicy(ScrollBarPolicy.NEVER);
		//scrollPane.setBorder(BorderService.getInstance().createTopBorder(10));
		setBody(scrollPane);

		/*
		Box box = new ToolbarBottomBox();

		mSamplesButton.setToolTip("Samples Database", "Load ChIP-seq samples from database.");
		box.add(mSamplesButton);
		box.add(ModernTheme.createHorizontalGap());
		mTracksButton.setToolTip("Load Annotation Tracks", "Load additional annotation tracks.");
		box.add(mTracksButton);
		box.add(ModernTheme.createHorizontalGap());
		mEditButton.setToolTip("Edit Tracks", "Edit track properties.");
		box.add(mEditButton);
		box.add(ModernTheme.createHorizontalGap());
		//box.add(Box.createHorizontalGlue());
		mDeleteButton.setToolTip("Delete", "Delete selected tracks.");
		box.add(mDeleteButton);
		//box.add(ModernTheme.createHorizontalGap());
		//mClearButton.setToolTip("Clear", "Remove all tracks.");
		//box.add(mClearButton);

		add(box, BorderLayout.PAGE_END);
		 */


	}

	private void setup() {
		mTrackList.setModel(mListModel);

		mDeleteButton.addClickListener(this);
	}

	@Override
	public void clicked(ModernClickEvent e) {
		if (e.getSource().equals(mDeleteButton)) {
			deleteTracks();
		} else {
			// do nothing
		}
	}

	private void deleteTracks() {
		mParent.createOkCancelDialog("Are you sure you want to delete the selected tracks?", 
				new DeleteEvents());
	}

	private void clearTracks() {
		ModernDialogStatus status = ModernMessageDialog.createDialog(mParent, 
				"Are you sure you want to delete all tracks?", 
				MessageDialogType.WARNING_OK_CANCEL);

		if (status == ModernDialogStatus.OK) {
			mListModel.clear();
		}
	}

	public void openFiles(List<Path> files) throws Exception {
		if (files == null) {
			return;
		}

		List<UCSCTrack> tracks = new ArrayList<UCSCTrack>();

		for (Path file : files) {

			List<BedGraph> fileTracks = BedGraph.parse(file);
				
			for (UCSCTrack track : fileTracks) {
				tracks.add(track);
			}
		}

		openTracks(tracks);
	}
	
	public void openTracks(List<UCSCTrack> tracks) {
		mListModel.addValues(tracks);
		
		mTrackList.setSelectedIndex(0);
	}
	
	public void addSelectionListener(ModernSelectionListener l) {
		mTrackList.addSelectionListener(l);
	}

	public UCSCTrack getSelectedTrack() {
		return mTrackList.getSelectedItem();
	}

	public List<UCSCTrack> getTracks() {
		List<UCSCTrack> tracks = new ArrayList<UCSCTrack>();
		
		for (UCSCTrack bedGraph : mListModel) {
			tracks.add(bedGraph);
		}
		
		return tracks;
	}
}
