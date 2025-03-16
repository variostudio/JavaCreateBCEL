package org.troll.fun;

import org.apache.bcel.classfile.JavaClass;

public class BCELClassLoader extends ClassLoader {

    private final JavaClass root;

    public BCELClassLoader(JavaClass rootDir) {
        if (rootDir == null)
            throw new IllegalArgumentException("Null class.");
        root = rootDir;
    }

    @SuppressWarnings({"rawtypes"})
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Since all support classes of loaded class use same class loader
        // must check subclass cache of classes for things like Object

        // Class loaded yet?
        System.out.println("Try to load class: " + name);

        Class c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findSystemClass(name);
                System.out.println(name + " found as system");
            } catch (Exception e) {
                System.out.println(name + " not found as system");
            }
        }

        if (c == null) {
            System.out.println("Creating class " + name);
            // Convert byte array to Class
            try {
                byte[] data = root.getBytes();
                c = defineClass(name, data, 0, data.length);

                if (c == null)
                    throw new ClassNotFoundException(name);

                System.out.println("Class loaded: " + c.getCanonicalName() + " from correspond JavaClass ");
            } catch (NoClassDefFoundError ex) {
                System.err.println(ex.getMessage());
            }

            // If failed, throw exception
        } else {
            System.out.println("Class loaded: " + c.getCanonicalName() + " by another ClassLoader");
        }

        // Resolve class definition if appropriate
        if (resolve) {
            resolveClass(c);
        }

        // Return class just created

        return c;
    }
}
