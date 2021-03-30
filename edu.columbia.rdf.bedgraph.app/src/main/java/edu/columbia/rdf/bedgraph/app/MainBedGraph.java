package edu.columbia.rdf.bedgraph.app;


import java.awt.FontFormatException;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;

import org.jebtk.core.AppService;
import org.jebtk.modern.UI;
import org.jebtk.modern.theme.ThemeService;

import edu.columbia.rdf.bedgraph.MainBedGraphWindow;




public class MainBedGraph {
	public static final void main(String[] args) throws FontFormatException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		AppService.getInstance().setAppInfo("bedgraph");
		
		ThemeService.getInstance().setTheme();
			
		main();
	}
	
	public static void main() {
		JFrame window = new MainBedGraphWindow();

		UI.centerWindowToScreen(window);

		window.setVisible(true);
	}
	
	public static void main(Path file) {
		JFrame window = new MainBedGraphWindow(file);

		UI.centerWindowToScreen(window);

		window.setVisible(true);
	}
}
