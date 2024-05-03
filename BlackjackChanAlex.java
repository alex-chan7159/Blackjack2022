package JavaSummatives;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;

/*
 * Name: Alex Chan
 * Date: May 8, 2023
 * Teacher: Mr Chu
 * Short Desc: 
 * -----Welcome to Blackjack!-----
 * The objective of the game is to draw cards totaling as close as 21, without going over.
 * If your cards total exactly 21 congratulations! Blackjack!
 * If your cards go over 21... uh oh you busted!
 * -"Hit" to draw a new card for your hand.
 * -"Stand" to end your turn and let the dealer play.
 * -"Double down" to double your bet, draw one more card, then stand.
 * -"Split".. if your first two cards are worth the same, you can choose to play them as separate hands! (your bet will be placed again on the second hand)
 * 
 * You win if your hand is closer to 21 than the dealer's, or if the dealer busts!
 * You lose if your hand is farther from 21 than the dealer's, or if you bust.
 * Good luck and have fun!
 */
public class BlackjackChanAlex {

	public static void main(String[] args) throws Exception {
		//intiailizing variables
		double[] highScoresArray = new double[3]; //array to store the scores stored in file
		int HSACounter = 0; //counter to help with array ^
		
		double userBalance = 500; //initializing user's balance (money to spend)
		double[] userWager = {0}; //amount of money user is betting, using an array so I can use pass-by-reference to "return" more than one thing in a method

		String[] userHand = new String[11]; //array to store the cards the user has
		String[] dealerHand = new String[11]; //array to store the cards the user has
		int[] userHandValue = {0}; //value of user hand, pass by reference
		int[] dealerHandValue = {0}; //value of dealer's hand, pass by reference
		int userCardCounter = 0; //how many cards user was dealt (to access things in array)
		int dealerCardCounter = 0; //how many cards dealer was dealt

		String[] cardsDealt = new String[52]; //making an array to keep track of cards dealt
		//placeholders for the string, so that comparison isn't with null datatype
		for (int i = 0; i < 52; i++) {
			cardsDealt[i] = "null"; //populating the array with placeholder string "null" so that .equals does not return null pointer exception
		}
		int[] cardsDealtCounter = {0}; //counter to keep track of cards dealt and also help with filling the array ^, using an array so I can do pass-by-reference (in order to return more than 1 thing in methods)
		String[] gameOutcome = {"null"}; //acts as a boolean, but has 3 different values: "win", "lose", "tie", "null" - using an array so I can do pass-by-reference

		//special variables just for split, replicas of previous ones
		boolean splitOption = false; //using a boolean to keep track of whether split is an option
		boolean splitChosen = false; //using boolean to keep track of if user chose to do the split option
		String[] splitHandOne = new String[11];
		String[] splitHandTwo = new String[11];
		int[] handOneValue = {0}; //value of first hand
		int[] handTwoValue = {0}; //value of second hand
		int handOneCardCounter = 0;
		int handTwoCardCounter = 0;
		String[] handOneOutcome = {"null"};
		String[] handTwoOutcome = {"null"};
		double[] userWagerCopy = {0}; //replica variable of userWager (seperate copy of userWager, if user double downs on one hand, will not affect value of other)

		char gameEnd = 'y'; //variable used to store user input (if they want to continue playing or not)

		Scanner input = new Scanner(System.in); //initializing scanner;
		File file = new File("BlackjackHighScores.txt"); //referencing file
		Scanner in; //initializing scanner for file
		String userChoice = ""; //used with scanner to prompt user for choices (how to play their hand, for split, etc)
		PrintWriter output; //initializing printwriter

		// --- creating scanner for file if it exists, otherwise creates a new file with scores already on it
		try { //try catch to catch error returned if scanner cannot be made on file (does not exist)
			in = new Scanner(file); //creating a scanner for the file to read from it
			System.out.println("Developer: Score file does exist"); //visualizing code

		} catch (Exception x) { //the file does not exist, need to create one and write scores onto it
			System.out.println("Developer: Score file does not exist"); //visualizing code, returns that file does not exist

			output = new PrintWriter(file); //finishing initializing of PrintWriter
			for (int i = 0; i < 3; i++) { //for loop to write empty scores for the new file
				output.println("0"); //writing to file
			}
			output.close(); //closing file after finish writing
			in = new Scanner(file); //now, creating scanner for the file
		}

		//starting menu
		System.out.println(" _______________________________________________________"); //formating divider
		System.out.println("|      ♦️ ♥️ ♣ ♠️ Welcome to Blackjack! ♦️ ♥️ ♣ ♠️            |");
		System.out.println("|_______________________________________________________|"); //formating divider
		System.out.println("Current highscores:"); //msg as part as menu
		//display high scores and store values in array
		while (in.hasNext()) { //checks to see if the next token is available (I assume it only runs for as many tokens there are in the file)
			highScoresArray[HSACounter] = in.nextDouble(); //read scores from file and store in array
			System.out.println("$" + highScoresArray[HSACounter]); //prints the scores stored in array
			HSACounter++; //increment the counter when done reading score
		}

		System.out.println(); //formating space
		do { //this do while contains the entire game, only ends when user chooses to stop playing

			System.out.println("Your current balance: $" + userBalance); //displaying how much money the user has

			//prompting user for money, repeating until valid input
			while (true) {
				try { //try catch to only accept num inputs
					System.out.println("Make your starting bet: "); //prompt msg
					userWager[0] = input.nextDouble(); //getting user input for bet amount
					userWager[0] = (double) Math.round(userWager[0] * 100) / 100; //rounding user bet to 2 decimal places (realistic money, cents)
					if (userWager[0] > userBalance) { //checking if user had enough money for bet placement
						System.out.println("You can't bet more than you have!"); //appropriate error msg
					}
					else if (userWager[0] < 0.01) {//checking if user entered a negative num or too small of a bet
						System.out.println("Invalid input, minimum bet is $0.01"); //appropriate error msg
					}
					else { //otherwise, input is valid
						break; //break while loop
					}
				}
				catch (Exception a) { //if datatype mismatch
					System.out.println("Invalid input, please enter a number instead! :)"); //appropriate error msg
					input.nextLine(); //clearing the scanner so the while loop doesn't run infinitely
				}
			}

			//drawing first two cards for user
			userHand[0] = uniqueCardMethod(cardsDealt, cardsDealtCounter);
			cardsDealtCounter[0]++; //additional card was dealt
			userHand[1] = uniqueCardMethod(cardsDealt, cardsDealtCounter);
			cardsDealtCounter[0]++; //additional card was dealt

			userHandValue[0] = handValueInterpreter(userHand);
			userCardCounter = 2; //two cards in user hand

			//checking to see if the first two cards the user drew have the same value (split option)
			if (cardValueInterpreter(userHand[0], userHandValue[0]) == cardValueInterpreter(userHand[1], userHandValue[0])) { //run when card 1 and card 2 are worth the same
				splitOption = true; //toggle the splitOption boolean as true
			}

			//drawing first two cards for dealer
			dealerHand[0] = uniqueCardMethod(cardsDealt, cardsDealtCounter);
			cardsDealtCounter[0]++; //additional card was dealt
			dealerHand[1] = uniqueCardMethod(cardsDealt, cardsDealtCounter);
			cardsDealtCounter[0]++; //additional card was dealt

			dealerHandValue[0] = handValueInterpreter(dealerHand);
			dealerCardCounter = 2; //dealer has 2 cards

			System.out.println("_______________________________________________________"); //formating divider for interface

			//showing the cards that the user drew
			System.out.println("Your hand...");
			handDisplayer(userHand); //displaying the cards in user hand
			System.out.println(); //formating print statement
			System.out.println("Value of your hand: " + userHandValue[0]);

			//showing the dealer's first card
			System.out.println("Dealer's hand...");
			System.out.println(dealerHand[0] + " __");

			System.out.println("_______________________________________________________"); //formating divider for interface

			//runs if the first 2 cards the user had were automatically blackjack (game ends early)
			if (userHandValue[0] == 21) { 
				System.out.println("Blackjack!"); //blackjack msg
				//showing dealer's hand
				System.out.println("Dealer's hand:");
				System.out.println(dealerHand[0] + " " + dealerHand[1]);
				if (dealerHandValue[0] == userHandValue[0]) {
					System.out.println("Dealer also had blackjack!");
					gameOutcome[0] = "tie"; //it was a tie
				}
				else {
					gameOutcome[0] = "win"; //game was won by user
				}
			}
			else { //the user's first two cards were not blackjack

				//prompting user for their action and running code based on selection

				if (splitOption) { //prompt user for if they'd like to play their hand as split when boolean is set to true :)
					do { //keep asking user if they want to split until appropriate answer
						System.out.println("You have the option to split your hand, wanna? (y/n)"); //prompting user with special msg
						userChoice = input.next(); //getting input
						if (!userChoice.equals("y") && !userChoice.equals("n")) { //error msg if invalid input
							System.out.println("Invalid input, please try again!");
						}
						else if (userChoice.equals("y")) { //otherwise, (is either y or n), user chose yes, set boolean to true
							splitChosen = true;
						}
						else { //user chose no, set to false
							splitChosen = false;
						}
					} while (!userChoice.equals("y") && !userChoice.equals("n")); //keep prompting until valid input

				}

				if (splitChosen == true) { //split version of game
					userWagerCopy[0] = userWager[0]; //making an equal bet on the other hand
					
					//splitting the hand into two split hands
					splitHandOne[0] = userHand[0]; //first card goes into hand one
					splitHandOne[1] = uniqueCardMethod(cardsDealt, cardsDealtCounter); //drawing another card for the first hand
					handOneCardCounter+=2; //two cards were added
					handOneValue[0] = handValueInterpreter(splitHandOne); //getting the value of hand one

					splitHandTwo[0] = userHand[1]; //second card goes into hand two
					splitHandTwo[1] = uniqueCardMethod(cardsDealt, cardsDealtCounter); //drawing another card for the second hand
					handTwoCardCounter+=2; //two cards were added
					handTwoValue[0] = handValueInterpreter(splitHandTwo); //getting the value of hand two

					//--------------finished splitting hands

					//playing the first hand
					//showing the cards that the user drew (first hand)
					System.out.println("          |I  Playing Hand One  I|"); //msg letting user know which hand they are playing
					System.out.println("Your hand...");
					handDisplayer(splitHandOne); //displaying the cards in first hand
					System.out.println(); //formating print statement
					System.out.println("Value of your hand: " + handOneValue[0]);
					System.out.println("_______________________________________________________"); //formating divider for interface
					handOneOutcome[0] = userTurnMethod(userChoice, input, splitHandOne, handOneCardCounter, cardsDealt, cardsDealtCounter, handOneValue, userWager);

					//playing the second hand
					System.out.println("          |II Playing Hand Two II|"); //msg letting user know which hand they are playing
					System.out.println("Your hand...");
					handDisplayer(splitHandTwo); //displaying the cards in second hand
					System.out.println(); //formating print statement
					System.out.println("Value of your hand: " + handTwoValue[0]);
					System.out.println("_______________________________________________________"); //formating divider for interface
					handTwoOutcome[0] = userTurnMethod(userChoice, input, splitHandTwo, handTwoCardCounter, cardsDealt, cardsDealtCounter, handTwoValue, userWagerCopy);

					//dealer's turn condensed in a method (he will play his hand, returns the outcome of the game after dealer is done playing) - passing variables through arguments, important for processes
					dealerTurnMethod(dealerHand, dealerHandValue, dealerCardCounter, cardsDealt, cardsDealtCounter, userHandValue, gameOutcome, splitChosen, handOneValue, handTwoValue, handOneOutcome, handTwoOutcome);

					System.out.println("Updating balance for hand one..");
					userBalance = userBalanceUpdater(handOneOutcome, userBalance, userWager); //this method changes how much money the user has, game results, balance, and wager amount are passed through arguments
					System.out.println("_______________________________________________________"); //formating divider for interface

					System.out.println("Updating balance for hand two..");
					userBalance = userBalanceUpdater(handTwoOutcome, userBalance, userWagerCopy);
				}
				else { // standard blackjack game (no split)
					//user's turn
					gameOutcome[0] = userTurnMethod(userChoice, input, userHand, userCardCounter, cardsDealt, cardsDealtCounter, userHandValue, userWager);

					//dealer's turn condensed in a method (he will play his hand, returns the outcome of the game after dealer is done playing) - passing variables through arguments, important for processes
					dealerTurnMethod(dealerHand, dealerHandValue, dealerCardCounter, cardsDealt, cardsDealtCounter, userHandValue, gameOutcome, splitChosen, handOneValue, handTwoValue, handOneOutcome, handTwoOutcome);

				}

			} //the game did not immediately end with first 2 cards blackjack

			//conclusion of the game
			if (splitChosen == false) { //the default game will update the user's balance only once, unlike split which does it twice during the game
				//updating the user's balance
				userBalance = userBalanceUpdater(gameOutcome, userBalance, userWager); //this method changes how much money the user has, game results, balance, and wager amount are passed through arguments
			}

			if (userBalance == 0) { //end game when user runs out of money (not in debt)
				System.out.println("Oops... looks like you ran out of money T_T"); //appropriate return msg
				break;
			}
			else if (userBalance < 0){ //end the game when user runs out of money, AND GOES IN THE RED
				System.out.println("You.. you're in debt- How did this happen?!?!?");
				break;
			}

			System.out.println("_______________________________________________________"); //formating divider for interface

			//reseting variables and prepare for another game
			userHandValue[0] = 0;
			dealerHandValue[0] = 0;
			userWager[0] = 0;
			userCardCounter = 0;
			dealerCardCounter = 0;
			cardsDealtCounter[0] = 0;
			gameOutcome[0] = "null"; 
			//split variables
			splitOption = false;
			splitChosen = false;
			handOneValue[0] = 0;
			handTwoValue[0] = 0;
			handOneOutcome[0] = "null"; 
			handTwoOutcome[0] = "null"; 
			userWagerCopy[0] = 0;


			for (int i = 0; i < 52; i++) { //clearing the cardsDealt array
				cardsDealt[i] = "null"; //filling each index with "null (so I can use .equals() without NPE )"
			}

			for (int i = 0; i < userHand.length; i++) { //clearing both user and dealer hands (and split hands too)
				userHand[i] = null;
				dealerHand[i] = null;
				splitHandOne[i] = null;
				splitHandTwo[i] = null;
			}

			//prompting user for if they'd like to play again
			do {
				System.out.println("      { Would you like to play again? (y/n) }"); //prompt msg
				gameEnd = input.next().charAt(0); //getting input, casting it as char because .next returns string
				if (gameEnd!='y' && gameEnd!='n') { //return appropriate message if invalid input
					System.out.println("Invalid input, please try again!");
				}
			} while(gameEnd!='y' && gameEnd!='n');

		} while(gameEnd!='n'); // replays game until the user decides to stop playing or until the user runs out of money

		//updating highscores
		highScoreUpdater(highScoresArray, userBalance); //updating the high score array using the procedure, passing the array and the user's final balance

		System.out.println(" ______________________________________________________"); //formating divider for interface
		System.out.println("|          ♣ ♠️ Thanks for Playin'! ♦️ ♥️                 |");
		System.out.println("|______________________________________________________|"); //formating divider for interface
		//creating printwriter again (closed it earlier)
		output = new PrintWriter(file);
		System.out.println("Current highscores:"); //showing updated highscores
		for (int i = 0; i < highScoresArray.length; i++) { //writing the updated high scores to file, saving + printing high scores after game end
			System.out.println("$" + highScoresArray[i]); //showing updated highscores, final results :)
			output.println(highScoresArray[i]); //writing scores onto file with print writer
		}
		output.close(); //closing printwriter, saving changes

	} //end of main method -----------------------------------------------------------------------------------------

