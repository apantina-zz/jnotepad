package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * Adds a {@link WindowListener} to the {@link LocalizationProviderBridge}, so
 * that when the main window frame closes, the JVM picks up the Singleton's
 * reference.
 * 
 * @author 0036502252
 * @see LocalizationProvider
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {

	/**
	 * Constructs a new {@link FormLocalizationProvider}.
	 * @param parent the 
	 * @param frame
	 */
	public FormLocalizationProvider(ILocalizationProvider parent,
			JFrame frame) {
		super(parent);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				connect();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				disconnect();
			}
		});
	}
}
