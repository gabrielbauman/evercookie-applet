package evercookie;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * {@link EvercookieJnlpBackend} uses the JNLP {@link PersistenceService} to
 * store Evercookie data. This will only work in Java 1.5 or better.
 * 
 * @author Gabriel Bauman <gabe@codehaus.org>
 * 
 */
class EvercookieJnlpBackend implements EvercookieBackend {

	private PersistenceService persistenceService = null;
	private boolean isAvailable = true;
	private URL codebaseUrl;

	public EvercookieJnlpBackend() {
		super();
		try {
			BasicService basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
			codebaseUrl = basicService.getCodeBase();
			persistenceService = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
		} catch (UnavailableServiceException e) {
			System.err.println("Failed to load JNLP services: " + e.getMessage());
			isAvailable = false;
		}
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void save(final Map<String, String> values) {
		try {
			FileContents file = persistenceService.get(codebaseUrl);
			ObjectOutputStream os = new ObjectOutputStream(file.getOutputStream(true));
			try {
				os.writeObject(values);
				os.flush();
			} finally {
				System.out.println("Saved properties: " + values.toString());
				os.close();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cache not found - reinitializing.");
			initialize();
			save(values); // recursion. This could be bad if things get wonky.
		} catch (Throwable e) {
			System.err.println("Unable to persist cache: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void load(final Map<String, String> data) {
		try {
			FileContents file = persistenceService.get(codebaseUrl);
			ObjectInputStream os = new ObjectInputStream(file.getInputStream());
			try {
				Hashtable<String, String> crap = (Hashtable<String, String>) os.readObject();
				data.putAll(crap);
			} finally {
				os.close();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cache does not exist. Initializing.");
			initialize();
			save(data);
		} catch (ClassNotFoundException e) {
			System.err.println("Cache found but incompatible. Overwriting.");
			save(data);
		} catch (EOFException e) {
			System.err.println("Cache exists but has no header. Overwriting.");
			save(data);
		} catch (Exception e) {
			System.err.println("Unable to load cached data:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void cleanup() {
		try {
			persistenceService.delete(codebaseUrl);
		} catch (Throwable e) {
			System.err.println("Unable to delete cache.");
		}
	}

	private void initialize() {
		try {
			long size = persistenceService.create(codebaseUrl, 16000);
			System.out.println("Cache initialized at " + codebaseUrl + " with size " + size);
		} catch (Throwable e) {
			System.err.println("Unable to initialize cache.");
		}

	}
}
