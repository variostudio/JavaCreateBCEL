package org.troll.fun;

import org.apache.bcel.Const;
import org.apache.bcel.generic.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Generate {
    public static void main(String[] args) {
        String className = "org.troll.test.HelloWorld";

        ClassGen cg = new ClassGen(className, "java.lang.Object", "<generated>", Const.ACC_PUBLIC | Const.ACC_SUPER, null);

        ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool

        FieldGen age = new FieldGen(Const.ACC_PUBLIC, Type.INT, "age", cp);

        FieldGen cnt = new FieldGen(Const.ACC_PUBLIC, Type.INT, "cnt", cp);

        cg.addField(age.getField());
        cg.addField(cnt.getField());

        createGetter(cg, age);
        createSetter(cg, age);

        createGetter(cg, cnt);
        createSetter(cg, cnt);

        createMethodMain(cg);

        // il.dispose(); // Allow instruction handles to be reused
        cg.addEmptyConstructor(Const.ACC_PUBLIC);

        try {
            BCELClassLoader loader = new BCELClassLoader(cg.getJavaClass());

            Thread.currentThread().setContextClassLoader(loader);

            @SuppressWarnings("rawtypes")
            Class clx = loader.loadClass(className, true);
            Object instance = clx.getDeclaredConstructor().newInstance();

            Method setterAge = instance.getClass().getMethod("setAge", int.class);
            Method setterCnt = instance.getClass().getMethod("setCnt", int.class);

            setterAge.invoke(instance, 6);
            setterCnt.invoke(instance, 10);

            Method getterAge = instance.getClass().getMethod("getAge");
            Integer newAge = (Integer) getterAge.invoke(instance);

            System.out.println("Age: " + newAge);

            Method getterCnt = instance.getClass().getMethod("getCnt");
            Integer newCnt = (Integer) getterCnt.invoke(instance);

            System.out.println("Cnt: " + newCnt);

            Method mainMethod = instance.getClass().getMethod("main", String[].class);

            String[] aa = {""};
            mainMethod.invoke(instance, new Object[]{aa});

            Field[] f = clx.getFields();
            for (Field field : f) {
                System.out.println("Field: " + field);
            }

            Method[] m = clx.getMethods();
            for (Method field : m) {
                System.out.println("Method: " + field);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    private static void createGetter(ClassGen cg, FieldGen fg) {
        ConstantPoolGen cpg = cg.getConstantPool();
        int accessFlags = fg.getAccessFlags();
        InstructionFactory ins = new InstructionFactory(cg, cpg);

        InstructionList il = new InstructionList();
        MethodGen getter = new MethodGen(accessFlags, fg.getType(), new Type[]{}, new String[]{}, createGetterName(fg.getName()), cg.getClassName(), il, cpg);

        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(ins.createGetField(cg.getClassName(), fg.getName(), fg.getType()));
        il.append(InstructionFactory.createReturn(fg.getType()));

        // must be after the whole method is ready
        getter.setMaxLocals();
        getter.setMaxStack();

        cg.addMethod(getter.getMethod());
    }

    private static void createSetter(ClassGen cg, FieldGen fg) {
        ConstantPoolGen cpg = cg.getConstantPool();
        int accessFlags = fg.getAccessFlags();
        InstructionFactory ins = new InstructionFactory(cg, cpg);

        InstructionList il = new InstructionList();
        MethodGen getter = new MethodGen(accessFlags, Type.VOID, new Type[]{fg.getType()}, new String[]{fg.getName()}, createSetterName(fg.getName()),
                cg.getClassName(), il, cpg);

        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(InstructionFactory.createLoad(fg.getType(), 1));
        il.append(ins.createPutField(cg.getClassName(), fg.getName(), fg.getType()));
        il.append(InstructionFactory.createReturn(Type.VOID));

        // must be after the whole method is ready
        getter.setMaxLocals();
        getter.setMaxStack();

        cg.addMethod(getter.getMethod());
    }

    private static void createMethodMain(ClassGen cg) {
        ConstantPoolGen _cp = cg.getConstantPool();
        InstructionFactory _factory = new InstructionFactory(cg, _cp);
        
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Const.ACC_PUBLIC | Const.ACC_STATIC, Type.VOID,
                new Type[] { new ArrayType(Type.STRING, 1) }, new String[] { "arg0" }, "main", cg.getClassName(), il, _cp);

        il.append(_factory.createFieldAccess("java.lang.System", "out", new ObjectType("java.io.PrintStream"), Const.GETSTATIC));
        il.append(new PUSH(_cp, "Hello World!"));
        il.append(_factory.createInvoke("java.io.PrintStream", "println", Type.VOID, new Type[] { Type.STRING }, Const.INVOKEVIRTUAL));
        il.append(InstructionFactory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        cg.addMethod(method.getMethod());
        il.dispose();
    }

    private static String createGetterName(String name) {
        String firstLetter = name.substring(0, 1); // Get first letter
        String remainder = name.substring(1); // Get remainder of word.
        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

        return "get" + capitalized;
    }

    private static String createSetterName(String name) {
        String firstLetter = name.substring(0, 1); // Get first letter
        String remainder = name.substring(1); // Get remainder of word.
        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

        return "set" + capitalized;
    }
}
