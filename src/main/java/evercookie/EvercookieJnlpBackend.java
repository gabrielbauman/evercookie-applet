package evercookie;

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

public class EvercookieJnlpBackend implements EvercookieBackend {

	private boolean isAvailable = true;
	private PersistenceService persistenceService = null;
	private URL codebaseUrl;

	public EvercookieJnlpBackend() {
		super();
		try {
			BasicService basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
			codebaseUrl = basicService.getCodeBase();
			persistenceService = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
			this.isAvailable = true;

		} catch (UnavailableServiceException e) {
			System.err.println("Failed to load JNLP services: " + e.getMessage());
			this.isAvailable = false;
		}
	}

	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}

	@Override
	public void save(final Properties values) {
		try {
			FileContents file = persistenceService.get(this.codebaseUrl);
			ObjectOutputStream os = new ObjectOutputStream(file.getOutputStream(true));
			try {
				os.writeObject(values);
				os.flush();
			} finally {
				os.close();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cache not found - reinitializing cache. Reload the applet.");
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load(final Properties data) {
		data.clear();
		try {
			FileContents file = persistenceService.get(codebaseUrl);
			ObjectInputStream os = new ObjectInputStream(file.getInputStream());
			try {
				((Properties) os.readObject()).putAll(data);
			} finally {
				os.close();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cache does not exist. Initializing.");
			this.initialize();
			this.save(data);
		} catch (ClassNotFoundException e) {
			System.err.println("Cache found but incompatible. Overwriting.");
			this.save(data);
		} catch (EOFException e) {
			System.err.println("Cache exists but has no header. Overwriting.");
			this.save(data);
		} catch (Exception e) {
			System.err.println("Unable to load cached data.");
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		try {
			persistenceService.delete(this.codebaseUrl);
		} catch (Exception e) {
			System.err.println("Unable to delete cache.");
			e.printStackTrace();
		}
	}

	/**
	 * This creates the "file" that cookie data is stored in.
	 */
	private void initialize() {

		try {
			long size = persistenceService.create(codebaseUrl, 16000);
			System.out.println("Cache initialized at " + codebaseUrl + " with size " + size);
		} catch (Exception e) {
			System.err.println("Unable to initialize cache.");
			e.printStackTrace();
		}

	}
}
