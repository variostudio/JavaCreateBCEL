package example;

import java.lang.reflect.Method;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.FieldGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Geneate {
	public static void main(String[] args) {
		String className = "org.troll.test.HelloWorld";
		// String className = "HelloWorld";

		ClassGen cg = new ClassGen(className, "java.lang.Object", "<generated>", Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);

		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		// InstructionList il = new InstructionList();

		FieldGen age = new FieldGen(Constants.ACC_PUBLIC, Type.INT, "age", cp);

		FieldGen cnt = new FieldGen(Constants.ACC_PUBLIC, Type.INT, "cnt", cp);

		cg.addField(age.getField());
		cg.addField(cnt.getField());

		createGetter(cg, age);
		createSetter(cg, age);

		createGetter(cg, cnt);
		createSetter(cg, cnt);

		// il.dispose(); // Allow instruction handles to be reused
		cg.addEmptyConstructor(Constants.ACC_PUBLIC);

		try {
			BCELClassLoader loader = new BCELClassLoader(cg.getJavaClass());

			Thread.currentThread().setContextClassLoader(loader);

			@SuppressWarnings("rawtypes")
			Class clx = loader.loadClass(className, true);
			Object instance = clx.newInstance();

			Method setter = instance.getClass().getMethod("setAge", int.class);
			Method setterCnt = instance.getClass().getMethod("setCnt", int.class);

			setter.invoke(instance, 6);
			setterCnt.invoke(instance, 10);

			Method getter = instance.getClass().getMethod("getAge");
			Integer res = (Integer) getter.invoke(instance);

			System.out.println("Res: " + res);

			Method getterCnt = instance.getClass().getMethod("getCnt");
			Integer res2 = (Integer) getterCnt.invoke(instance);

			System.out.println("Cnt: " + res2);

//			Field[] f = clx.getFields();
//			for (Field field : f) {
//				System.out.println("Field: " + field);
//			}

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
		MethodGen getter = new MethodGen(accessFlags, fg.getType(), new Type[] {}, new String[] {}, createGetterName(fg.getName()), cg.getClassName(), il, cpg);

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
		MethodGen getter = new MethodGen(accessFlags, Type.VOID, new Type[] { fg.getType() }, new String[] { fg.getName() }, createSetterName(fg.getName()),
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
