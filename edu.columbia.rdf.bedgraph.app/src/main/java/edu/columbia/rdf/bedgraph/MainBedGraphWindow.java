package edu.columbia.rdf.bedgraph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jebtk.bioinformatics.ext.ucsc.Bed;
import org.jebtk.bioinformatics.ext.ucsc.BedGraph;
import org.jebtk.bioinformatics.ext.ucsc.UCSCTrack;
import org.jebtk.bioinformatics.ui.BioInfDialog;
import org.jebtk.bioinformatics.ui.external.ucsc.TrackPanel;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.event.ChangeEvent;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.PathUtils;
import org.jebtk.modern.UI;
import org.jebtk.modern.button.ModernClickWidget;
import org.jebtk.modern.AssetService;
import org.jebtk.modern.clipboard.ClipboardRibbonSection;
import org.jebtk.modern.dialog.DialogEvent;
import org.jebtk.modern.dialog.DialogEventListener;
import org.jebtk.modern.dialog.ModernDialogStatus;
import org.jebtk.modern.event.ModernClickEvent;
import org.jebtk.modern.event.ModernClickListener;
import org.jebtk.modern.event.ModernSelectionListener;
import org.jebtk.modern.graphics.icons.QuickOpenVectorIcon;
import org.jebtk.modern.graphics.icons.QuickSaveVectorIcon;
import org.jebtk.modern.help.ModernAboutDialog;
import org.jebtk.modern.io.OpenRibbonPanel;
import org.jebtk.modern.io.RecentFilesService;
import org.jebtk.modern.io.SaveAsRibbonPanel;
import org.jebtk.modern.ribbon.QuickAccessButton;
import org.jebtk.modern.ribbon.RibbonLargeButton;
import org.jebtk.modern.ribbon.RibbonMenuItem;
import org.jebtk.modern.tooltip.ModernToolTip;
import org.jebtk.modern.window.ModernRibbonWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.columbia.rdf.bedgraph.app.BedGraphInfo;


/**
 * Window for showing 2D graphs such as a scatter plot.
 * 
 * @author Antony Holmes
 *
 */
public class MainBedGraphWindow extends ModernRibbonWindow implements ModernClickListener {
	private static final long serialVersionUID = 1L;

	private OpenRibbonPanel openPanel = new OpenRibbonPanel();

	private SaveAsRibbonPanel saveAsPanel = new SaveAsRibbonPanel();

	private BedGraphsPanel mBedGraphsPanel;


	private Map<UCSCTrack, Path> mTrackMap = new HashMap<UCSCTrack, Path>();

	private static final Logger LOG = 
			LoggerFactory.getLogger(MainBedGraphWindow.class);

	private class TrackEvents implements ModernSelectionListener {

		@Override
		public void selectionChanged(ChangeEvent e) {
			editBedGraph();
		}

	}

	private class ExportCallBack implements DialogEventListener {

		private Path mFile;

		public ExportCallBack(Path file) {
			mFile = file;
		}