	public static String randomCard() { //procedure to generate a random card
		Random randomGen = new Random(); //making random
		String card = "";
		int randomNum = randomGen.nextInt(13) + 1; //generating a random number to decide what number/face the card will be
		int randomSuit = randomGen.nextInt(4) + 1; //generating a random num between 1 and 4 for the suit of the card

		if (randomNum == 11) { //the jack
			card += "J";
		}
		else if (randomNum == 12) { //the queen
			card += "Q";
		}
		else if (randomNum == 13) { //the king
			card += "K"; 
		}
		else if (randomNum == 1) { //an ace!!!
			card += "A";
		}
		else {
			card += randomNum; //otherwise, a number card
		}
		//the suit of the card
		if (randomSuit == 1) { //club
			card += "♣";
		}
		else if (randomSuit == 2) { //diamond
			card += "♦";
		}
		else if (randomSuit == 3) { //heart
			card += "♥";
		}
		else { //spade
			card += "♠";
		}

		return card; //returning the finished card
	}

	//method to interpret the value of a card, hand value is passed through too to determine whether ace is worth 1 or 11
	public static int cardValueInterpreter(String enteredCard, int handValue) { 
		int cardValue = 0; //card's value
		char cardNum; //the first character of the card (string)

		cardNum = enteredCard.charAt(0); //getting the first part of the card, number or face

		if (cardNum == 'J' || cardNum == 'Q' || cardNum == 'K' || (enteredCard.substring(0, 2)).equals("10")) { //value of face cards and 10 (comparing the first two characters and seeing if it's 10)
			cardValue = 10;
		}
		else if (cardNum == 'A') { //value of an ace
			if ((handValue + 11) > 21) { //if the 11 value will cause the hand to bust
				cardValue = 1; //the ace will be worth 1 instead
			}
			else {
				cardValue = 11; //otherwise the ace is worth 11
			}
		}
		else { //number cards (not including 10)
			cardValue = Integer.valueOf(String.valueOf(cardNum)); //otherwise, the card is worth its number
		}

		return cardValue; //returning the interpreted value of the card
	}

