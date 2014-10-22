package edu.cmu.lti.f14.hw3.hw3_rgoutam.annotators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.uima.UIMARuntimeException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.f14.hw3.hw3_rgoutam.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_rgoutam.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_rgoutam.utils.StanfordLemmatizer;
import edu.cmu.lti.f14.hw3.hw3_rgoutam.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  HashSet<String> stopwords;
  @Override
  public void initialize(UimaContext aContext) {
    // TODO Auto-generated method stub
    stopwords = new HashSet<String>();
    File file = new File("src/main/resources/stopwords.txt");
    FileReader fileReader = null;
    try {
      fileReader = new FileReader(file);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    StringBuffer stringBuffer = new StringBuffer();
    String line;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        stopwords.add(line);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      fileReader.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
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
	 * Non-word character spaced tokenizer with each word normalized by using stanford stemmer, words converted to lower case.
	 * 
	 * @param doc input text
	 * @return a list of tokens
	 */
	
	List<String> tokenizeStemming(String doc) {
	  List<String> res = new ArrayList<String>();
	  
	  StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
	  for (String s: doc.split("\\W+")) {
	    s = s.toLowerCase();
	    s = lemmatizer.stemWord(s);
	    if(!stopwords.contains(s) && s.length() > 1)
	      res.add(s);
	  }
	  
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
		//List<String> tokens = tokenizeStemming(docText);
		
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
