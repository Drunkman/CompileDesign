package diablo.model;

/**
 * ��Ԫʽ�����ݽṹ
 * 
 * @author mgy
 * 
 */
public class Fouryuan {
	public String op, arg1, arg2, result;// ��Ԫʽ����������������������
	public static int count = 1;// ��¼��ԪʽĿǰ����
	public int num;// ����Ԫʽ������

	public Fouryuan(String op, String arg1, String arg2, String result) {
		this.num = count++;
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.result = result;
	}

	public Fouryuan(String op, String arg1, String arg2) {
		this.num = count++;
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.result = num + 3 + "";
	}

	// ��Ԫʽ����ʾ��ʽ
	@Override
	public String toString() {
		return num + "  (" + op + "," + arg1 + "," + arg2 + "," + result + ")";
	}

	// ������ת��
	public void setResult(String s) {
		this.result = s;
	}

	// �õ���ǰ����
	public static int getLineNum() {
		return count;
	}

	// ��������
	public static void clear() {
		count = 1;
	}
}
