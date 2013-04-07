package evercookie;

import java.applet.Applet;
import java.awt.HeadlessException;
import java.util.Properties;

/**
 * This applet persists Evercookies in the user's browser using as many
 * strategies as possible. You can call it from Javascript using domId.set() or
 * domId.get().
 * 
 * @author Gabriel Bauman <gabe@codehaus.org>
 * 
 */
public class EvercookieApplet extends Applet {

	private static final long serialVersionUID = 1L;
	private static final EvercookieBackend[] backends = {
			new EvercookieJnlpBackend(), new EvercookieFileBackend() };
	private final Properties data = new Properties();
	private boolean workingBackends = false;

	public EvercookieApplet() throws HeadlessException {
		super();
	}

	@Override
	public void init() {

		for (EvercookieBackend backend : backends) {
			if (backend.isAvailable()) {
				this.workingBackends = true;
				break;
			}
		}

		if (!workingBackends) {
			System.out.println("Initialization failed. No working backends.");
			return;
		}

		load(data);
		System.out.println("Initialization complete. Cache has " + this.data.size() + " entries.");
		super.init();
	}

	public String get(String name) {
		return data.getProperty(name);
	}

	public void set(String name, String value) {
		data.setProperty(name, value);
		save(data);
	}

	private void save(Properties values) {

		if (!workingBackends) {
			return;
		}

		for (EvercookieBackend backend : backends) {
			if (backend.isAvailable()) {
				backend.save(values);
			}
		}

	}

	private void load(Properties data) {

		if (!workingBackends) {
			return;
		}

		Properties loaded = new Properties();
		data.clear();

		for (EvercookieBackend backend : backends) {
			if (backend.isAvailable()) {
				backend.load(loaded);
				data.putAll(loaded);
			}
		}
	}

}
