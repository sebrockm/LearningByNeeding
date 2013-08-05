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
import java.util.Queue;
import java.util.LinkedList;


/**
 * This class represents a learning system for vocabularies. A VocabularyBox consists of (usually) 5 cases with the
 * numbers 0, 1, 2, 3, 4. Case 0 contains those vocabularies you do not know yet while the case with the highest number contains those
 * you (should) know very well. Every time you answer a vocabulary correctly it is put into the next case
 * (respectively it stays in the last case since there is no next case). If your answer was wrong, the vocabulary goes
 * back into case 0. The Vocabularies are only stored with their foreign language part. The counter part
 * in your native language has to be looked up somewhere else. E.g. if you are a German learning the pair "freedom - Freiheit", only
 * "freedom" is stored in the VocabularyBox.
 * 
 */
public class VocabularyBox implements Serializable{

	private static final long serialVersionUID = 1917951796892188357L;
	
	private Queue<String>[] cases;
	
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
			cases[i] = new LinkedList<String>();
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
	 * @return A new VocabularyBox object read from path.
	 * @throws FileNotFoundException if the given file does not exist
	 * @throws IOException if an IO error occurs
	 * @throws ClassNotFoundException if the class of the serialized object cannot be found
	 */
	public static VocabularyBox loadFromFile(String path) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois;
		ois = new ObjectInputStream(new FileInputStream(path));
		
		VocabularyBox ret = (VocabularyBox)ois.readObject();
		ois.close();
		
		return ret;
	}
	
	/**
	 * Serializes the VocabularyBox into the given path.
	 * 
	 * @param path Path to the file where to store the VocabularyBox in.
	 * @throws FileNotFoundException if the given file does not exist
	 * @throws IOException if an IO error occurs
	 */
	public void storeInFile(String path) throws FileNotFoundException, IOException
	{
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(new FileOutputStream(path));
		
		oos.writeObject(this);
		
		oos.close();
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
	 * @return The number of the case vocab were found in or -1 if vocab was not found in any of the cases.
	 */
	public int find(String vocab)
	{
		for(int i=0; i<getNumberOfCases(); i++)
		{
			if(cases[i].contains(vocab))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Searches for a vocabulary in the given case.
	 * 
	 * @param vocab The vocabulary searched for.
	 * @param caseNo The number of the case searched in.
	 * @return true if the given case contains vocab, false otherwise
	 * @throws IllegalArgumentException if caseNo is not in [0,size[. 
	 */
	public boolean find(String vocab, int caseNo)
	{
		checkCaseNo(caseNo);
		
		return cases[caseNo].contains(vocab);
	}
	
	/**
	 * Removes a vocabulary from the VocabularyBox if it is contained.
	 * @param vocab The vocabulary to remove.
	 * @return true if the removal was successful, false otherwise i.e. <param>vocab</param> was not found in the VocabularyBox.
	 */
	public boolean remove(String vocab)
	{
		for(int i=0; i<getNumberOfCases(); i++)
		{
			if(cases[i].remove(vocab))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Inserts a new vocabulary into case 0 of the VocabularyBox if it is not contained in any of the cases.
	 * 
	 * @param vocab The vocabulary to insert.
	 * @return true if insertion was successful, false otherwise (probably because vocab is already in the VocabularyBox).
	 */
	public boolean insert(String vocab)
	{
		if(find(vocab) < 0)
			return cases[0].add(vocab);
		
		return false;
	}
	
	/**
	 * Gets the first vocabulary of the given case or null if that case is empty.
	 * 
	 * @param caseNo The number of the case the vocabulary is taken from.
	 * @return The first vocabulary in the case with the number caseNo or null if that case is empty.
	 * @throws IllegalArgumentException if caseNo is not in [0,size[.
	 */
	public String getNextVocabInCase(int caseNo)
	{
		checkCaseNo(caseNo);
		
		return cases[caseNo].peek();
	}
	
	/**
	 * Answers the vocabulary got by getNextVocabInCase(). Answering means it is removed from the current case and inserted 
	 * into the next one (respectively it stays in the last case since there is no next one) if it was answered correctly 
	 * or it is put back into case 0 if it was answered wrong.
	 * 
	 * @param caseNo The number of the case the answered vocabulary was taken from.
	 * @param correct Must be true if the answer was correct and false otherwise.
	 * @throws NoSuchElementException if the given case is empty.
	 * @throws IllegalArgumentException if caseNo is not in [0,size[.
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
	 * Shuffles a case due to learning the vocabularies themselves instead of their sequence.
	 * 
	 * @param caseNo The number of the case to be shuffled.
	 * @throws IllegalArgumentException if caseNo is not in [0,size[.
	 */
	public void shuffleCase(int caseNo)
	{
		checkCaseNo(caseNo);
		
		Collections.shuffle((LinkedList<String>)cases[caseNo]);
	}
	
	/**
	 * Retrieves an array indicating the amount of vocabularies in each case.
	 * @return An array indicating the the amount of vocabularies in each case.
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
}
