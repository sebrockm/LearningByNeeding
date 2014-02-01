package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.LinkedList;



/**
 * This class represents a learning system for vocabularies. A VocabularyBox consists of (usually) 5 cases with the
 * numbers 0, 1, 2, 3, 4. Case 0 contains those vocabularies you do not know yet while the case with the highest number contains those
 * you (should) know very well. Every time you answer a vocabulary correctly it is put into the next case
 * (respectively it stays in the last case since there is no next case). If your answer was wrong, the vocabulary goes
 * back into case 0. 
 * 
 */
public class VocabularyBox implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3149647846060599036L;

	/**
	 * 
	 * Inner class to represent a vocabulary card.
	 * A VocabularyCard contains a vocab in the foreign language and a list of translations of it.
	 *
	 */
	public class VocabularyCard implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2500453800565619402L;
		private String english;
		private List<String[]> germans;
		
		/**
		 * Creates a new VocabularyCard with a foreign language vocab and an empty list of translations
		 * @param english The vocab in the foreign language
		 */
		public VocabularyCard(String english)
		{
			this.english = english;
			this.germans = new LinkedList<String[]>();
		}
		
		/**
		 * Adds a new translation to this card.
		 * @param german translation to add
		 * @return false if the card already contains this translation, true if successfully added
		 */
		public boolean addGerman(String[] german)
		{
			if(!germans.contains(german))
			{
				germans.add(german);
				return true;
			}
			return false;
		}
		
		/**
		 * Removes a translation from this card.
		 * @param german translation to remove
		 * @return false if the card does not contain this translation, true if successfully removed
		 */
		public boolean removeGerman(String[] german)
		{
			return germans.remove(german);
		}
		
		/**
		 * Removes the translation at the given index.
		 * @param index index of the translation in the list
		 * @return true if successfully returned, false otherwise
		 */
		public boolean removeGerman(int index)
		{
			return germans.remove(index) != null;
		}
		
		@Override
		public boolean equals(Object other)
		{
			if(other instanceof VocabularyCard)
			{
				VocabularyCard c = (VocabularyCard)other;
				return this.english.equals(c.english);
			}
			return false;
		}
		
		public String getEnglish()
		{
			return english;
		}
		
		public List<String[]> getGermans()
		{
			return germans;
		}
	}

	
	private Queue<VocabularyCard>[] cases;
	
	private void checkCaseNo(int caseNo)
	{
		if(caseNo < 0 || caseNo >= getNumberOfCases())
			throw new IllegalArgumentException("Invalid case number! Must be between 0 and " + (getNumberOfCases()-1));
	}

	/**
	 * Constructs a new VocabularyBox with size empty cases.
	 * @param size The number of cases the VocabularyBox consists of.
	 */
	@SuppressWarnings("unchecked")
	public VocabularyBox(int size)
	{
		if(size <= 0)
			throw new IllegalArgumentException("The number of cases must be greater than 0!");

		cases = new Queue[size];
		for(int i=0; i<size; i++)
		{
			cases[i] = new LinkedList<VocabularyCard>();
		}
	}
		
	/**
	 * Constructs a new VocabularyBox with fife empty cases.
	 */
	public VocabularyBox()
	{
		this(5);
	}
	
	/**
	 * Deserializes a VocabularyBox from a file.
	 * 
	 * @param path Path to the file to deserialize from.
	 * @return A new VocabularyBox object read from <tt>path</tt>.
	 * @throws ClassNotFoundException if the class of the serialized object cannot be found
	 * @throws IOException 
	 */
	public static VocabularyBox loadFromFile(String path) throws ClassNotFoundException, IOException
	{
		ObjectInputStream ois;
		VocabularyBox ret;
		
		ois = new ObjectInputStream(new FileInputStream(path));
		try {	
			ret = (VocabularyBox)ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException occured:\n" + e.getMessage());
		}
		
		return ret;
	}
	
	/**
	 * Serializes the VocabularyBox into the given path.
	 * 
	 * @param path Path to the file where to store the VocabularyBox in.
	 * @throws FileNotFoundException if the given file does not exist
	 */
	public void storeInFile(String path) throws FileNotFoundException
	{
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(path));
			
			oos.writeObject(this);
			
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException occured:\n" + e.getMessage());
		}
	}
	
	/**
	 * Retrieves the number of cases this VocabularyBox consists of.
	 * 
	 * @return The number of cases.
	 */
	public int getNumberOfCases()
	{
		return cases.length;
	}
	
	/**
	 * Searches for a vocabulary and returns the number of the containing case or -1 if
	 * none of the cases contains it.
	 * 
	 * @param vocab The vocabulary searched for.
	 * @return The number of the case <tt>vocab</tt> were found in or -1 if <tt>vocab</tt> was not found in any of the cases.
	 */
	public int find(String vocab)
	{
		for(int i=0; i<getNumberOfCases(); i++)
		{
			if(cases[i].contains(new VocabularyCard(vocab)))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Searches for a vocabulary in the given case.
	 * 
	 * @param vocab The vocabulary searched for.
	 * @param caseNo The number of the case searched in.
	 * @return true if the given case contains <tt>vocab</tt>, false otherwise
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[. 
	 */
	public boolean find(String vocab, int caseNo)
	{
		checkCaseNo(caseNo);
		
		return cases[caseNo].contains(new VocabularyCard(vocab));
	}
	
	/**
	 * Removes a vocabulary from the VocabularyBox if it is contained.
	 * @param vocab The vocabulary to remove.
	 * @return true if the removal was successful, false otherwise i.e. <tt>vocab</tt> was not found in the VocabularyBox.
	 */
	public boolean remove(String vocab)
	{
		for(int i=0; i<getNumberOfCases(); i++)
		{
			if(cases[i].remove(new VocabularyCard(vocab)))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Inserts a new vocabulary at the end of case 0 of the VocabularyBox if it is not contained in any case.
	 * 
	 * @param vocab The vocabulary to insert.
	 * @return true if insertion was successful, false otherwise (probably because <tt>vocab</tt> is already in the VocabularyBox).
	 */
	public boolean insert(String vocab)
	{
		if(find(vocab) < 0)
			return cases[0].add(new VocabularyCard(vocab));
		
		return false;
	}
	
	public boolean insert(VocabularyCard card)
	{
		return cases[0].add(card);
	}
	
	/**
	 * Gets the first vocabulary of the given case or null if that case is empty.
	 * 
	 * @param caseNo The number of the case the vocabulary is taken from.
	 * @return The first vocabulary in the case with the number caseNo or null if that case is empty.
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[.
	 */
	public String getNextVocabInCase(int caseNo)
	{
		checkCaseNo(caseNo);
		if(cases[caseNo].isEmpty())
			return null;
		return cases[caseNo].peek().getEnglish();
	}
	
	/**
	 * Gets a list of translations of the first vocabulary of the given case or null if that case is empty.
	 * @param caseNo the number of the case
	 * @return a list of translations of the first vocabulary in the case with number caseNo or null if that case is empty.
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[.
	 */
	public List<String[]> getTranslationsOfNextVocabInCase(int caseNo)
	{
		checkCaseNo(caseNo);
		if(cases[caseNo].isEmpty())
			return null;
		
		return cases[caseNo].peek().getGermans();
	}
	
	/**
	 * Answers the vocabulary got by getNextVocabInCase(). Answering means it is removed from the current case and inserted 
	 * into the next one (respectively it stays in the last case since there is no next one) if it was answered correctly 
	 * or it is put back into case 0 if it was answered wrong.
	 * 
	 * @param caseNo The number of the case the answered vocabulary was taken from.
	 * @param correct Must be true if the answer was correct and false otherwise.
	 * @throws NoSuchElementException if the given case is empty.
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[.
	 */
	public void answerVocabInCase(int caseNo, boolean correct)
	{
		checkCaseNo(caseNo);
		
		if(correct)
		{
			int nextCase = Math.min(getNumberOfCases()-1, caseNo+1);
			cases[nextCase].add(cases[caseNo].remove());
		}
		else
		{
			cases[0].add(cases[caseNo].remove());
		}
	}
	
	/**
	 * Adds a translation to the first vocabulary card in the given case.
	 * @param caseNo The number of the case
	 * @param pos position of the card in the case, front is 0
	 * @param german the translation to add
	 * @return true if successfully added, false if the card does already have this translation
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[.
	 */
	public boolean addTranslationToVocabInCase(int caseNo, int pos, String[] german)
	{
		checkCaseNo(caseNo);
		
		if(pos < 0 || pos >= cases[caseNo].size())
		{
			return false;
		}
		return ((LinkedList<VocabularyCard>)cases[caseNo]).get(pos).addGerman(german);
	}
	
	/**
	 * Removes a translation from the first vocabulary card in the given case.
	 * @param caseNo the number of the case
	 * @param pos position of the card in the case, front is 0
	 * @param german the translation to remove
	 * @return true if successfully removed, false if the card does not have this translation
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[.
	 */
	public boolean removeTranslationFromVocabInCase(int caseNo, int pos, String german[])
	{
		checkCaseNo(caseNo);
		
		if(cases[caseNo].isEmpty())
		{
			return false;
		}
		return ((LinkedList<VocabularyCard>)cases[caseNo]).get(pos).removeGerman(german);
	}
	
	/**
	 * Removes a translation from the first vocabulary card in the given case at the given 
	 * index of the list of translations of that card.
	 * @param caseNo the number of the case
	 * @param pos position of the card in the case, front is 0
	 * @param index the index of the translation to remove
	 * @return true if successfully removed, false otherwise
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[.
	 */
	public boolean removeTranslationFromVocabInCase(int caseNo, int pos, int index)
	{
		checkCaseNo(caseNo);
		
		if(cases[caseNo].isEmpty())
		{
			return false;
		}
		return ((LinkedList<VocabularyCard>)cases[caseNo]).get(pos).removeGerman(index);
	}
	
	/**
	 * Shuffles a case due to learning the vocabularies themselves instead of their sequence.
	 * 
	 * @param caseNo The number of the case to be shuffled.
	 * @throws IllegalArgumentException if <tt>caseNo</tt> is not in [0,size[.
	 */
	public void shuffleCase(int caseNo)
	{
		checkCaseNo(caseNo);
		
		Collections.shuffle((LinkedList<VocabularyCard>)cases[caseNo]);
	}
	
	/**
	 * Retrieves an array indicating the amount of vocabularies in each case.
	 * @return An array indicating the amount of vocabularies in each case.
	 */
	public int[] getCaseVolumes()
	{
		int[] res = new int[getNumberOfCases()];
		for(int i=0; i<res.length; i++)
		{
			res[i] = cases[i].size();
		}
		
		return res;
	}
	
	/**
	 * Retrieves the total amount of all vocabularies in this VocabularyBox.
	 * @return The total amount of vocabularies.
	 */
	public int getVolume()
	{
		int res = 0;
		for(int i=0; i<getNumberOfCases(); i++)
		{
			res += cases[i].size();
		}
		
		return res;
	}
	
	/**TODO
	 * Creates a new instance of VocabularyBox that contains all vocabularies of this and die other box.
	 * Neither this nor the other box will change their inner state. If this box has n cases and the other one has m
	 * cases, the resulting box will have max(n,m) cases. All copied vocabularies will be inserted
	 * into their old case number. As an exception of this there is the case that a vocabulary is contained in both 
	 * boxes, this and other. In this case it is inserted only once namely into the lower one of both cases.
	 * 
	 * @param other The other VocabularyBox this box will be merged with.
	 * @return A new VocabularyBox containing the vocabularies of <tt>this</tt> and <tt>other</tt> in their original cases.
	 */
	public VocabularyBox merge(VocabularyBox other)
	{
		VocabularyBox box = new VocabularyBox(Math.max(this.getNumberOfCases(), other.getNumberOfCases()));
		
		for(int i=0; i<this.getNumberOfCases(); i++)
		{
			for(int j=0; j<this.cases[i].size(); j++)
			{
				box.cases[i].add(this.cases[i].element());
				this.cases[i].add(this.cases[i].remove());
			}
		}
		
		for(int i=0; i<other.getNumberOfCases(); i++)
		{
			for(int j=0; j<other.cases[i].size(); j++)
			{
				VocabularyCard vocab = other.cases[i].element();
				int found = box.find(vocab.english);
				if(found < 0)//box does not contain vocab jet
				{
					box.cases[i].add(vocab);
				}
				else if(found > i)//box does already contain vocab, so let's put it into the lower of both prior cases
				{
					box.remove(vocab.english);
					box.cases[i].add(vocab);
				}

				other.cases[i].add(other.cases[i].remove());
			}
		}
		
		return box;
	}
}