	//this method takes in an array (the cards are inside of the array), and returns the value of the hand
	public static int handValueInterpreter(String[] hand) {
		int handValue = 0; //the var to return (value of hand)
		for (int i = 0; i < hand.length; i++) { //for loop to run through every index of the hand array
			if (hand[i] != null) { //validating for null datatype, will not run following code if IS null
				handValue += cardValueInterpreter(hand[i], handValue); //using the cardValueInterpreter method and passing each card through it
			}
		}
		return handValue; //returning the total sum/value of the hand in the end
	}

	//this procedure displays the cards in a hand
	public static void handDisplayer(String[] hand) { //takes in the hand that the procedure will display the cards of
		for (int i = 0; i < hand.length; i++) { //runs through all indexes of the hand array
			if (hand[i] != null) { //checking to see if thing at index is null, will not display it if true
				System.out.print(hand[i] + " "); //displaying the card at the index
			}
		}
	}

	//this method returns a card that has not been drawn yet (probability of cards in a deck), 
	public static String uniqueCardMethod(String[] cardsDealt, int[] cardsDealtCounter) {
		String card = randomCard(); //generating a card
		boolean cardExists = false;
		do { //at least once..
			for (int i = 0; i < cardsDealt.length; i++) { //for loop to read through all indexes in the array, checking for if cards already dealt
				if (cardsDealt[i].equals(card)) { //checking to see if the card exists at the array index
					//System.out.println("Developer: Card was already dealt"); //visualizing
					card = randomCard(); //randomly generating a new card
					cardExists = true; //flipping the boolean from false to true because the card exists
				}
			}

			if (cardExists == false) { //if the boolean is false, aka the card was not dealt
				cardsDealt[cardsDealtCounter[0]] = card; //add the card to the array
				break; //end the do while loop
			}
			cardExists = false; //reset the cardExists boolean
		} while (true); //run until the break that happens after a unique card is generated

		return card;

	}
	//this method returns the results of the game based on the values of the user's hand and the dealer's hand
	public static String gameOutcomeMethod(int[] userHandValue, int[] dealerHandValue) { //value of user and dealer hand passed through parameters
		String gameOutcome = ""; //initializing var, local in method
		if (userHandValue[0] > 21) { //if the user's hand busted, this would only be available during the split variation (gameOutcomeMethod is not used when user busts)
			System.out.println("Hand busted!"); //return msg
			gameOutcome = "lose"; //user losttt!!
		}
		else if (userHandValue[0] > dealerHandValue[0]) { //if user's hand is more than dealer's
			System.out.println("Your hand is worth more! (" + userHandValue[0] + " > " + dealerHandValue[0] + ")"); //return msg
			gameOutcome = "win"; //user wins because of better hand!
		}
		else if (userHandValue[0] < dealerHandValue[0]) { //if dealer's hand beat the user
			System.out.println("Dealer's hand is worth more.. (" + userHandValue[0] + " < " + dealerHandValue[0] + ")"); //return msg
			gameOutcome = "lose"; //user lost
		}
		else { //hand had same value, results in a tie
			System.out.println("Hands are the same value! (" + userHandValue[0] + " = " + dealerHandValue[0] + ")"); //return msg
			gameOutcome = "tie"; //it's a tie
		}
		return gameOutcome; //method finally returns the outcome of the game
	}


