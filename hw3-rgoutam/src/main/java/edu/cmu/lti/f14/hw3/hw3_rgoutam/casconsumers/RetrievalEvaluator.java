package edu.cmu.lti.f14.hw3.hw3_rgoutam.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_rgoutam.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_rgoutam.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_rgoutam.utils.Utils;


public class RetrievalEvaluator extends CasConsumer_ImplBase {

	/** query id number **/
	public ArrayList<Integer> qIdList;

	/** query and text relevant values **/
	public ArrayList<Integer> relList;

	public ArrayList<Map<String, Double>> fsList;
	
	public ArrayList<String> docList;
	
	public ArrayList<Double> cosineSimilarity;
	
	public void initialize() throws ResourceInitializationException {

		qIdList = new ArrayList<Integer>();

		relList = new ArrayList<Integer>();
		
		fsList = new ArrayList<Map<String, Double>>();
		
		docList = new ArrayList<String>();
		
		cosineSimilarity = new ArrayList<Double>();
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
	
		if (it.hasNext()) {
			Document doc = (Document) it.next();

			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			//ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);

			qIdList.add(doc.getQueryID());
			relList.add(doc.getRelevanceValue());
			
			ArrayList<Token> queryToken = Utils.fromFSListToCollection(fsList.get(i), Token.class);
      Map<String, Integer> temp = convertToMap(queryToken);
      Map<String, Double> queryMap = L1Norm(temp);
			
			fsList.add(queryMap);
			docList.add(doc.getText());
			
			//Do something useful here

		}

	}

	
	private Map<String, Integer> convertToMap(Collection<Token> c) {
	  Map<String, Integer> ret = new HashMap<String, Integer>();
	  for(Token t : c)
	    ret.put(t.getText(), t.getFrequency());
    return ret;
	}
	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);

    // TODO :: compute the cosine similarity measure
		
		int i = 0;
		while(i < qIdList.size()) {
		  Map<String, Double> queryMap = fsList.get(i);
		  cosineSimilarity.add(1.0);
		  int j = i + 1;
		  while(j < qIdList.size() && qIdList.get(j) == qIdList.get(j)) {
		    Map<String, Double> docMap = fsList.get(j);
		    double cosSim = computeCosineSimilarity(queryMap, docMap);
		    cosineSimilarity.add(cosSim);
		  }
		}

		// TODO :: compute the rank of retrieved sentences
		i = 0;
		while(i < qIdList.size()) {
		  ArrayList<Integer> temp = new ArrayList<Integer>();
		  int j = i + 1;
		  while(j < qIdList.size() && qIdList.get(j) == qIdList.get(j)) {
		    temp.add(j);
		  }
		  
		  Collections.sort(temp,  myComparator);
		  
		}
		
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}

	private Map<String, Double> L1Norm(Map<String, Integer> vector) {
	  double mag = 0.0;
	  Map<String, Double> ret = new HashMap<String, Double>();
	  for(String w : vector.keySet()) {
      mag += vector.get(w);
    }
	  for(String w: vector.keySet()) {
	    ret.put(w, (double)vector.get(w)/mag);
	  }
	  return ret;
	}
	
	private Map<String, Double> L2Norm(Map<String, Integer> vector) {
    double mag = 0.0;
    Map<String, Double> ret = new HashMap<String, Double>();
    for(String w : vector.keySet()) {
      mag += (vector.get(w) * vector.get(w));
    }
    mag = Math.sqrt(mag);
    
    for(String w: vector.keySet()) {
      ret.put(w, (double)vector.get(w)/mag);
    }
    return ret;
  }
	
	private double L2NormMag(Map<String, Double> vector) {
    double mag = 0.0;
    Map<String, Double> ret = new HashMap<String, Double>();
    for(String w : vector.keySet()) {
      mag += (vector.get(w) * vector.get(w));
    }
    mag = Math.sqrt(mag);
    return mag;
  }
	
	/**
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Double> queryVector,
			Map<String, Double> docVector) {
		double cosine_similarity=0.0;

		// TODO :: compute cosine similarity between two sentences
		double magQuery = L2NormMag(queryVector), magDoc = L2NormMag(docVector);
		double num = 0;
		for(String w : queryVector.keySet()) {
		  if(docVector.containsKey(w)) {
		    num += queryVector.get(w) * docVector.get(w);
		  }
		}
		cosine_similarity = num/(magQuery * magDoc);

		return cosine_similarity;
	}

	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr() {
		double metric_mrr=0.0;

		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		
		return metric_mrr;
	}

}
