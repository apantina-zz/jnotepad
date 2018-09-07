package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * The decorator for other {@link ILocalizationProvider} classes. It manages a
 * connection status, and transmits all listener notifications further to the
 * localization provider it is connected to. It is used in order to ensure that
 * once the program is closed, the JVM's garbage collector picks up all stray
 * references.
 * 
 * @author 0036502252
 *
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {
	/**
	 * The connection status of the bridge.
	 */
	private boolean connected;
	/**
	 * The parent to which this bridge is connected to.
	 */
	private ILocalizationProvider parent;
	/**
	 * 
	 */
	private ILocalizationListener listener;

	/**
	 * Constructs a new {@link LocalizationProviderBridge}. 
	 * 
	 * @param parent the parent to which this bridge will connect to
	 */
	public LocalizationProviderBridge(ILocalizationProvider parent) {
		super();
		this.parent = parent;
		this.listener = new ILocalizationListener() {

			@Override
			public void localizationChanged() {
				fire();
			}
		};
	}

	/**
	 * Connects to the parent localization provider.
	 */
	public void connect() {
		connected = true;
		parent.addLocalizationListener(listener);
	}

	/**
	 * Disconnects from the parent localization provider.
	 */
	public void disconnect() {
		connected = false;
		parent.removeLocalizationListener(listener);
	}

	@Override
	public String getString(String key) {
		return parent.getString(key);
	}

}
