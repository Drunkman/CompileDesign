package diablo.model;

/**
 * �ʷ����������ݽṹ
 * 
 * @author mgy
 * 
 */
public class Item {
	public String type, value;// ���������������ͺ�ֵ
	public int rownum;// ��������������������

	public Item(String type, String value, int rownum) {
		this.type = type;
		this.value = value;
		this.rownum = rownum;
	}

	// �Ƿ��Ǳ�ʶ��
	public boolean isIdentifier() {
		return type.equals("identifier");
	}

	// �Ƿ�������
	public boolean isNum() {
		return type.equals("number");
	}

	// �Ƿ��ǹ�ϵ�����
	public boolean isRelational() {
		return value.equals("=") || value.equals("#") || value.equals("<")
				|| value.equals("<=") || value.equals(">")
				|| value.equals(">=");
	}

	// �Ƿ��Ǽӷ������
	public boolean isAdd() {
		return value.equals("+") || value.equals("-");
	}

	// �Ƿ��ǳ˷������
	public boolean isMul() {
		return value.equals("*") || value.equals("/");
	}

	// ÿһ��������ʽ
	@Override
	public String toString() {
		return "<" + type + ",\"" + value + "\">";
	}

}
