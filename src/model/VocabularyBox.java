package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.LinkedList;

/**
 * This class represents a learning system for vocabularies. A VocabularyBox
 * consists of (usually) 5 cases with the numbers 0, 1, 2, 3, 4. Case 0 contains
 * those vocabularies you do not know yet while the case with the highest number
 * contains those you (should) know very well. Every time you answer a
 * vocabulary correctly it is put into the next case (respectively it stays in
 * the last case since there is no next case). If your answer was wrong, the
 * vocabulary goes back into case 0.
 * 
 */
public class VocabularyBox implements Serializable {

	private static final long serialVersionUID = -3149647846060599036L;

	private LinkedList<VocabularyCard>[] cases;

	private void checkCaseNo(int caseNo) {
		if (caseNo < 0 || caseNo >= getNumberOfCases())
			throw new IllegalArgumentException(
					"Invalid case number! Must be between 0 and "
							+ (getNumberOfCases() - 1));
	}

	/**
	 * Constructs a new VocabularyBox with size empty cases.
	 * 
	 * @param size
	 *            The number of cases the VocabularyBox consists of.
	 */
	@SuppressWarnings("unchecked")
	public VocabularyBox(int size) {
		if (size <= 0)
			throw new IllegalArgumentException(
					"The number of cases must be greater than 0!");

		cases = new LinkedList[size];
		for (int i = 0; i < size; i++) {
			cases[i] = new LinkedList<VocabularyCard>();
		}
	}

	/**
	 * Constructs a new VocabularyBox with fife empty cases.
	 */
	public VocabularyBox() {
		this(5);
	}

	/**
	 * Deserializes a VocabularyBox from a file.
	 * 
	 * @param path
	 *            Path to the file to deserialize from.
	 * @return A new VocabularyBox object read from <tt>path</tt>.
	 * @throws ClassNotFoundException
	 *             if the class of the serialized object cannot be found
	 * @throws IOException
	 */
	public static VocabularyBox loadFromFile(String path)
			throws ClassNotFoundException, IOException {
		ObjectInputStream ois;
		VocabularyBox ret;

		ois = new ObjectInputStream(new FileInputStream(path));
		try {
			ret = (VocabularyBox) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException occured:\n"
					+ e.getMessage());
		}