	//this method returns appropriate messages after the end of the game, and also makes changes to how much money the user has - outcome of game, money user has, and the size of the bet made is passed through parameters
	public static double userBalanceUpdater(String[] gameOutcome, double userBalance, double[] userWager) {
		if (gameOutcome[0].equals("win")) { //game resulted in win for user
			System.out.println("Congratulations! You won :D");
			System.out.println("$" + userBalance + " + $" + userWager[0] + " = $" + (userBalance+userWager[0])); //displaying calculation
			userBalance += userWager[0]; //adding bet to user balance
		}
		else if (gameOutcome[0].equals("lose")) { //game resulted in lost for user
			System.out.println("You lost! Oh well.. :<");
			System.out.println("$" + userBalance + " - $" + userWager[0] + " = $" + (userBalance-userWager[0])); //displaying calculation
			userBalance -= userWager[0]; //subtracting bet from user balance
		}
		else if (gameOutcome[0].equals("tie")){ //game was a tie, not using else because I want to check for "tie"
			System.out.println("It's a push! (tie) :|");
		}
		userBalance = (double) Math.round(userBalance * 100) / 100; //rounding balance 2 decimal places (realistic money, cents)
		System.out.println("Your current balance: $" + userBalance); //displaying current balance after calc
		return userBalance; //returning the updated user balance
	}

