package c4tw.blackjack;

import java.util.ArrayList;

import com.biotools.meerkat.Card;

public class Hand {
	private ArrayList<Card> cards;
	
	public Hand() {
		this.cards = new ArrayList<Card>();
	}
	
	public void addCard(Card c) {
		this.cards.add(c);
	}
	
	public void clear() {
		this.cards.clear();
	}
	
	public String toString() {
		String str = "";
		for (Card c : cards)
			str += c + " ";
		str.trim();
		return str;
	}
	
	public Card getCard(int pos) {
		return this.cards.get(pos);
	}
	
	public int size() {
		return this.cards.size();
	}
}
