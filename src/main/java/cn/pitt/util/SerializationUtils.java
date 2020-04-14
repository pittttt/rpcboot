package cn.pitt.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Java内置序列化工具类
 * 
 * 对象必须实现序列化接口
 */
public class SerializationUtils {

	private SerializationUtils() {
		throw new UnsupportedOperationException("不支持创建实例！");
	}

	/**
	 * 序列化（对象 -> 字节数组）
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		// 定义一个字节数组输出流
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 对象输出流
		ObjectOutputStream out = new ObjectOutputStream(os);
		// 将对象写入到字节数组输出，进行序列化
		out.writeObject(obj);
		return os.toByteArray();
	}

	/**
	 * 反序列化（字节数组 -> 对象）
	 * 
	 * @param data
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
		// 字节数组输入流
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		// 执行对象反序列化，从流中读取对象
		return new ObjectInputStream(is).readObject();
	}
}