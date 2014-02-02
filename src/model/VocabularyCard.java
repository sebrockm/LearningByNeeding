package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



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
	private List<List<String>> germans;
	
	/**
	 * Creates a new VocabularyCard with a foreign language vocab and an empty list of translations
	 * @param english The vocab in the foreign language
	 */
	public VocabularyCard(String english)
	{
		this.english = english;
		this.germans = new LinkedList<List<String>>();
	}
	
	/**
	 * Adds a new translation to this card.
	 * @param german translation to add
	 * @return false if the card already contains this translation, true if successfully added
	 */
	public boolean addGerman(String[] german)
	{
		List<String> l = Arrays.asList(german);
		if(!germans.contains(l))
		{
			germans.add(l);
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
		return germans.remove(Arrays.asList(german));
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
		List<String[]> list = new LinkedList<String[]>();
		for(List<String> l : germans)
		{
			list.add((String[]) l.toArray());
		}
		return list;
	}
	
	public VocabularyCard merge(VocabularyCard other)
	{
		if(!this.getEnglish().equals(other.getEnglish()))
			return null;
		
		VocabularyCard card = new VocabularyCard(this.getEnglish());
		for(List<String> l : this.germans)
		{
			card.germans.add(l);
		}
		for(List<String> l : other.germans)
		{
			if(!card.germans.contains(l))
				card.germans.add(l);
		}
		
		return card;
	}
}
