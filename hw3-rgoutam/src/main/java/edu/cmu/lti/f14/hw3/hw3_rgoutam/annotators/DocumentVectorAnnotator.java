package edu.cmu.lti.f14.hw3.hw3_rgoutam.annotators;

import java.util.*;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_rgoutam.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_rgoutam.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_rgoutam.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}

	/**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   *
	 * @param doc input text
	 * @return    a list of tokens.
	 */

	List<String> tokenize0(String doc) {
	  List<String> res = new ArrayList<String>();
	  
	  for (String s: doc.split("\\s+"))
	    res.add(s);
	  return res;
	}

	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		
		List<String> tokens = tokenize0(docText);
		
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		for(String w : tokens) {
		  if(wordMap.containsKey(w))
		    wordMap.put(w,  wordMap.get(w) + 1);
		  else
		    wordMap.put(w,  1);
		}
		
		Collection<Token> tokenList = new ArrayList<Token>();
		
		for(String w: wordMap.keySet()) {
		  Token t = new Token(jcas);
		  t.setText(w);
		  t.setFrequency(wordMap.get(w));
		  tokenList.add(t);
		}
		doc.setTokenList(Utils.fromCollectionToFSList(jcas, tokenList));

	}

}
