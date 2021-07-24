package edu.columbia.rdf.bedgraph.app;

import org.jebtk.core.AppVersion;
import org.jebtk.modern.AssetService;
import org.jebtk.modern.help.GuiAppInfo;


public class BedGraphInfo extends GuiAppInfo {

	public BedGraphInfo() {
		super("BedGraph",
				new AppVersion(6),
				"Copyright (C) 2014-${year} Antony Holmes",
				AssetService.getInstance().loadIcon(BedGraphIcon.class, 128),
				"Manipulate BedGraph files.");
	}

}