		return ret;
	}

	/**
	 * Serializes the VocabularyBox into the given path.
	 * 
	 * @param path
	 *            Path to the file where to store the VocabularyBox in.
	 * @throws FileNotFoundException
	 *             if the given file does not exist
	 */
	public synchronized void storeInFile(String path) throws FileNotFoundException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(path));

			oos.writeObject(this);
			System.out.println("VocabularyBox written to " + path);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException occured:\n"
					+ e.getMessage());
		} finally {
			if(oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Retrieves the number of cases this VocabularyBox consists of.
	 * 
	 * @return The number of cases.
	 */
	public int getNumberOfCases() {
		return cases.length;
	}

	/**
	 * Searches for a vocabulary and returns the number of the containing case
	 * or -1 if none of the cases contains it.
	 * 
	 * @param vocab
	 *            The vocabulary searched for.
	 * @return The number of the case <tt>vocab</tt> were found in or -1 if
	 *         <tt>vocab</tt> was not found in any of the cases.
	 */
	public int find(String vocab) {
		return find(new VocabularyCard(vocab));
	}

	/**
	 * Searches for a vocabulary and returns the number of the containing case
	 * or -1 if none of the cases contains it.
	 * 
	 * @param card
	 *            The vocabulary searched for.
	 * @return The number of the case <tt>vocab</tt> were found in or -1 if
	 *         <tt>vocab</tt> was not found in any of the cases.
	 */
	public int find(VocabularyCard card) {
		for (int i = 0; i < getNumberOfCases(); i++) {
			if (cases[i].contains(card))
				return i;
		}

		return -1;
	}

	/**
	 * Searches for a vocabulary in the given case.
	 * 
	 * @param vocab
	 *            The vocabulary searched for.
	 * @param caseNo
	 *            The number of the case searched in.
	 * @return true if the given case contains <tt>vocab</tt>, false otherwise
	 * @throws IllegalArgumentException
	 *             if <tt>caseNo</tt> is not in [0,size[.
	 */
	public boolean find(String vocab, int caseNo) {
		return find(new VocabularyCard(vocab), caseNo);
	}

	/**
	 * Searches for a vocabulary in the given case.
	 * 
	 * @param card
	 *            The vocabulary searched for.
	 * @param caseNo
	 *            The number of the case searched in.
	 * @return true if the given case contains <tt>vocab</tt>, false otherwise
	 * @throws IllegalArgumentException
	 *             if <tt>caseNo</tt> is not in [0,size[.
	 */
	public boolean find(VocabularyCard card, int caseNo) {
		checkCaseNo(caseNo);

		return cases[caseNo].contains(card);
	}

	/**
	 * Removes a vocabulary from the VocabularyBox if it is contained.
	 * 
	 * @param vocab
	 *            The vocabulary to remove.
	 * @return the removed VocabularyCard or null if it was not found.
	 */
	public VocabularyCard remove(String vocab) {
		return remove(new VocabularyCard(vocab));
	}

	/**
	 * Removes a vocabulary from the VocabularyBox if it is contained.
	 * 
	 * @param card
	 *            The vocabulary to remove.
	 * @return the removed VocabularyCard or null if it was not found.
	 */
	public VocabularyCard remove(VocabularyCard card) {
		System.out.println("VocabularyBox.remove("+card.getEnglish()+")");
		for (int i = 0; i < getNumberOfCases(); i++) {
			for (int j = 0; j < cases[i].size(); j++) {
				if (cases[i].get(j).equals(card))
					return cases[i].remove(j);
			}
		}

		return null;
	}

	/**
	 * Inserts a new vocabulary at the end of case 0 of the VocabularyBox if it
	 * is not contained in any case.
	 * 
	 * @param vocab
	 *            The vocabulary to insert.
	 * @return true if insertion was successful, false otherwise (probably
	 *         because <tt>vocab</tt> is already in the VocabularyBox).
	 */
	public boolean insert(String vocab) {
		if (find(vocab) < 0)
			return cases[0].add(new VocabularyCard(vocab));

		return false;
	}

	/**
	 * Inserts a new vocabulary at the end of case 0 of the VocabularyBox if it
	 * is not contained in any case. Otherwise it will be removed from that
	 * case, its translations will be merged and it will be reinserted into case
	 * 0.
	 * 
	 * @param card
	 *            The vocabulary to insert
	 * @return true if insertion or merging and reinsertion respectively was
	 *         successful, false otherwise
	 */
	public boolean insert(VocabularyCard card) {
		int caseNo = find(card.getEnglish());
		if (caseNo < 0) {
			return cases[0].add(card);
		}
		int index = cases[caseNo].lastIndexOf(card);
		VocabularyCard vc = cases[caseNo].get(index).merge(card);
		cases[caseNo].remove(index);
		return cases[0].add(vc);
	}

	/**
	 * Gets the first vocabulary of the given case or null if that case is
	 * empty.
	 * 
	 * @param caseNo
	 *            The number of the case the vocabulary is taken from.
	 * @return The first vocabulary in the case with the number caseNo or null
	 *         if that case is empty.
	 * @throws IllegalArgumentException
	 *             if <tt>caseNo</tt> is not in [0,size[.
	 */
	public VocabularyCard getNextVocabInCase(int caseNo) {
		checkCaseNo(caseNo);
		if (cases[caseNo].isEmpty())
			return null;
		return cases[caseNo].peek();
	}

	/**
	 * Answers the vocabulary got by getNextVocabInCase(). Answering means it is
	 * removed from the current case and inserted into the next one
	 * (respectively it stays in the last case since there is no next one) if it
	 * was answered correctly or it is put back into case 0 if it was answered
	 * wrong.
	 * 
	 * @param caseNo
	 *            The number of the case the answered vocabulary was taken from.
	 * @param correct
	 *            Must be true if the answer was correct and false otherwise.
	 * @throws NoSuchElementException
	 *             if the given case is empty.
	 * @throws IllegalArgumentException
	 *             if <tt>caseNo</tt> is not in [0,size[.
	 */
	public void answerVocabInCase(int caseNo, boolean correct) {
		checkCaseNo(caseNo);

		if (correct) {
			int nextCase = Math.min(getNumberOfCases() - 1, caseNo + 1);
			cases[nextCase].add(cases[caseNo].remove());
		} else {
			cases[0].add(cases[caseNo].remove());
		}
	}
	
	/**
	 * Similar to answerVocabInCase() but only applicable for case 0.
	 * If the answer is not correct, the card is at most <tt>limit<tt>
	 * positions put back instead of put back to the end.
	 * 
	 * @param limit
	 * 			Maximum number of positions the card is put back.
	 * @param correct
	 * 			Must be true if the answer was correct and false otherwise.
	 * @throws IllegalArgumentException
	 * 			if <tt>limit<tt> is negative.
	 */
	public void answerVocabInCaseZeroWithLimit(int limit, boolean correct) {
		if(limit < 0)
			throw new IllegalArgumentException("limit must be non-negative");
		
		if(correct) {
			cases[Math.min(1, cases.length-1)].add(cases[0].remove());
		} else {
			limit = Math.min(limit, cases[0].size()-1);
			cases[0].add(limit, cases[0].remove());
		}
	}

	/**
	 * Shuffles a case due to learning the vocabularies themselves instead of
	 * their sequence.
	 * 
	 * @param caseNo
	 *            The number of the case to be shuffled.
	 * @throws IllegalArgumentException
	 *             if <tt>caseNo</tt> is not in [0,size[.
	 */
	public void shuffleCase(int caseNo) {
		checkCaseNo(caseNo);

		Collections.shuffle((LinkedList<VocabularyCard>) cases[caseNo]);
	}

	/**
	 * Retrieves an array indicating the amount of vocabularies in each case.
	 * 
	 * @return An array indicating the amount of vocabularies in each case.
	 */
	public int[] getCaseVolumes() {
		int[] res = new int[getNumberOfCases()];
		for (int i = 0; i < res.length; i++) {
			res[i] = cases[i].size();
		}

		return res;
	}

	/**
	 * Retrieves the total amount of all vocabularies in this VocabularyBox.
	 * 
	 * @return The total amount of vocabularies.
	 */
	public int getVolume() {
		int res = 0;
		for (int i = 0; i < getNumberOfCases(); i++) {
			res += cases[i].size();
		}

		return res;
	}

	/**
	 * Creates a new instance of VocabularyBox that contains all vocabularies of
	 * this and die other box. Neither this nor the other box will change their
	 * inner state. If this box has n cases and the other one has m cases, the
	 * resulting box will have max(n,m) cases. All copied vocabularies will be
	 * inserted into their old case number. As an exception of this there is the
	 * case that a vocabulary is contained in both boxes, this and other. In
	 * this case it is inserted only once namely into the lower one of both
	 * cases and their translations will be merged as well.
	 * 
	 * @param other
	 *            The other VocabularyBox this box will be merged with.
	 * @return A new VocabularyBox containing the vocabularies of <tt>this</tt>
	 *         and <tt>other</tt> in their original cases.
	 */
	public VocabularyBox merge(VocabularyBox other) {
		VocabularyBox box = new VocabularyBox(Math.max(this.getNumberOfCases(),
				other.getNumberOfCases()));

		for (int i = 0; i < this.getNumberOfCases(); i++) {
			for (int j = 0; j < this.cases[i].size(); j++) {
				box.cases[i].add(this.cases[i].get(j));
			}
		}

		for (int i = 0; i < other.getNumberOfCases(); i++) {
			for (int j = 0; j < other.cases[i].size(); j++) {
				VocabularyCard card = other.cases[i].get(j);
				int found = box.find(card);
				if (found < 0) { // box does not contain vocab jet
					box.cases[i].add(card);
				} else { // box does already contain vocab, so let's put it into
						 // the lower of both prior cases and merge
					card = box.remove(card).merge(card);
					box.cases[Math.min(found, i)].add(card);
				}
			}
		}

		return box;
	}
}
