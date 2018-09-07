package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of a {@link ILocalizationProvider}.
 * 
 * @author 0036502252
 *
 */
public abstract class AbstractLocalizationProvider
		implements ILocalizationProvider {
	/**
	 * The listeners of this provider.
	 */
	List<ILocalizationListener> listeners;

	/**
	 * Creates a new {@link AbstractLocalizationProvider}.
	 */
	public AbstractLocalizationProvider() {
		listeners = new ArrayList<>();
	}

	/**
	 * Notifies all listeners that a localization change has occurred.
	 */
	public void fire() {
		listeners.forEach(l -> l.localizationChanged());
	}

	public void addLocalizationListener(ILocalizationListener listener) {
		listeners.add(listener);
	}

	public void removeLocalizationListener(ILocalizationListener listener) {
		listeners.remove(listener);
	}
}
