package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * Instances of classes that implement this interface will be able to give us
 * translations for given keys. Those instances will be subjects in the Observer
 * pattern, which enables dynamical language changes.
 * 
 * @author 0036502252
 *
 */
public interface ILocalizationProvider {
	/**
	 * Adds a listener to the provider's list of listeners.
	 * 
	 * @param listener
	 *            the listener to be attached
	 */
	void addLocalizationListener(ILocalizationListener listener);

	/**
	 * Removes a listener from the provider's list of listeners.
	 * 
	 * @param listener
	 *            the listener to be detached
	 */
	void removeLocalizationListener(ILocalizationListener listener);

	/**
	 * Gets a string for the given key, independent of the language. Depending
	 * on the currently set language, it gets the string from that exact
	 * language file.
	 * 
	 * @param key
	 *            the key from which the string will be gotten
	 * @return the string for the given key
	 */
	String getString(String key);
}
