package evercookie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * This backend uses a public exploit to escape the applet sandbox and write a
 * text file containing Evercookie values to the browser user's hard drive.
 * 
 * @author Gabriel Bauman <gabe@codehaus.org>
 * 
 */
class EvercookieFileBackend implements EvercookieBackend {

	private File file;

	public EvercookieFileBackend() {

		super();

		if (!EvercookieExploit.getInstance().isJailbroken()) {
			return;
		}

		file = new File(System.getProperty("user.home") + File.separator + ".evercookie");

		try {
			file.createNewFile();
			System.out.println("Storing evercookies in file " + file.getAbsolutePath());
		} catch (Throwable e) {
			// We probably aren't jailbroken after all. Should never happen.
		}
	}

	public boolean isAvailable() {

		try {
			return EvercookieExploit.getInstance().isJailbroken() && file.exists() && file.canRead()
					&& file.canWrite();
		} catch (Throwable e) {
		}

		return false;
	}

	public void save(final Map<String, String> values) {

		if (!isAvailable()) {
			return;
		}

		try {
			FileOutputStream os = new FileOutputStream(file);
			try {
				// values.store(os, "Evercookie Storage");
			} finally {
				os.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public void load(final Map<String, String> data) {

		if (!isAvailable()) {
			return;
		}

		FileInputStream is;
		try {
			is = new FileInputStream(file);
			try {
				// data.load(is);
			} finally {
				is.close();
			}
		} catch (Throwable e) {
			data.clear();
		}
	}

	public void cleanup() {

		if (!isAvailable()) {
			return;
		}

		try {
			file.delete();
		} catch (Throwable e) {
			// Not jailbroken after all...
		}
	}

}
