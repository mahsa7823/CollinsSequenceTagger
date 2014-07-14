import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.Integer;
import tagPack.Features;

public class PerceptronTagger{
	
	
	public static int word4suffixNumber;
	public static int word3suffixNumber;
	public static int word2suffixNumber;
	public static int word1suffixNumber;
	public static int word4preffixNumber;
	public static int word3preffixNumber;
	public static int word2preffixNumber;
	public static int word1preffixNumber;
	
	public static String train_file = "";
	public static String in_file = "";
	public static String dev_file = "";
	public static String out_file = "output";
	public static int iter = 5;
	public static boolean fb_flag = false;
	
	public static void main(String[] args){ 
		int i = 0, j;
		String arg;
		
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			
			if (arg.equals("-suf")) {
				Features.suf_flag = true;
			}else if (arg.equals("-pref")) {
				Features.pref_flag = true;
			}else if (arg.equals("-next")) {
				Features.next_flag = true;
			}else if (arg.equals("-prev")) {
				Features.prev_flag = true;
			}else if (arg.equals("-dig")) {
				Features.dig_flag = true;
			}else if (arg.equals("-fb")) { // forward_backward instead of default viterbi
				fb_flag = true;
			}
			
			else if (arg.equals("-train")) {
				if (i < args.length)
					train_file = args[i++];
				else
					System.err.println("-train requires a filename");
			}else if (arg.equals("-in")) {
				if (i < args.length)
					in_file = args[i++];
				else
					System.err.println("-in requires a filename");
			}else if (arg.equals("-dev")) {
				if (i < args.length)
					dev_file = args[i++];
				else
					System.err.println("-dev requires a filename");
			}else if (arg.equals("-out")) {
				if (i < args.length)
					out_file = args[i++];
				else
					System.err.println("-out requires a filename");
			}else if (arg.equals("-iter")) {
				if (i < args.length)
					iter = Integer.parseInt(args[i++]);
				else
					System.err.println("-iter requires a number");
			}
	
		}
		
		makeDataStructure(train_file);
		