	//this procedure updates the high score array from descending order (greatest to least)
	public static void highScoreUpdater(double highScoresArray[], double finalBalance) { //takes in the highScore array and the final balance from when the user ends the game
		if (finalBalance > highScoresArray[0]) { // if the final balance was higher than the previous high score
			highScoresArray[2] = highScoresArray[1]; //shift score two to score three
			highScoresArray[1] = highScoresArray[0]; //shift previous score one to score two
			highScoresArray[0] = finalBalance; //new highscore is the final balance
		}
		else if (finalBalance > highScoresArray[1]) { //otherwise, if the total roll was not higher than HS1 but higher than HS2:
			highScoresArray[2] = highScoresArray[1]; //shift score two to score three
			highScoresArray[1] = finalBalance; //highscore two is the final balance
		}
		else if (finalBalance > highScoresArray[2]) { //otherwise, if the total roll from that round was only higher than the third highest score
			highScoresArray[2] = finalBalance; //high score three is the final balance
		}
	}

	//method for the user's turn, returns the game outcome after their turn ends either by standing or by busting
	public static String userTurnMethod(String userChoice, Scanner input, String[] userHand, int userCardCounter, String[] cardsDealt, int[] cardsDealtCounter, int[] userHandValue, double[] userWager) {
		String gameOutcome = "null"; //initializing var to return
		do { //do while to prompt user for selections until they bust or stand

			//checking to see if user has blackjack with first two cards, end their turn immediately (blackjack DURING split required)
			if (userCardCounter == 2 && handValueInterpreter(userHand) == 21) {
				System.out.println("Blackjack!");
				break;
			}

			System.out.println("Would you like to hit, stand, or double down? (h/s/dd) ");

			userChoice = input.next();

			if (userChoice.equals("h")) { //hit
				userHand[userCardCounter] = uniqueCardMethod(cardsDealt, cardsDealtCounter); //generating new card and storing it as user's
				System.out.println("You drew a " + userHand[userCardCounter]); //displaying card drawn

				//showing user's hand
				System.out.println("Your hand...");
				handDisplayer(userHand); //displaying the cards in user hand
				userHandValue[0] = handValueInterpreter(userHand); //updating value of user's hand
				System.out.println(); //formating space
				System.out.println("Value of your hand: " + userHandValue[0]); //displaying hand value

				cardsDealtCounter[0]++; //additional card was dealt
				userCardCounter++; //user draws another card

				System.out.println("_______________________________________________________"); //formating divider for interface

				if (userHandValue[0] > 21) { //checking to see if the user busted (they lose)
					System.out.println("Ohh no.. You busted!");
					gameOutcome = "lose";
					return gameOutcome; //returning that the user lost
				}

			}
			else if (userChoice.equals("s")) { //standing
				System.out.println("_______________________________________________________"); //formating divider for interface
				break; //standing
			}
			else if (userChoice.equals("dd")) { //double down
				userHand[userCardCounter] = uniqueCardMethod(cardsDealt, cardsDealtCounter); //generating new card and storing it as user's
				System.out.println("You drew a " + userHand[userCardCounter]); //displaying card drawn

				//showing user's hand
				System.out.println("Your hand...");
				handDisplayer(userHand); //displaying the cards in user hand
				userHandValue[0] = handValueInterpreter(userHand); //updating value of user's hand
				System.out.println(); //formating space
				System.out.println("Value of your hand: " + userHandValue[0]); //displaying hand value

				cardsDealtCounter[0]++; //additional card was dealt
				userCardCounter++; //user draws another card

				userWager[0]*= 2; //doubling user wager because dd
				System.out.println("Double down! Your wager is now: $" + userWager[0]); //double down!! user wager is doubled msg

				System.out.println("_______________________________________________________"); //formating divider for interface

				if (userHandValue[0] > 21) { //checking to see if the user busted (they lose)
					System.out.println("Ohh no.. You busted!");
					gameOutcome = "lose";
					return gameOutcome; //returning that the user lost
				}
				break; //standing
			}

			else {
				System.out.println("Invalid input, please try again!");
			}

		} while (userHandValue[0] <= 21); //keep prompting user until they bust (or stand, breaks)

		return gameOutcome; //return "null" as game outcome, user did not bust and ended their turn

	}

