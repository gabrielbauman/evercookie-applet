package evercookie;

import java.applet.Applet;
import java.awt.HeadlessException;
import java.util.Properties;

import netscape.javascript.JSObject;

/**
 * This applet persists Evercookies in the user's browser using the JNDI
 * PersistenceService. This requires the user to have the Java 1.6 plugin or
 * better. You can call it from Javascript using appletCssId.methodName().
 * 
 * @author Gabriel Bauman <gabe@codehaus.org>
 * 
 */
public class EvercookieApplet extends Applet {

	private static final long serialVersionUID = 1L;

	private Properties data;

	EvercookieBackend backend = new EvercookieJndiBackend();

	@SuppressWarnings("unused")
	private JSObject window;

	public EvercookieApplet() throws HeadlessException {
		super();
		data = new Properties();
	}

	@Override
	public void init() {

		System.out.print("Starting Evercookie applet... ");

		this.window = JSObject.getWindow(this);

		if (backend.isAvailable()) {
			this.data = backend.load();
			System.out.println("Initialization complete. Cache has " + this.data.size() + " entries.");
		}

		super.init();
	}

	/**
	 * Call this from JavaScript to read a cached value.
	 */
	public String getCachedValue(String name) {
		return this.data.getProperty(name);
	}

	/**
	 * Call this from JavaScript to save a cached valuez.
	 */
	public void setCachedValue(String name, String value) {
		this.data.setProperty(name, value);
		this.backend.save(data);
	}

	/**
	 * Call this from Javascript to see if a cached value exists with a given
	 * name.
	 */
	public boolean containsCachedValue(String name) {
		return this.data.containsKey(name);
	}

	/**
	 * Call this from Javascript to clear and persist an empty cache.
	 */
	public void clearCache()
	{
		data.clear();
		this.backend.save(data);
	}

	/**
	 * @return true if applet is ready and able to persist.
	 */
	public boolean isReady() {
		return this.backend.isAvailable();
	}

}
