package evercookie;

import java.applet.Applet;
import java.awt.HeadlessException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Properties;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

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

	private PersistenceService persistenceService = null;
	private URL codebaseUrl;
	private Properties data;
	private boolean ready = false;

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

		// Attempt to fire up JNLP services. This will only work on Java 1.6
		// JVMs or better. PersistenceService did not exist prior to that, so
		// this applet is useless on pre-1.6 JVMs.

		try {
			BasicService basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
			codebaseUrl = basicService.getCodeBase();
		} catch (UnavailableServiceException e) {
			System.err.println("Failed to load javax.jnlp.BasicService: " + e.getMessage());
			return;
		}

		try {
			persistenceService = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
		} catch (UnavailableServiceException e) {

			System.err.println("Failed to load javax.jnlp.PersistenceService: " + e.getMessage());
			return;
		}

		// Load existing cache or initialize a new one.
		this.loadCache();
		System.out.println("Initialization complete. Cache has " + this.data.size() + " entries.");

		this.ready = true;

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
		this.saveCache();
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
		System.out.println("Clearing cache.");
		data.clear();
		saveCache();
	}

	/**
	 * @return true if applet is ready and able to persist.
	 */
	public boolean isReady() {
		return ready;
	}

	public void deleteCache()
	{
		System.out.println("Deleting cache. Reinitialization will be required.");
		data.clear();
		try {
			persistenceService.delete(this.codebaseUrl);
		} catch (Exception e) {
			System.err.println("Unable to delete cache.");
			e.printStackTrace();
		}
	}

	private void saveCache() {

		System.out.println("Saving cache.");

		try {
			FileContents file = persistenceService.get(this.codebaseUrl);
			ObjectOutputStream os = new ObjectOutputStream(file.getOutputStream(true));
			try {
				os.writeObject(this.data);
				os.flush();
			} finally {
				os.close();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cache not found - reinitializing cache. Reload the applet.");
			initializeCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadCache() {

		System.out.println("Loading cache.");

		try {
			FileContents file = persistenceService.get(codebaseUrl);
			ObjectInputStream os = new ObjectInputStream(file.getInputStream());
			try {
				this.data = (Properties) os.readObject();
			} finally {
				os.close();
			}

			// Now, notify Javascript of each stored value, one by one.
			/**
			 * Enumeration<String> e = (Enumeration<String>)
			 * data.propertyNames();
			 * 
			 * while (e.hasMoreElements()) { String key = e.nextElement();
			 * window.eval("onEvercookieValueFound('" + key + "','" +
			 * data.getProperty(key) + "')"); }
			 **/

		} catch (FileNotFoundException e) {
			System.err.println("Cache does not exist. Initializing.");
			this.initializeCache();
		} catch (ClassNotFoundException e) {
			System.err.println("Cache found but incompatible. Reinitializing.");
			this.initializeCache();
		} catch (EOFException e) {
			System.err.println("Cache exists but has no header.");
			this.initializeCache();
		} catch (Exception e) {
			System.err.println("Unable to load cached data.");
			e.printStackTrace();
		}
	}

	private void initializeCache() {

		System.out.println("Initializing cache.");

		this.deleteCache();

		try {
			long size = persistenceService.create(codebaseUrl, 16000);
			System.out.println("Cache initialized at " + codebaseUrl + " with size " + size);
		} catch (Exception e) {
			System.err.println("Unable to initialize cache.");
			e.printStackTrace();
			System.exit(0);
		}

		this.saveCache();
	}

}