		@Override
		public void statusChanged(DialogEvent e) {
			if (e.getStatus() == ModernDialogStatus.OK) {
				try {
					save(mFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public MainBedGraphWindow() {
		this(null);
	}

	public MainBedGraphWindow(Path file) {
		super(new BedGraphInfo());

		init();

		if (file != null) {
			try {
				openFile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void init() {
		createRibbon();

		createUi();

		mBedGraphsPanel.addSelectionListener(new TrackEvents());

		setSize(1000, 600);

		UI.centerWindowToScreen(this);
	}

	public final void createRibbon() {
		//RibbongetRibbonMenu() getRibbonMenu() = 
		//		new RibbongetRibbonMenu()(SettingsService.getInstance().getInt("/bedgraph/file-menu-default-index"));
		RibbonMenuItem menuItem;

		menuItem = new RibbonMenuItem(UI.MENU_OPEN);
		getRibbonMenu().addTabbedMenuItem(menuItem, openPanel);

		menuItem = new RibbonMenuItem(UI.MENU_SAVE);
		getRibbonMenu().addTabbedMenuItem(menuItem, saveAsPanel);

		menuItem = new RibbonMenuItem(UI.MENU_SAVE_ALL);
		getRibbonMenu().addTabbedMenuItem(menuItem);
		
		
		getRibbonMenu().addDefaultItems(getAppInfo());



		getRibbonMenu().addClickListener(this);


		ModernClickWidget button;

		//Ribbon2 ribbon = new Ribbon2();
		getRibbon().setHelpButtonEnabled(getAppInfo());

		button = new QuickAccessButton(AssetService.getInstance().loadIcon(QuickOpenVectorIcon.class, 16));
		button.setClickMessage(UI.MENU_OPEN);
		button.setToolTip(new ModernToolTip("Open", 
				"Open BedGraph files."));
		button.addClickListener(this);
		addQuickAccessButton(button);

		button = new QuickAccessButton(AssetService.getInstance().loadIcon(QuickSaveVectorIcon.class, 16));
		button.setClickMessage(UI.MENU_SAVE);
		button.setToolTip(new ModernToolTip("Save", 
				"Save the current BedGraph file."));
		button.addClickListener(this);
		addQuickAccessButton(button);



		// home
		getRibbon().getHomeToolbar().add(new ClipboardRibbonSection(getRibbon()));

		button = new RibbonLargeButton("Set All Heights",
				AssetService.getInstance().loadIcon("height", 32),
				"Set All Heights", 
				"Set the height of all BedGraph tracks.");
		button.addClickListener(this);

		getRibbon().getHomeToolbar().getSection("Tools").add(button);

		//setRibbon(ribbon, getRibbonMenu());

		getRibbon().setSelectedIndex(1);
	}

	/*
	public void setFormatPane(FormatPlotPane formatPane) {
		this.formatPane = formatPane;

		addFormatPane();
	}
	 */

	public final void createUi() {

		mBedGraphsPanel = new BedGraphsPanel(this);



		//mContentPane.getModel().setCenterTab(new CenterTab(panel));

		addBedGraphPane();
	}

	/**
	 * Adds the group pane to the layout if it is not already showing.
	 */
	private void addBedGraphPane() {
		if (tabsPane().tabs().left().contains("Files")) {
			return;
		}

		tabsPane().addLeftTab("Files", mBedGraphsPanel, 250, 200, 500);
	}

	@Override
	public final void clicked(ModernClickEvent e) {
		System.err.println("e " + e.getMessage());

		if (e.getMessage().equals(UI.MENU_OPEN) ||
				e.getMessage().equals(UI.MENU_BROWSE) ||
				e.getMessage().startsWith("Other...")) {
			try {
				browseForFile();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getMessage().equals(OpenRibbonPanel.FILE_SELECTED)) {
			try {
				openFile(openPanel.getSelectedFile());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getMessage().equals(OpenRibbonPanel.DIRECTORY_SELECTED)) {
			try {
				browseForFile(openPanel.getSelectedDirectory());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getMessage().equals(UI.MENU_SAVE_ALL)) {
			try {
				saveAll();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getMessage().equals(UI.MENU_SAVE)) {
			try {
				export();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getMessage().equals(SaveAsRibbonPanel.DIRECTORY_SELECTED)) {
			try {
				export(saveAsPanel.getSelectedDirectory());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getMessage().equals("Set All Heights")) {
			setAllHeights();
		} else if (e.getMessage().equals(UI.MENU_ABOUT)) {
			ModernAboutDialog.show(this, getAppInfo());
		} else if (e.getMessage().equals(UI.MENU_EXIT)) {
			close();
		} else {

		}
	}

	private void editBedGraph() {
		UCSCTrack bedGraph = mBedGraphsPanel.getSelectedTrack();

		if (bedGraph == null) {
			return;
		}

		TrackPanel dialog = new TrackPanel(this, bedGraph);

		setCard(dialog);
	}

	private void setAllHeights() {
		HeightsDialog hd = new HeightsDialog(this, 128);

		hd.setVisible(true);

		if (hd.getStatus() == ModernDialogStatus.CANCEL) {
			return;
		}

		int height = hd.getValue();

		for (UCSCTrack bedGraph : mBedGraphsPanel.getTracks()) {
			bedGraph.setHeight(height);
		}
	}

	private void browseForFile() throws Exception {
		browseForFile(RecentFilesService.getInstance().getPwd());
	}

	private void browseForFile(Path pwd) throws Exception {
		openFiles(BioInfDialog.open(this).bedAndBedgraph().getFiles(pwd));
	}

	public void openFile(Path file) throws Exception {
		openFiles(CollectionUtils.asList(file));
	}

	public void openFiles(List<Path> files) throws Exception {
		if (files == null) {
			return;
		}

		//mBedMap.clear();

		List<UCSCTrack> tracks = new ArrayList<UCSCTrack>();

		for (Path file : files) {
			List<UCSCTrack> tmpTracks;

			if (PathUtils.getFileExt(file).equals("bed")) {
				tmpTracks = Bed.parseTracks(file);
			} else {
				tmpTracks = BedGraph.parse(file);
			}

			for (UCSCTrack track : tmpTracks) {
				tracks.add(track);

				RecentFilesService.getInstance().add(file);

				mTrackMap.put(track, file);
			}
		}

		mBedGraphsPanel.openTracks(tracks);
	}


	private void export() throws IOException {
		export(RecentFilesService.getInstance().getPwd());
	}

	private void export(Path pwd) throws IOException {
		Path file = BioInfDialog.saveBedGraphFile(this, pwd);

		if (file == null) {
			return;
		}

		if (FileUtils.exists(file)) {
			createFileExistsDialog(file, new ExportCallBack(file));
		} else {
			save(file);
		}
	}

	/**
	 * Save all of the currently loaded bedGraphs.
	 * 
	 * @throws IOException
	 */
	private void saveAll() throws IOException {
		for (UCSCTrack bedGraph : mBedGraphsPanel.getTracks()) {
			LOG.debug("Saving {}", bedGraph.getName());

			BedGraph.write(bedGraph, mTrackMap.get(bedGraph));
		}
	}


	private void save(Path file) throws IOException {
		List<UCSCTrack> bedGraphs = mBedGraphsPanel.getTracks();

		BedGraph.write(bedGraphs, file);

		createFileSavedDialog(file);
	}

}
