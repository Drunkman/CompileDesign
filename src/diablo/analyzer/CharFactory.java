package diablo.analyzer;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 字符工厂，用于把输入流分解成一个个字符，交给自动机分析
 * 
 * @author mgy
 * 
 */
public class CharFactory {
	private String str;
	private int num = 0;// 字符在一条字符串中的位置
	private BufferedReader reader;// 提供的输入流
	private int rowcount = 0;// 记录当前读到的行数

	public int getRowcount() {
		return rowcount;
	}

	public CharFactory(BufferedReader reader) {
		this.reader = reader;
		str = getNextLine();
	}

	// 获得下一个字符，每行的结尾返回' '
	public Character getNextChar() {
		if (str == null)
			return null;
		if (num >= str.length()) {
			if (num == str.length()) {
				str = getNextLine();
				num = 0;
				return ' ';
			}
			str = getNextLine();
			num = 0;
		}
		if (str == null)
			return null;
		return str.charAt(num++);
	}

	// 获取下一行
	private String getNextLine() {
		String s;
		try {
			if ((s = reader.readLine()) != null) {
				rowcount++;
				return s;
			} else
				return null;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("数据输出错误");
		}
		return null;
	}
}
