package diablo.analyzer;

import java.util.ArrayList;

import diablo.model.Fouryuan;
import diablo.model.Item;

/**
 * �﷨�����������
 * 
 * @author mgy
 * 
 */
public class GrammerAnalyzer {
	private ArrayList<Item> items;// �ʷ������Ľ����
	private ArrayList<Fouryuan> fouryuans;// ��Ԫʽ����
	private ArrayList<String> consts, vars;// �����ͱ����ļ���
	private ArrayList<String> errors;// ����ļ���
	private ItemFactory itemFactory;// Item����
	private int count = 0;// ��Ϊ���ɵ�t�������±�

	public GrammerAnalyzer(ArrayList<Item> items) {
		this.items = items;
		itemFactory = new ItemFactory(items);
		fouryuans = new ArrayList<Fouryuan>();
		consts = new ArrayList<String>();
		vars = new ArrayList<String>();
		errors = new ArrayList<String>();
	}

	// ����
	public ArrayList analysis() {
		block();
		if (next() != null && next().value.equals(".")) {
			getNext();
			System.out.println("�﷨��������");
		} else {
			errors.add(getItem().rownum + "��:ȱ�� .");
		}
		// ��������Ľ���д��󣬷���errors
		if (errors.isEmpty())
			return fouryuans;
		// ���û�д��󣬷�����Ԫʽ�ļ���
		else
			return errors;
	}

	// �ֳ���
	private void block() {
		while (next() != null) {
			Item item = next();
			if (item.value.equals("const")) {
				constPart();
			} else if (item.value.equals("var")) {
				varPart();
			} else if (item.value.equals("procedure")) {
				procePart();
			} else {
				break;
			}
		}
		if (next() != null)
			statement();
	}

	// ���
	private void statement() {
		Item item = next();// Ԥ��һλ
		if (item == null)
			return;
		if (item.isIdentifier()) {
			assignment();
		} else if (item.value.equals("if")) {
			conditionstate();
		} else if (item.value.equals("begin")) {
			compound();
		} else if (item.value.equals("while")) {
			loop();
		} else if (item.value.equals("read")) {
			read();
		} else if (item.value.equals("write")) {
			write();
		} else {
			return;
		}
	}

	// �������
	private void conditionstate() {
		getNext();
		condition();
		fouryuans.add(new Fouryuan("=", "false", "_", "t" + count++));
		Fouryuan fy = new Fouryuan("jp", "_", "_");// �����������ת���
		fouryuans.add(fy);
		if (next() != null && next().value.equals("then")) {
			getNext();
			statement();
			int i = getLineNum();
			fy.setResult(i + "");// �ڴ�Ӧ���˻�����������
		} else {
			errors.add(getItem().rownum + "��:ȱ��then");
		}
	}

	// ����
	private void condition() {
		Item item = next();
		if (item != null
				&& (item.isAdd() || item.isIdentifier() || item.isNum() || item.value
						.equals(")"))) {
			String arg1 = expression();
			if (next() != null && next().isRelational()) {
				String op = "j" + getNext().value;
				String arg2 = expression();
				fouryuans.add(new Fouryuan(op, arg1, arg2));// ������ȷ����ת���
			} else {
				errors.add(getItem().rownum + "��:ȱ�ٹ�ϵ�����");
			}
		} else {
			errors.add(getItem().rownum + "��:����������");
		}
	}

	// д���
	private void write() {
		getNext();
		if (next() != null && next().value.equals("(")) {
			getNext();
			expression();
			while (next() != null && next().value.equals(",")) {
				getNext();
				expression();
			}
			if (next() != null && next().value.equals(")")) {
				getNext();
			} else {
				errors.add(getItem().rownum + "��:ȱ�� )");
			}
		} else {
			errors.add(getItem().rownum + "��:ȱ�� (");
		}
	}

	// �����
	private void read() {
		getNext();
		if (next() != null && next().value.equals("(")) {
			getNext();
			identity();
			while (next() != null && next().value.equals(",")) {
				getNext();
				identity();
			}
			if (next() != null && next().value.equals(")")) {
				getNext();
			} else {
				errors.add(getItem().rownum + "��:ȱ�� )");
			}
		} else {
			errors.add(getItem().rownum + "��:ȱ�� )");
		}
	}

	// ����ѭ�����
	private void loop() {
		getNext();
		condition();
		fouryuans.add(new Fouryuan("=", "false", "_", "t" + count++));
		Fouryuan fy = new Fouryuan("jp", "_", "_");// �����������ת���
		int j = fy.num - 2;
		fouryuans.add(fy);
		if (next() != null && next().value.equals("do")) {
			getNext();
			statement();
			fouryuans.add(new Fouryuan("jp", "_", "_", j + ""));
			int i = getLineNum();
			fy.setResult(i + "");// �ڴ�Ӧ���˻�����������
		} else {
			errors.add(getItem().rownum + "��:ȱ�� do");
		}
	}

	// �������
	private void compound() {
		getNext();
		statement();
		while (next() != null && next().value.equals(";")) {
			getNext();
			statement();
		}
		if (next() != null && next().value.equals("end")) {
			getNext();
		} else {
			errors.add(getItem().rownum + "��:ȱ��end");
		}
	}

