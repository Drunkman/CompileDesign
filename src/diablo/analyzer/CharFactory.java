package diablo.analyzer;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * �ַ����������ڰ��������ֽ��һ�����ַ��������Զ�������
 * 
 * @author mgy
 * 
 */
public class CharFactory {
	private String str;
	private int num = 0;// �ַ���һ���ַ����е�λ��
	private BufferedReader reader;// �ṩ��������
	private int rowcount = 0;// ��¼��ǰ����������

	public int getRowcount() {
		return rowcount;
	}

	public CharFactory(BufferedReader reader) {
		this.reader = reader;
		str = getNextLine();
	}

	// �����һ���ַ���ÿ�еĽ�β����' '
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

	// ��ȡ��һ��
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
			System.out.println("�����������");
		}
		return null;
	}
}
