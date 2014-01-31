package c4tw.blackjack;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Deck;

public class BlackjackGame {
	
	public static void main(String[] args) {
		BlackjackGame game = new BlackjackGame();
		for (int i=0; i<20; i++) {
//			while (game.getScore() < 17)
//				game.hitPlayer();
			game.standPlayer();
			game.printStatus();
			game.newGame();
		}
	}

	private int gameId;
	private Deck deck;
	private Hand[] cards; // all the players, 0 = dealer
	private boolean[] won;
	private boolean finished;
	
	public BlackjackGame() {
		this(2);
	}
	
	private BlackjackGame(int numPlayers) {
		
		// init the game and deck
		this.gameId = 0;
		this.deck = new Deck();
		
		// initialize all player's hands
		this.cards = new Hand[numPlayers];
		this.won = new boolean[numPlayers];
		for (int i = 0; i < this.cards.length; i++) {
			this.cards[i] = new Hand();
			this.won[i] = true;
		}
				
		newGame();
	}
	
	public void newGame() {
		
		// start a new game
		this.gameId++;
		this.finished = false;
		
		// initialize deck
		this.deck.shuffle();
		
		// reset player's hands
		for (int i = 0; i < this.cards.length; i++) {
			this.cards[i].clear();
			this.won[i] = true;
		}
		
		// deal the cards
		deal();
	}
	
	private void deal() {
		// go round and round until everybody has two cards
		for (int i = 0; i < 2; i++)
			for (Hand h : cards)
				h.addCard(deck.deal());
	}
	
	public void hitPlayer() {
		hit(1);
	}
	
	public void standPlayer() {
		stand(1);
	}
	
	private void hit(int playerIndex) {
		
		if (finished || !won[playerIndex]) {
			// not allowed to act anymore
			return;
		}
		
		// hit the player with another card from the deck
		cards[playerIndex].addCard(deck.deal());
		
		// if he busts, game is over
		if (isPlayerBusted(playerIndex)) {
			won[playerIndex] = false;
			
			// if he's also the last player, let the dealer act
			if (playerIndex+1 == cards.length)
				actDealer();
		}
	}
	
	private void stand(int playerIndex) {
		
		if (finished || !won[playerIndex]) {
			// not allowed to act anymore
			return;
		}
		
		// mark player as done
		won[playerIndex] = false;
		
		// if he's the last player, let the dealer act
		if (playerIndex+1 == cards.length)
			actDealer();
	}
	
	private void actDealer() {
		
		// dealer only hits if he doesn't have everyone beat already
		boolean beatsAll = false;
		while (!beatsAll && getScoreForPlayer(0) < 17) {
			beatsAll = true;
			int dealerScore = getScoreForPlayer(0);
			for (int i = 1; i < cards.length; i++) {
				if (!isPlayerBusted(i) && dealerScore < getScoreForPlayer(i)) {
					beatsAll = false;
					break;
				}
			}
			
			if (!beatsAll)
				cards[0].addCard(deck.deal());
		}
		
		// if the dealer is busted, everyone that didn't bust yet wins, otherwise compare scores
		if (isPlayerBusted(0)) {
			// mark all players that didn't bust as winners
			won[0] = false;
			// mark everyone that didn't bust yet as a winner
			for (int i = 1; i < cards.length; i++) {
				won[i] = !isPlayerBusted(i);
			}
		} else {
			// compare all non busted players' scores with the dealer's score
			for (int i = 1; i < cards.length; i++) {
				won[i] = !isPlayerBusted(i) && (getScoreForPlayer(0) < getScoreForPlayer(i));
			}
		}
		
		// now mark as finished
		finished = true;
	}
	
	private boolean isPlayerBusted(int playerIndex) {
		return getScoreForPlayer(playerIndex) > 21;
	}
	
	public int getScore() {
		return getScoreForPlayer(1);
	}
	
	private int getScoreForPlayer(int playerIndex) {
		// keep track of number of aces
		int numAces = 0;
		int score = 0;
		
		// loop over all cards in the hand and add scores together
		for (int i = 0; i < cards[playerIndex].size(); i++) {
			int rank = cards[playerIndex].getCard(i).getRank();
			if (rank <= Card.NINE) {
				score += rank + 2; // ranks are encoded 0=TWO ... 7=NINE
			} else if (rank >= Card.TEN && rank <= Card.KING) {
				score += 10;
			} else if (rank == Card.ACE){
				score += 1; // add just 1 for now, then add 10 while possible later
				numAces++;
			}
		}
		
		// now we have the minimum score, let's get the max without busting
		while (score <= 11 && numAces > 0) {
			score += 10; // use 11 value for this ace
			numAces--;
		}
		
//		System.out.println(cards[playerIndex].flashingString() + ": " + score);
		return score;
	}
	
	public String getStatus() {
		String status = "";
		status += "## GAME: " + gameId + "\n";
		status += "Dealer: " + cards[0].getCard(0);
		if (finished) {
			for (int c = 1; c < cards[0].size(); c++) {
				status += " " + cards[0].getCard(c);
			}
			if (finished)
				status += " - Score: " + getScoreForPlayer(0);
		}
		status += "\n";
		for (int i = 1; i < cards.length; i++) {
			status += "Player " + i + ":";
			for (int c = 0; c < cards[i].size(); c++) {
				status += " " + cards[i].getCard(c);
			}
			if (finished)
				status += " - Score: " + getScoreForPlayer(i);
			status += "\n";
		}
		if (finished) {
			status += "Player Wins:";
			for (int i = 1; i < cards.length; i++) {
				status += " " + won[i];
			}
		}
		return status;
	}
	
	public void printStatus() {
		System.out.println(getStatus());
	}
}
