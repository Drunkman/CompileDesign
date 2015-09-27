package diablo.analyzer;

import java.util.ArrayList;

import diablo.model.Item;

/**
 * 
 * 对词法分析所返回的结果集进行处理
 * 
 * @author mgy
 * 
 */
public class ItemFactory {
	private ArrayList<Item> items;
	private int count = 0;// 记录当前Item位置

	public ItemFactory(ArrayList<Item> items) {
		this.items = items;
	}

	// 获得下一个Item
	public Item getNextItem() {
		if (count >= items.size()) {
			return null;
		}
		Item item = items.get(count);
		count++;
		return item;
	}

	// 预读下一个Item
	public Item next() {
		if (count >= items.size()) {
			return null;
		}
		Item item = items.get(count);
		return item;
	}

	// 获得当前Item
	public Item getItem() {
		return items.get(count - 1);
	}
}
