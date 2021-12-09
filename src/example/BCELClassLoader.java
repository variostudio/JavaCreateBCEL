package example;

import org.apache.bcel.classfile.JavaClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BCELClassLoader extends ClassLoader {
	
	private final JavaClass root;

	public BCELClassLoader(JavaClass rootDir) {
		if (rootDir == null)
			throw new IllegalArgumentException("Null class.");
		root = rootDir;
	}

	@SuppressWarnings({"rawtypes"})
	public Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {

		// Since all support classes of loaded class use same class loader
		// must check subclass cache of classes for things like Object

		// Class loaded yet?
		System.out.println("Try to load class: " + name);
		
		Class c = findLoadedClass(name);
		if (c == null) {
			try {
				c = findSystemClass(name);
			} catch (Exception e) {
				// Ignore these
			}
		} else {
			System.out.println(name + " found as system");
		}
		
		if (c == null) {
			// Convert byte array to Class
			try {
				byte[] data = root.getBytes();
				c = defineClass(name, data, 0, data.length);
				
				if (c == null)
					throw new ClassNotFoundException(name);

				System.out.println("Class loaded: " + c.getCanonicalName() + " from correspond JavaClass ");

			} catch (NoClassDefFoundError ex) {
				// Ignore? A hack!
			}

			// If failed, throw exception
		} else {
			System.out.println("Class loaded: " + c.getCanonicalName() + " by another ClassLoader");	
		}

		// Resolve class definition if approrpriate
		if (resolve) {
			resolveClass(c);
		}

		// Return class just created

		return c;
	}

	public InputStream getResourceAsStream(String name) {
		InputStream res = super.getResourceAsStream(name);
		if (res == null) {
			if (name.replace('/', '.').startsWith(root.getClassName())) { //This is my hack too :-)
				byte[] data = root.getBytes();
				res = new ByteArrayInputStream(data);
			}
		}
		System.out.println("call getResourceAsStream with name: " + name
				+ ", res: " + res);
		return res;
	}
}