	// ��ֵ���
	private void assignment() {
		String result = getNext().value;
		if (isConst(result)) {
			errors.add(getItem().rownum + "��:���������ٶ���");
		}
		if (!isVar(result)) {
			errors.add(getItem().rownum + "��:�޴˱���");
		}
		if (next() != null && next().value.equals(":=")) {
			String op = getNext().value;
			String arg1 = expression();
			fouryuans.add(new Fouryuan(op, arg1, "_", result));// ��ֵ������Ԫʽ
		} else {
			errors.add(getItem().rownum + "��:ȱ�� :=");
		}
	}

	// ���ʽ�����ر��ʽ���Ľ��
	private String expression() {
		String sign = "";
		if (next() != null && next().isAdd()) {
			sign = getNext().value;
		}
		String arg1 = nape();
		while (next() != null && next().isAdd()) {
			String op = getNext().value;
			String arg2 = nape();
			String result = "t" + count++;
			fouryuans.add(new Fouryuan(op, arg1, arg2, result));
			arg1 = result;
		}
		return arg1;
	}

	// ��
	private String nape() {
		String arg1 = factor();
		while (next() != null && next().isMul()) {
			String op = getNext().value;
			String arg2 = factor();
			String result = "t" + count++;
			fouryuans.add(new Fouryuan(op, arg1, arg2, result));
			arg1 = result;
		}
		return arg1;
	}

	// ����
	private String factor() {
		Item item = next();
		if (item != null && item.isIdentifier()) {
			return getNext().value;
		} else if (item != null && item.isNum()) {
			return getNext().value;
		} else if (item != null && item.value.equals("(")) {
			getNext();
			String s = expression();
			if (next() != null && next().value.equals(")")) {
				getNext();
			} else {
				errors.add(getItem().rownum + "��:ȱ�� )");
			}
			return s;
		} else {
			errors.add(getItem().rownum + "��:���ӳ���");
			return null;
		}
	}

	// ����˵������
	private void procePart() {
		processPre();
		block();
		if (next() != null && next().value.equals(";")) {
			getNext();
			if (next() != null && next().value.equals("procedure")) {
				getNext();
				procePart();
			}
		} else {
			errors.add(getItem().rownum + "��:ȱ�١�;��");
		}
	}

	// �����ײ�
	private void processPre() {
		if (next() != null && next().value.equals("procedure")) {
			getNext();
			if (next() != null && next().isIdentifier()) {
				getNext();
				if (next() != null && next().value.equals(";")) {
					getNext();
					return;
				} else {
					errors.add(getItem().rownum + "��:ȱ�� ;");
				}
			} else {
				errors.add(getItem().rownum + "��:ȱ�ٱ�ʶ��");
			}
		} else {
			errors.add(getItem().rownum + "��:ȱ�� procedure");
		}
	}

	// ����˵������
	private void varPart() {
		getNext();
		String s = identity();
		if (s == null)
			return;
		if (!isVar(s) && !isConst(s)) {
			vars.add(s);
		} else {
			errors.add(getItem().rownum + "��:�ظ��������");
		}
		while (next() != null && next().value.equals(",")) {
			getNext();
			String ss = identity();
			if (ss == null)
				return;
			if (!isVar(ss) && !isConst(ss)) {
				vars.add(ss);
			} else {
				errors.add(getItem().rownum + "��:�ظ�����ͬ������");
			}
		}
		if (next() != null && next().value.equals(";")) {
			getNext();
		} else {
			errors.add(getItem().rownum + "��:ȱ�� ;");
		}
	}

	// ����˵������
	private void constPart() {
		getNext();
		constDefinition();
		while (next() != null && next().value.equals(",")) {
			getNext();
			constDefinition();
		}
		if (next() != null && next().value.equals(";")) {
			getNext();
		} else {
			errors.add(getItem().rownum + "��:ȱ�١�;��");
		}
	}

	// ��������
	private String constDefinition() {
		String op = null, arg1 = null, result = null;
		if (next() != null && next().isIdentifier()) {
			result = getNext().value;
			if (!isConst(result) && !isVar(result)) {
				consts.add(result);
			} else {
				errors.add(getItem().rownum + "��:���������ٶ���");
			}
		} else {
			errors.add(getItem().rownum + "��:�����������");
		}
		if (next() != null && next().value.equals("=")) {
			op = getNext().value;
		} else {
			errors.add(getItem().rownum + "��:�����������");
		}
		if (next() != null && next().isNum()) {
			arg1 = getNext().value;
		} else {
			errors.add(getItem().rownum + "��:�����������");
		}
		if (op != null && arg1 != null && result != null) {
			fouryuans.add(new Fouryuan(op, arg1, "_", result));
			return result;
		} else {
			return null;
		}
	}

	// ��ʶ��
	private String identity() {
		if (next() != null && next().isIdentifier()) {
			return getNext().value;
		} else {
			errors.add(getItem().rownum + "��:ȱ�� ��ʶ��");
			return null;
		}
	}

	// ��ȡ��һ��item
	private Item getNext() {
		return itemFactory.getNextItem();
	}

	// Ԥ����һ��item
	private Item next() {
		return itemFactory.next();
	}

	// ��õ�ǰItem
	private Item getItem() {
		return itemFactory.getItem();
	}

	// �Ƿ��Ѿ������ڱ�������
	private boolean isVar(String s) {
		for (String var : vars) {
			if (var.equals(s))
				return true;
		}
		return false;
	}

	// �Ƿ��Ѿ������ڳ�������
	private boolean isConst(String s) {
		for (String cons : consts) {
			if (cons.equals(s))
				return true;
		}
		return false;
	}

	// ��ǰ��Ԫʽ�ı��
	private int getLineNum() {
		return Fouryuan.getLineNum();
	}
}
