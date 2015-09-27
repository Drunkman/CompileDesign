package diablo.analyzer;

import java.util.ArrayList;

import diablo.model.Item;

/**
 * 
 * �Դʷ����������صĽ�������д���
 * 
 * @author mgy
 * 
 */
public class ItemFactory {
	private ArrayList<Item> items;
	private int count = 0;// ��¼��ǰItemλ��

	public ItemFactory(ArrayList<Item> items) {
		this.items = items;
	}

	// �����һ��Item
	public Item getNextItem() {
		if (count >= items.size()) {
			return null;
		}
		Item item = items.get(count);
		count++;
		return item;
	}

	// Ԥ����һ��Item
	public Item next() {
		if (count >= items.size()) {
			return null;
		}
		Item item = items.get(count);
		return item;
	}

	// ��õ�ǰItem
	public Item getItem() {
		return items.get(count - 1);
	}
}