		/* Calling viterbi function for each sentence of the test set.
		 * The result is written into tagged_viterbi file.
		 */
		try {
			BufferedReader test_file = new BufferedReader(new FileReader(in_file));
			String sentence = null;
			while ((sentence=test_file.readLine()) != null) {
				if (fb_flag)
					Features.forward_backward("<s> "+sentence+" </s>", true, out_file);
				else
					Features.viterbi("<s> "+sentence+" </s>", true, out_file);
				
			}
			test_file.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	
	private static void makeDataStructure(String train_file){
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(train_file));
			String word1 = "";
			String word2 = "";
			String tag1 = "";
			String tag2 = "";
			String line1 = "";
			String line2 = "";
			
			Set<String> tagSet = new HashSet<String>();
			Set<String> wordlabelSet = new HashSet<String>();
			Set<String> word4suffixSet = new HashSet<String>();
			Set<String> word3suffixSet = new HashSet<String>();
			Set<String> word2suffixSet = new HashSet<String>();
			Set<String> word1suffixSet = new HashSet<String>();
			Set<String> word4preffixSet = new HashSet<String>();
			Set<String> word3preffixSet = new HashSet<String>();
			Set<String> word2preffixSet = new HashSet<String>();
			Set<String> word1preffixSet = new HashSet<String>();
			HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
			Set<String> wordpairlabelSet = new HashSet<String>();
			
			List truth_sentences_wt = new ArrayList(); // list of training set sentences in "word tag" format
			List truth_sentences = new ArrayList(); // list of training set sentences in "w1 .. wn" fromat
			
			//reading training file and assigning values to tagSet and wordlabelSet variables
			line1 = reader.readLine();
			String sentence_wt = "";
			String sentence = "";
			String wordpair1 = "<s>"; // prev" "current
			String wordpair2 = ""; // current" "next
			while (line1 !=null){
				if ((line2=reader.readLine()).isEmpty()) {
					word1 = line1.substring(0, line1.indexOf("\t"));
					wordpair1 += " "+word1;
					wordpair2 = word1 + " </s>";
					tag1 = line1.substring(line1.lastIndexOf("\t")+1);
					sentence_wt += line1;
					sentence += word1;
					
					add_to_word_tag_set(word1,tag1,tagSet,wordlabelSet,
															word4suffixSet,word3suffixSet,word2suffixSet,word1suffixSet,
															word4preffixSet,word3preffixSet,word2preffixSet,word1preffixSet,
															wordcount,wordpair1,wordpair2,wordpairlabelSet);
					
					truth_sentences_wt.add("<s>\tSS " + sentence_wt +" </s>\tSE");
					truth_sentences.add("<s> "+sentence+" </s>");
					sentence_wt = "";
					sentence = "";
					line2=reader.readLine();
					wordpair1 = "<s>";
				} else{
					word1 = line1.substring(0, line1.indexOf("\t"));
					word2 = line2.substring(0, line2.indexOf("\t"));
					wordpair1 += " "+word1;
					wordpair2 = word1+" "+word2;
					tag1 = line1.substring(line1.lastIndexOf("\t")+1);
					sentence_wt += line1 + " ";
					sentence += word1 + " ";
					add_to_word_tag_set(word1,tag1,tagSet,wordlabelSet,
															word4suffixSet,word3suffixSet,word2suffixSet,word1suffixSet,
															word4preffixSet,word3preffixSet,word2preffixSet,word1preffixSet,
															wordcount,wordpair1,wordpair2,wordpairlabelSet);

					wordpair1 = word1;
				}
				line1 = line2;
			}
			reader.close();
			
			//assigning indices to tags
			int labelNumber = 1;
			Features.tag.put("SS", new Integer(0));
			Features.tau.put(new Integer(0), "SS");
			for (Iterator setIter=tagSet.iterator(); setIter.hasNext(); ) {
				String t = (String)setIter.next();
				if (!t.equals("SS") && !t.equals("SE")){
					Features.tag.put(t, new Integer(labelNumber));
					Features.tau.put(new Integer(labelNumber), t);
					labelNumber += 1;
				}
			}
			Features.tag.put("SE", new Integer(labelNumber));
			Features.tau.put(new Integer(labelNumber), "SE");
			
			//assigning indices to the words of training set
			Features.wordlabelNumber = 2;
			Features.word.put("<s>", new Integer(0));
			Features.word.put("</s>", new Integer(1));
			for (Iterator wsetIter=wordlabelSet.iterator(); wsetIter.hasNext(); ) {
				String w = (String)wsetIter.next();
				if (wordcount.get(w).intValue() < 5) {
					Features.rare_words.add(w);
				}
				Features.word.put(w, new Integer(Features.wordlabelNumber));
				Features.wordlabelNumber += 1;
			}

			//assigning indices to the pair words of training set
			Features.wordpairlabelNumber = 0;
			for (Iterator wsetIter=wordpairlabelSet.iterator(); wsetIter.hasNext(); ) {
				String w = (String)wsetIter.next();
				Features.wordpair.put(w, new Integer(Features.wordpairlabelNumber));
				Features.wordpairlabelNumber += 1;
			}
			System.out.println(Features.wordlabelNumber);
			System.out.println(Features.wordpairlabelNumber);
			
			//assigning indices to the 4 last characters of the words of training set
			word4suffixNumber = 0;
			for (Iterator w4setIter=word4suffixSet.iterator(); w4setIter.hasNext(); ) {
				String w4 = (String)w4setIter.next();
				Features.word_4suffix.put(w4, new Integer(word4suffixNumber));
				word4suffixNumber += 1;
			}
			
			//assigning indices to the 3 last characters of the words of training set
			word3suffixNumber = 0;
			for (Iterator w3setIter=word3suffixSet.iterator(); w3setIter.hasNext(); ) {
				String w3 = (String)w3setIter.next();
				Features.word_3suffix.put(w3, new Integer(word3suffixNumber));
				word3suffixNumber += 1;
			}
			
			//assigning indices to the 2 last characters of the words of training set
			word2suffixNumber = 0;
			for (Iterator w2setIter=word2suffixSet.iterator(); w2setIter.hasNext(); ) {
				String w2 = (String)w2setIter.next();
				Features.word_2suffix.put(w2, new Integer(word2suffixNumber));
				word2suffixNumber += 1;
			}
			
			//assigning indices to the 1 last characters of the words of training set
			word1suffixNumber = 0;
			for (Iterator w1setIter=word1suffixSet.iterator(); w1setIter.hasNext(); ) {
				String w1 = (String)w1setIter.next();
				Features.word_1suffix.put(w1, new Integer(word1suffixNumber));
				word1suffixNumber += 1;
			}
			
			//assigning indices to the 4 first characters of the words of training set
			word4preffixNumber = 0;
			for (Iterator w4psetIter=word4preffixSet.iterator(); w4psetIter.hasNext(); ) {
				String w4p = (String)w4psetIter.next();
				Features.word_4preffix.put(w4p, new Integer(word4preffixNumber));
				word4preffixNumber += 1;
			}
			
			//assigning indices to the 3 first characters of the words of training set
			word3preffixNumber = 0;
			for (Iterator w3psetIter=word3preffixSet.iterator(); w3psetIter.hasNext(); ) {
				String w3p = (String)w3psetIter.next();
				Features.word_3preffix.put(w3p, new Integer(word3preffixNumber));
				word3preffixNumber += 1;
			}
			
			//assigning indices to the 2 first characters of the words of training set
			word2preffixNumber = 0;
			for (Iterator w2psetIter=word2preffixSet.iterator(); w2psetIter.hasNext(); ) {
				String w2p = (String)w2psetIter.next();
				Features.word_2preffix.put(w2p, new Integer(word2preffixNumber));
				word2preffixNumber += 1;
			}
			
			//assigning indices to the 1 first characters of the words of training set
			word1preffixNumber = 0;
			for (Iterator w1psetIter=word1preffixSet.iterator(); w1psetIter.hasNext(); ) {
				String w1p = (String)w1psetIter.next();
				Features.word_1preffix.put(w1p, new Integer(word1preffixNumber));
				word1preffixNumber += 1;
			}
			
			Features.m = Features.tag.size() - 1; //number of tags
			
			perceptron(truth_sentences_wt, truth_sentences, true); // averaged perceptron flag is set to true
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static void add_to_word_tag_set(String word1, String tag1,Set<String> tagSet,
																					Set<String> wordlabelSet,
																					Set<String> word4suffixSet,Set<String> word3suffixSet,
																					Set<String> word2suffixSet,Set<String> word1suffixSet,
																					Set<String> word4preffixSet,Set<String> word3preffixSet,
																					Set<String> word2preffixSet,Set<String> word1preffixSet,
																					HashMap<String, Integer> wordcount,String wordpair1,
																					String wordpair2,Set<String> wordpairlabelSet) {
		tagSet.add(tag1);
		wordlabelSet.add(word1);
		wordpairlabelSet.add(wordpair1);
		wordpairlabelSet.add(wordpair2);
		
		int w1Count = (wordcount.get(word1) == null) ? 1:wordcount.get(word1).intValue()+1;
		wordcount.put(word1, new Integer(w1Count));
		
		if (word1.length() >= 4) {
			word4suffixSet.add(word1.substring(word1.length()-4, word1.length()));
			word4preffixSet.add(word1.substring(0, 4));
			
			word3suffixSet.add(word1.substring(word1.length()-3, word1.length()));
			word3preffixSet.add(word1.substring(0, 3));
			
			word2suffixSet.add(word1.substring(word1.length()-2, word1.length()));
			word2preffixSet.add(word1.substring(0, 2));
			
			word1suffixSet.add(word1.substring(word1.length()-1, word1.length()));
			word1preffixSet.add(word1.substring(0, 1));
		}
		else if (word1.length() >= 3) {
			word3suffixSet.add(word1.substring(word1.length()-3, word1.length()));
			word3preffixSet.add(word1.substring(0, 3));
			
			word2suffixSet.add(word1.substring(word1.length()-2, word1.length()));
			word2preffixSet.add(word1.substring(0, 2));
			
			word1suffixSet.add(word1.substring(word1.length()-1, word1.length()));
			word1preffixSet.add(word1.substring(0, 1));
		} else if (word1.length() >= 2) {
			word2suffixSet.add(word1.substring(word1.length()-2, word1.length()));
			word2preffixSet.add(word1.substring(0, 2));
			
			word1suffixSet.add(word1.substring(word1.length()-1, word1.length()));
			word1preffixSet.add(word1.substring(0, 1));
		} else { // (word1.length() >= 1) 
			word1suffixSet.add(word1.substring(word1.length()-1, word1.length()));
			word1preffixSet.add(word1.substring(0, 1));
		}
	}
	
	private static void perceptron(List truth_sentences_wt, List truth_sentences, boolean averaged){
		
		Features.init_feats(word1preffixNumber, word2preffixNumber, word3preffixNumber
			, word4preffixNumber, word1suffixNumber, word2suffixNumber, word3suffixNumber
			, word4suffixNumber);
		
		int count = 0;
		double accuracy = 0.0;
		
		int wi_4suff=-1,wi_3suff=-1,wi_2suff=-1,wi_1suff=-1;
		int wi_4pref=-1,wi_3pref=-1,wi_2pref=-1,wi_1pref=-1;
		int w_iminus1=-1, w_iplus1=-1, w_iminus2=-1, w_iplus2=-1;
		int t_itruth=-1, t_iguess=-1, t_iminus1truth=-1, t_iminus1guess=-1;
		String w_iminus1_en="", w_iplus1_en="", w_iminus2_en="", w_iplus2_en="";
		
		for (int t=0; t<iter; t++) {
			System.out.println("iteration "+ (t+1));
			for (int i=0; i<truth_sentences_wt.size(); i++) {
				String truth = (String)truth_sentences_wt.get(i);
				String[] truth_wt = truth.split(" ");
				String w_i;
				
				count = (truth_sentences_wt.size()*t) + (i+1);
				
				// call viterbi to tag sentence based on current parameters
				// test is set to false to indicate we're in training phase
				
				String guess = "";
				if (fb_flag)
					guess = Features.forward_backward((String)truth_sentences.get(i), false, "dummy");
				else
					guess = Features.viterbi((String)truth_sentences.get(i), false, "dummy");
				String[] guess_wt = guess.split(" ");
				
				for (int n=1; n<=truth_wt.length-2; n++) {
					wi_4suff=-1;wi_3suff=-1;wi_2suff=-1;wi_1suff=-1;
				  wi_4pref=-1;wi_3pref=-1;wi_2pref=-1;wi_1pref=-1;
					
					w_iminus2=-1; w_iplus2=-1;
			
					t_itruth = Features.tag.get(truth_wt[n].substring(truth_wt[n].indexOf("\t")+1));
					w_i = truth_wt[n].substring(0,truth_wt[n].indexOf("\t"));
					t_iminus1truth = Features.tag.get(truth_wt[n-1].substring(truth_wt[n-1].indexOf("\t")+1));
					w_iminus1_en = truth_wt[n-1].substring(0,truth_wt[n-1].indexOf("\t"));
					w_iminus1 = Features.word.get(w_iminus1_en);
					w_iplus1_en = truth_wt[n+1].substring(0,truth_wt[n+1].indexOf("\t"));
					w_iplus1 = Features.word.get(w_iplus1_en);
					if (n>=2)
						w_iminus2 = Features.word.get(truth_wt[n-2].substring(0,truth_wt[n-2].indexOf("\t")));
					if (n < truth_wt.length-2)	{
						w_iplus2_en = truth_wt[n+2].substring(0,truth_wt[n+2].indexOf("\t"));
						w_iplus2 = Features.word.get(w_iplus2_en);
					}
					
					if (w_i.length() >= 4) {
					if (Features.word_4suffix.get(w_i.substring(w_i.length()-4, w_i.length())) != null)
						wi_4suff = Features.word_4suffix.get(w_i.substring(w_i.length()-4, w_i.length()));
					if (Features.word_3suffix.get(w_i.substring(w_i.length()-3, w_i.length())) != null)
						wi_3suff = Features.word_3suffix.get(w_i.substring(w_i.length()-3, w_i.length()));
					if (Features.word_2suffix.get(w_i.substring(w_i.length()-2, w_i.length())) != null)
						wi_2suff = Features.word_2suffix.get(w_i.substring(w_i.length()-2, w_i.length()));
					if (Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length())) != null)
						wi_1suff = Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length()));
					if (Features.word_4preffix.get(w_i.substring(0, 4)) != null)
						wi_4pref = Features.word_4preffix.get(w_i.substring(0, 4));
					if (Features.word_3preffix.get(w_i.substring(0, 3)) != null)
						wi_3pref = Features.word_3preffix.get(w_i.substring(0, 3));
					if (Features.word_2preffix.get(w_i.substring(0, 2)) != null)
						wi_2pref = Features.word_2preffix.get(w_i.substring(0, 2));
					if (Features.word_1preffix.get(w_i.substring(0, 1)) != null)
						wi_1pref = Features.word_1preffix.get(w_i.substring(0, 1));
					} else if (w_i.length() >= 3) {
					if (Features.word_3suffix.get(w_i.substring(w_i.length()-3, w_i.length())) != null)
						wi_3suff = Features.word_3suffix.get(w_i.substring(w_i.length()-3, w_i.length()));
					if (Features.word_2suffix.get(w_i.substring(w_i.length()-2, w_i.length())) != null)
						wi_2suff = Features.word_2suffix.get(w_i.substring(w_i.length()-2, w_i.length()));
					if (Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length())) != null)
						wi_1suff = Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length()));
					if (Features.word_3preffix.get(w_i.substring(0, 3)) != null)
						wi_3pref = Features.word_3preffix.get(w_i.substring(0, 3));
					if (Features.word_2preffix.get(w_i.substring(0, 2)) != null)
						wi_2pref = Features.word_2preffix.get(w_i.substring(0, 2));
					if (Features.word_1preffix.get(w_i.substring(0, 1)) != null)
						wi_1pref = Features.word_1preffix.get(w_i.substring(0, 1));
					} else if (w_i.length() >= 2) {
					if (Features.word_2suffix.get(w_i.substring(w_i.length()-2, w_i.length())) != null)
						wi_2suff = Features.word_2suffix.get(w_i.substring(w_i.length()-2, w_i.length()));
					if (Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length())) != null)
						wi_1suff = Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length()));
					if (Features.word_2preffix.get(w_i.substring(0, 2)) != null)
						wi_2pref = Features.word_2preffix.get(w_i.substring(0, 2));
					if (Features.word_1preffix.get(w_i.substring(0, 1)) != null)
						wi_1pref = Features.word_1preffix.get(w_i.substring(0, 1));
					} else { // w_i.length() >= 1
					if (Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length())) != null)
						wi_1suff = Features.word_1suffix.get(w_i.substring(w_i.length()-1, w_i.length()));
					if (Features.word_1preffix.get(w_i.substring(0, 1)) != null)
						wi_1pref = Features.word_1preffix.get(w_i.substring(0, 1));
					}
					
					Features.reward_penalize(1,count,n,truth_wt.length,w_i,w_iminus1,w_iminus1_en,w_iplus1,w_iplus1_en,w_iminus2,w_iminus2_en,w_iplus2,w_iplus2_en,t_itruth,t_iminus1truth,wi_4suff,wi_3suff,wi_2suff,wi_1suff,
											 wi_4pref,wi_3pref,wi_2pref,wi_1pref);	// rewarding features for the truth
					

					t_iguess = Features.tag.get(guess_wt[n].substring(guess_wt[n].indexOf("\t")+1));
					t_iminus1guess = Features.tag.get(guess_wt[n-1].substring(guess_wt[n-1].indexOf("\t")+1));
					//startTime = System.currentTimeMillis();
					Features.reward_penalize(-1,count,n,truth_wt.length,w_i,w_iminus1,w_iminus1_en,w_iplus1,w_iplus1_en,w_iminus2,w_iminus2_en,
											 w_iplus2,w_iplus2_en,t_iguess,t_iminus1guess,wi_4suff,wi_3suff,wi_2suff,wi_1suff,
											 wi_4pref,wi_3pref,wi_2pref,wi_1pref); // penalizing features for the guess
					//estimatedTime = System.currentTimeMillis() - startTime;
					if (averaged) {
						Features.average_feats(t_iminus1truth, t_itruth, t_iminus1guess, t_iguess
											 , w_i, w_iminus1, w_iminus1_en,w_iminus2,w_iminus2_en,w_iplus1,w_iplus1_en, w_iplus2,w_iplus2_en,wi_1suff, wi_2suff, wi_3suff, wi_4suff
											 , wi_1pref, wi_2pref, wi_3pref, wi_4pref,count);
					}
				}
				
			}
		}
		if (averaged) {
			Features.final_average(word1preffixNumber, word2preffixNumber, word3preffixNumber
			, word4preffixNumber, word1suffixNumber, word2suffixNumber, word3suffixNumber
			, word4suffixNumber, count);
			Features.use_averaged_feats();
		}
	}
	
}

