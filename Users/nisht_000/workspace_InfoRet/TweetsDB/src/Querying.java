import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class Querying {

	 
    private static String url = "http://localhost:8983/solr/collection1/";
    private static HttpSolrServer server;

    public Querying() throws MalformedURLException
    {
    	server = new HttpSolrServer(url);
    }
    public void query(String user_query, boolean user_desc) throws SolrServerException
    {
    	MaxentTagger tagger = new MaxentTagger("C:\\Users\\nisht_000\\Documents\\Sem 4.2\\CZ4034 Info Retrieval\\stanford-postagger-full-2015-01-29\\stanford-postagger-full-2015-01-30\\models\\wsj-0-18-left3words-distsim.tagger");
    	
    	SolrQuery solr_query = new SolrQuery();
    	String[] queryTerms = user_query.split(" ");
    	
    	ArrayList<String> noun_adj_uni = new ArrayList<String>();
    	ArrayList<String> noun_adj_pairs = new ArrayList<String>();
    	
    	for(int i=0; i<queryTerms.length; i++)
    	{
    		String tag = tagger.tagString(queryTerms[i]);
    	 
    		  		
    		if(tag.split("_")[1].trim().equals("JJ")
    			|| tag.split("_")[1].trim().equals("NNP")
    			|| tag.split("_")[1].trim().equals("NN")
    			|| tag.split("_")[1].trim().equals("NNS"))
    		{
    			noun_adj_uni.add(queryTerms[i]);
    		}
    	}
       //System.out.println(noun_adj_uni.get(0));
    	//Preserving the order of the query words
    	for(int j=0; j<noun_adj_uni.size()-1; j++)
    	{
    		for(int k=j+1; k<noun_adj_uni.size()-1; j++)
        	{
    			
    		    noun_adj_pairs.add(noun_adj_uni.get(j)+" "+noun_adj_uni.get(k));
        	}
    	}
    	
    	String queryStr="";
    	if(user_desc){ //if user wants to query on the user_description also
    	for(int i=0; i<noun_adj_uni.size();i++)
    	{
    		queryStr = queryStr + "tweet:*"+noun_adj_uni.get(i)+"*OR user_description:*"+noun_adj_uni.get(i)+"*AND ";
    	}
    	for(int i=0; i<noun_adj_pairs.size();i++)
    	{
    		queryStr = queryStr + "tweet:*"+noun_adj_pairs.get(i)+"*OR user_description:*"+noun_adj_pairs.get(i)+"*AND ";
    	}
    	}
    	else
    	{
    		for(int i=0; i<noun_adj_uni.size();i++)
        	{
        		queryStr = queryStr + "tweet:*"+noun_adj_uni.get(i)+"*AND ";
        	}
        	for(int i=0; i<noun_adj_pairs.size();i++)
        	{
        		queryStr = queryStr + "tweet:*"+noun_adj_pairs.get(i)+"*AND ";
        	}
    	}
    	
    	System.out.println(queryStr.substring(0,queryStr.length()-4));
    	solr_query.setQuery(queryStr.substring(0,queryStr.length()-4));
        //"tweet:*dead*AND user_description:*Twitter*"
    	solr_query.setRows(100);
        //query.addSortField( "user_followers_count", SolrQuery.ORDER.asc );
        QueryResponse rsp = server.query( solr_query );
        System.out.println("Querying");
        SolrDocumentList docs = rsp.getResults();
        System.out.println(docs.size());
        //List<Item> beans = rsp.getBeans(Item.class);
        
        String[] rankedDocs = getRankedDocuments(user_desc, noun_adj_uni, noun_adj_pairs,docs);
        
    }
    public String[] getRankedDocuments(boolean user_desc, ArrayList<String> unigrams, ArrayList<String> bigrams, SolrDocumentList docs)
    {
    	//check frequency of queryTerms in the list of tweets and userDesc
    	HashMap<SolrDocument, Integer> unigrams_in_tweet = new HashMap<SolrDocument, Integer>();
    	HashMap<SolrDocument, Integer> bigrams_in_tweet = new HashMap<SolrDocument, Integer>();
    	HashMap<SolrDocument, Integer> unigrams_in_user_desc = new HashMap<SolrDocument, Integer>();
    	HashMap<SolrDocument, Integer> bigrams_in_user_desc = new HashMap<SolrDocument, Integer>();
    	
    	for(int i=0; i<unigrams.size(); i++)
    	{
    		for(SolrDocument doc : docs)
    		{
    		  unigrams_in_tweet.put(doc, getFrequency((String)doc.getFieldValue("tweet"),unigrams.get(i)));
    		}
    		if(user_desc)
    		{
    			for(SolrDocument doc : docs)
        		{
    				unigrams_in_user_desc.put(doc, getFrequency((String)doc.getFieldValue("user_description"),unigrams.get(i)));
        		}
    		}
    	}
    	for(int i=0; i<bigrams.size(); i++)
    	{
    		for(SolrDocument doc : docs)
    		{
    		  bigrams_in_tweet.put(doc, getFrequency((String)doc.getFieldValue("tweet"),bigrams.get(i)));
    		}
    		if(user_desc)
    		{
    			for(SolrDocument doc : docs)
        		{
    				bigrams_in_user_desc.put(doc, getFrequency((String)doc.getFieldValue("user_description"),bigrams.get(i)));
        		}
    		}
    	}
    	//sort the hashmaps on basis of the values 
    	return null;
    }
    public int getFrequency(String text, String term)
    {
    	String patternString = "hello";

        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(text);
        //System.out.println(matcher.groupCount());
        
        boolean matches = matcher.matches();
        System.out.println(matches);
        int frequency =0;
        while (matcher.find())
        {
        	System.out.println("true");
        	frequency++;
        }
        System.out.println(frequency);
    	return frequency;
    }
    
    public static void main(String[] args)throws Exception
    {
    	Querying obj = new Querying();
    	//obj.query("people dead in Malaysia", true);
    	obj.getFrequency("hello this is nishtha, hello world, hello universe", "hello");
    }
}