	//procedure for the dealer's turn, but returns the game outcome (using pass by reference)
	public static void dealerTurnMethod(String[] dealerHand, int[] dealerHandValue, int dealerCardCounter, String[] cardsDealt, int[] cardsDealtCounter, int[] userHandValue, String[] gameOutcome, boolean splitChosen, int[] handOneValue, int[] handTwoValue, String[] handOneOutcome, String[] handTwoOutcome) {
		System.out.println("Flipping dealer's other card..");

		//showing the cards that the dealer drew
		System.out.println("Dealer's hand...");
		System.out.println(dealerHand[0] + " " + dealerHand[1]); //first and second card
		System.out.println("Value of dealer's hand: " + dealerHandValue[0]); //displaying hand value
		System.out.println("_______________________________________________________"); //formating divider for interface

		if (!gameOutcome[0].equals("lose")) { //if the user already busted, skip dealer turn and go to results
			while (dealerHandValue[0] < 17) { //the dealer will hit until their hand is worth 17 or more (casino rules), or if their hand is already worth more than user's
				if (dealerHandValue[0] > userHandValue[0]) { //end dealer's turn early if their hand is worth more than user's(stop hitting needlessly)
					break;
				}
				dealerHand[dealerCardCounter] = uniqueCardMethod(cardsDealt, cardsDealtCounter); //generating new card and storing it as dealers's
				System.out.println("Dealer hits! Drew a " + dealerHand[dealerCardCounter]); //displaying card drawn

				//showing dealers's hand
				System.out.println("Dealer's hand...");
				handDisplayer(dealerHand); //displaying the cards in user hand
				dealerHandValue[0] = handValueInterpreter(dealerHand); //updating value of dealer's hand
				System.out.println(); //formating space
				System.out.println("Value of dealer's hand: " + dealerHandValue[0]); //displaying hand value

				cardsDealtCounter[0]++; //additional card was dealt
				dealerCardCounter++; //dealer draws another card

				if (dealerHandValue[0] > 21) { //checking to see if the dealer busts (user will win)
					System.out.println("Dealer busted!");
					if (splitChosen) { //updating split variables if playing split
						if (!handOneOutcome[0].equals("lose")) { //only change outcome to win if the hand did not bust
							handOneOutcome[0] = "win";
						}
						if (!handTwoOutcome[0].equals("lose")) { //only change outcome to win if the hand did not bust
							handTwoOutcome[0] = "win";
						}
					}
					else { //updating regular blackjack outcome var
						gameOutcome[0] = "win";
					}
					break; //ends dealer turn because they busted
				}

				System.out.println("_______________________________________________________"); //formating divider for interface
			}

			//dealer is satisfied with hand, they stand
			if (dealerHandValue[0] <= 21) { //do not return this message if the while loop ended because they busted
				System.out.println("Dealer stands."); //dealer's move

				//game outcome, neither player or dealer busted, game is decided by who's hand is closer to 21
				if (splitChosen) { //updating split variables if playing split
					System.out.print("First hand: ");
					handOneOutcome[0] = gameOutcomeMethod(handOneValue, dealerHandValue); //this method returns the outcome of the game by comparing hand values

					System.out.print("Second hand: ");
					handTwoOutcome[0] = gameOutcomeMethod(handTwoValue, dealerHandValue); //this method returns the outcome of the game by comparing hand values
				}
				else { //updating regular blackjack outcome var
					gameOutcome[0] = gameOutcomeMethod(userHandValue, dealerHandValue); //this method returns the outcome of the game by comparing hand values, storing in gameOutcome var
				}

			}

			System.out.println("_______________________________________________________"); //formating divider for interface

		} //end bracket dealer's turn, user did not immediately lose

	}

}
