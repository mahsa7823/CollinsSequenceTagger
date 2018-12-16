import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.lang.Integer;
import tagpack.*;

public class PerceptronTagger{

	public static String train_file = "";
	public static String in_file = "";
	public static String dev_file = "";
	public static String out_name = "output";
	public static String mdl_path = "";
	public static boolean avg_flag = true;
	public static String mode_flag = "train"; // or test
	public static HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
	public static double best_acc_so_far = 0;
	public static int best_iter_so_far = 1;
	public static int max_iter = 30;

	public static void main(String[] args){ 
		int i = 0;
		String arg;

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			if (arg.equals("-train")) {
				if (i < args.length)
					train_file = args[i++];
			}else if (arg.equals("-in")) {
				if (i < args.length)
					in_file = args[i++];
			}else if (arg.equals("-dev")) {
				if (i < args.length)
					dev_file = args[i++];
			}else if (arg.equals("-outname")) {
				if (i < args.length)
					out_name = args[i++];
			}else if (arg.equals("-mdlpath")) {
				if (i < args.length)
					mdl_path = args[i++];
			}else if (arg.equals("-mode")) {
				if (i < args.length)
					mode_flag = args[i++];
			}
		}
		if (mode_flag.equals("train")) {
			System.err.println("Training phase.");
			// remove old files mdl_path/dev_outout_* mdl_path/mdl_*.zip
			// or mkdir mdl_path
			System.err.println("Removing old model files in "+mdl_path);
			File file = new File(mdl_path);
			String[] myFiles;      
			if(file.isDirectory()){  
				myFiles = file.list();  
				for (int f=0; f<myFiles.length; f++) {  
					File myFile = new File(file, myFiles[f]);
					if ((myFile.getName().endsWith("mdl_") && myFile.getName().endsWith(".zip")) || myFile.getName().startsWith("dev_output_"))
						myFile.delete();  
				}  
			} else {
				if (file.mkdir()) {
					System.err.println(mdl_path+" directory is created!");
				} else {
					System.err.println("Failed to create directory "+mdl_path+"!");
				}
			}
			System.err.println("Training the model...");
			makeDataStructure(train_file);
			System.err.println("Saving model...");
			dumpModel(mdl_path,"");
			System.err.println("Training phase is done!");
		} else if (mode_flag.equals("test")) {
			/* Calling viterbi function for each sentence of the test set.
			 * The result is written into "out_name" file.
			 */
			try {
				System.err.println("Test phase.");
				System.err.println("Removing the old output \""+out_name+"\" ...");
				File file = new File(out_name);
				if(file.delete()){
					System.err.println(file.getName() + " is deleted!");
				}else{
					System.err.println("No such file exists!");
				}
				System.err.println("Loading model ...");
				loadModel(mdl_path,"");
				System.err.println("Model loaded!");
				BufferedReader test_file = new BufferedReader(new FileReader(in_file));
				System.err.println("Writing the result into \""+out_name+"\" ...");
				String sentence = "";
				String line = null;
				boolean only_word = false;
				while ((line=test_file.readLine()) != null) {
					if (line.isEmpty()) {
						Features.viterbi("<s> "+sentence.substring(0,sentence.length()-1)+" </s>", true, out_name, avg_flag);
						sentence = "";
					} else {
						if (line.indexOf("\t") != -1) // test file contains truth tags
							sentence += line.substring(0,line.indexOf("\t")) + " ";
						else { // test files contains only word sequence
							only_word = true;
							sentence += line + " ";
						}
					}
				}
				test_file.close();
				System.err.println("Test phase is done!\n");
				if (!only_word) {
					double test_acc = tagging_accuracy(in_file, out_name);
					DecimalFormat df = new DecimalFormat("00.00");
					System.err.println("Tagging accuracy = "+df.format(test_acc*100)+"%");
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	private static void dumpModel(String path, String iter) {
		try{
			StringBuilder sb = new StringBuilder();
			// a
			for (int i=0; i<Features.m; i++)
				for (int j=0; j<Features.m; j++)
					sb.append(Features.bestiter_a[i][j] +"\t"+Features.bestiter_avg_a[i][j]+"\n");
			File f = new File(path+"/mdl_a"+iter+".zip");
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
			ZipEntry e = new ZipEntry("mdl_a"+iter+".txt");
			out.putNextEntry(e);
			byte[] data = sb.toString().getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			out.close();
			// other features
			String[] dump_names = {"b","p","q","bp","bq","p2","pp2","q2","qq2",
					"suf4","prf4","suf3","prf3","suf2","prf2","suf1","prf1",
					"dig","hyp","upr"};//,"bpq","bpp2","bqq2"};
			for (int c = 0; c<dump_names.length; c++) {
				sb = new StringBuilder();
				Features.maplist.get(c).remove("dummykey");
				for(Iterator i=Features.maplist.get(c).keySet().iterator(); i.hasNext(); ){		
					String k = (String)i.next();
					Integer v = Features.maplist.get(c).get(k);
					if ( (c > 8) ||
							Features.bestiter_featweights[c][v.intValue()]!=0 || 
							Features.bestiter_avg_featVal[c][v.intValue()]!=0)
						sb.append(k +"\t"+ v +"\t"+ 
								Features.bestiter_featweights[c][v.intValue()] +"\t"+
								Features.bestiter_avg_featVal[c][v.intValue()] +"\n");
				}
				f = new File(path+"/mdl_"+dump_names[c]+""+iter+".zip");
				out = new ZipOutputStream(new FileOutputStream(f));
				e = new ZipEntry("mdl_"+dump_names[c]+""+iter+".txt");
				out.putNextEntry(e);
				data = sb.toString().getBytes();
				out.write(data, 0, data.length);
				out.closeEntry();
				out.close();
			}
			// tau
			sb = new StringBuilder();
			sb.append(Features.m+"\n");
			for(Iterator i=Features.tau.keySet().iterator(); i.hasNext(); ){		
				Integer k = (Integer)i.next();
				String v = Features.tau.get(k);
				sb.append(k +"\t"+ v +"\n");
			}
			f = new File(path+"/mdl_tau"+iter+".zip");
			out = new ZipOutputStream(new FileOutputStream(f));
			e = new ZipEntry("mdl_tau"+iter+".txt");
			out.putNextEntry(e);
			data = sb.toString().getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			out.close();
			// wordset
			sb = new StringBuilder();
			sb.append(Features.wordset.toString());
			f = new File(path+"/mdl_wordset.zip");
			out = new ZipOutputStream(new FileOutputStream(f));
			e = new ZipEntry("mdl_wordset.txt");
			out.putNextEntry(e);
			data = sb.toString().getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			out.close();
			// rare_words
			sb = new StringBuilder();
			sb.append(Features.rare_words.toString());
			f = new File(path+"/mdl_rare_words.zip");
			out = new ZipOutputStream(new FileOutputStream(f));
			e = new ZipEntry("mdl_rare_words.txt");
			out.putNextEntry(e);
			data = sb.toString().getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			out.close();
			// tag dictionary for each word
			sb = new StringBuilder();
			for (Iterator wsetIter=Features.wordset.iterator(); wsetIter.hasNext(); ) {
				String w = (String)wsetIter.next();
				sb.append(w+"\t"+Features.tagDict.get(w).toString()+"\n");
			}
			f = new File(path+"/mdl_tagdict.zip");
			out = new ZipOutputStream(new FileOutputStream(f));
			e = new ZipEntry("mdl_tagdict.txt");
			out.putNextEntry(e);
			data = sb.toString().getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			out.close();
			System.err.println("Done");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private static void loadModel(String path, String iter) {
		try{
			// tau
			ZipFile zipFile = new ZipFile(path+"/mdl_tau.zip");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					zipFile.getInputStream(zipFile.entries().nextElement())));
			int m = Integer.parseInt(reader.readLine());
			Features.m = m;
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\t");
				Features.tau.put(new Integer(parts[0]), parts[1]);
				if (!parts[1].equals("SS") && !parts[1].equals("SE"))
					Features.alltags.add(new Integer(parts[0]));
			}
			reader.close();
			zipFile.close();
			//a
			zipFile = new ZipFile(path+"/mdl_a"+iter+".zip");
			reader = new BufferedReader(new InputStreamReader(
					zipFile.getInputStream(zipFile.entries().nextElement())));
			line = "";
			int i=0,j=0;
			Features.a = new double[m][m];
			Features.best_avg_a = new double[m][m];
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\t");
				if (j<m-1) {
					Features.a[i][j] = Double.parseDouble(parts[0]);
					Features.best_avg_a[i][j] = Double.parseDouble(parts[1]);
					j++;
				} else {
					Features.a[i][j] = Double.parseDouble(parts[0]);
					Features.best_avg_a[i][j] = Double.parseDouble(parts[1]);
					i++;
					j=0;
				}
			}
			reader.close();
			zipFile.close();
			// other features
			for (int f=0; f<Features.num_feat; f++) {
				HashMap<String, Integer> dummap = new HashMap<String, Integer>();
				dummap.put("dummykey", new Integer(-1));
				Features.maplist.add(f, dummap);
			}
			String[] dump_names = {"b","p","q","bp","bq","p2","pp2","q2","qq2",
					"suf4","prf4","suf3","prf3","suf2","prf2","suf1","prf1",
					"dig","hyp","upr"};//,"bpq","bpp2","bqq2"};
			for (int c = 0; c<dump_names.length; c++) {
				zipFile = new ZipFile(path+"/mdl_"+dump_names[c]+""+iter+".zip");
				reader = new BufferedReader(new InputStreamReader(
						zipFile.getInputStream(zipFile.entries().nextElement())));
				line = "";
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split("\t");
					Features.maplist.get(c).put(parts[0],new Integer(parts[1]));
					Features.featweights[c][Integer.parseInt(parts[1])] = Double.parseDouble(parts[2]);
					Features.best_avg_featVal[c][Integer.parseInt(parts[1])] = Double.parseDouble(parts[3]);
				}
				reader.close();
				zipFile.close();
			}
			// wordset
			zipFile = new ZipFile(path+"/mdl_wordset.zip");
			reader = new BufferedReader(new InputStreamReader(
					zipFile.getInputStream(zipFile.entries().nextElement())));
			line = reader.readLine();
			line = line.substring(1,line.length()-1);
			Features.wordset = new HashSet<String>(Arrays.asList(line.split(", ")));
			reader.close();
			zipFile.close();
			// rare_words
			zipFile = new ZipFile(path+"/mdl_rare_words.zip");
			reader = new BufferedReader(new InputStreamReader(
					zipFile.getInputStream(zipFile.entries().nextElement())));
			line = reader.readLine();
			line = line.substring(1,line.length()-1);
			Features.rare_words = new HashSet<String>(Arrays.asList(line.split(", ")));
			reader.close();
			zipFile.close();
			// tag dictionary
			zipFile = new ZipFile(path+"/mdl_tagdict.zip");
			reader = new BufferedReader(new InputStreamReader(
					zipFile.getInputStream(zipFile.entries().nextElement())));
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\t");
				String[] tags_str = parts[1].substring(1,parts[1].length()-1).split(", ");
				Integer[] tags_Int = new Integer[tags_str.length];
				for (int x = 0; x < tags_str.length; x++)
					tags_Int[x] = new Integer(tags_str[x]);
				Collection<Integer> tags = new HashSet<Integer>(Arrays.asList(tags_Int));
				Features.tagDict.put(parts[0], tags);
			}
			reader.close();
			zipFile.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private static void makeDataStructure(String train_file){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(train_file));
			String l = "";
			Set <String> tags = new HashSet<String>(); // all POS tags except SS and SE
			int labelNumber = 1;
			while ((l = reader.readLine()) !=null){
				tags.add(l.substring(l.lastIndexOf("\t")+1));
			}
			tags.remove("");
			reader.close();
			List<String> list = new ArrayList<String>(tags);
			Collections.sort(list);
			for (Iterator<String> ti =list.iterator(); ti.hasNext(); ) {
				String t = ti.next();
				Features.tag.put(t, new Integer(labelNumber));
				Features.tau.put(new Integer(labelNumber),t);
				Features.alltags.add(new Integer(labelNumber));
				labelNumber += 1;
			}
			Features.tag.put("SS", new Integer(0));
			Features.tau.put(new Integer(0),"SS");
			Features.tag.put("SE", new Integer(labelNumber));
			Features.tau.put(new Integer(labelNumber),"SE");
			String wordp2 = "";
			String wordp = "<s>";
			String wordcur = "";
			String wordq = "";
			String wordq2 = "";
			String tagcur = "";
			String line1 = "";
			String line2 = "";
			String line0 = "";

			List truth_sentences_wt = new ArrayList(); // list of training set sentences in "word tag" format
			List truth_sentences = new ArrayList(); // list of training set sentences in "w1 .. wn" fromat

			reader = new BufferedReader(new FileReader(train_file));
			line0 = reader.readLine();
			line1 = reader.readLine();
			String sentence_wt = "";
			String sentence = "";

			for (int f=0; f<Features.num_feat; f++) {
				HashMap<String, Integer> dummap = new HashMap<String, Integer>();
				dummap.put("dummykey", new Integer(-1));
				Features.maplist.add(f, dummap);
			}

			while (line1 !=null){
				if (line1.isEmpty()) {
					wordcur = line0.substring(0, line0.indexOf("\t"));
					wordq = "</s>";	
					wordq2 = "";
					tagcur = line0.substring(line0.lastIndexOf("\t")+1);
					sentence_wt += line0;
					sentence += wordcur;
					int t1 = Features.tag.get(tagcur).intValue();
					add_to_word_tag_set(wordp2,wordp,wordcur,"</s>",wordq2,t1);
					add_to_tagdictionary(wordcur,t1);
					truth_sentences_wt.add("<s>\tSS " + sentence_wt +" </s>\tSE");
					truth_sentences.add("<s> "+sentence+" </s>");
					sentence_wt = "";
					sentence = "";
					line0=reader.readLine();
					line1=reader.readLine();
					wordp = "<s>";
					wordp2 = "";
				} else {
					if ((line2=reader.readLine()).isEmpty()) {
						wordcur = line0.substring(0, line0.indexOf("\t"));
						wordq = line1.substring(0, line1.indexOf("\t"));
						wordq2 = "</s>";
						tagcur = line0.substring(line0.lastIndexOf("\t")+1);
						sentence_wt += line0 + " ";
						sentence += wordcur + " ";
						int t1 = Features.tag.get(tagcur).intValue();
						add_to_word_tag_set(wordp2,wordp,wordcur,wordq,wordq2,t1);
						add_to_tagdictionary(wordcur,t1);
						//don't skip last word of the sentence - begin
						tagcur = line1.substring(line1.lastIndexOf("\t")+1);
						sentence_wt += line1;
						sentence += line1.substring(0, line1.indexOf("\t"));
						t1 = Features.tag.get(tagcur).intValue();
						add_to_word_tag_set(wordp,line1.substring(0, line1.indexOf("\t")),wordq,"</s>","",t1);
						add_to_tagdictionary(wordq,t1);
						truth_sentences_wt.add("<s>\tSS " + sentence_wt +" </s>\tSE");
						truth_sentences.add("<s> "+sentence+" </s>");
						sentence_wt = "";
						sentence = "";
						// end
						line0=reader.readLine();
						line1=reader.readLine();
						wordp = "<s>";
						wordp2 = "";
					} else{
						wordcur = line0.substring(0, line0.indexOf("\t"));
						wordq = line1.substring(0, line1.indexOf("\t"));
						wordq2 = line2.substring(0, line2.indexOf("\t"));
						tagcur = line0.substring(line0.lastIndexOf("\t")+1);
						sentence_wt += line0 + " ";
						sentence += wordcur + " ";
						int t1 = Features.tag.get(tagcur).intValue();
						add_to_word_tag_set(wordp2,wordp,wordcur,wordq,wordq2,t1);
						add_to_tagdictionary(wordcur,t1);
						wordp2 = wordp;
						wordp = wordcur;
						line0 = line1;
						line1 = line2;
					}
				}
			}
			reader.close();
			// rare words
			for (Iterator wsetIter=Features.wordset.iterator(); wsetIter.hasNext(); ) {
				String w = (String)wsetIter.next();
				if (wordcount.get(w).intValue() < 5) {
					Features.rare_words.add(w);
				}
			}
			//assigning indices
			for(Iterator i=Features.tag.keySet().iterator(); i.hasNext(); ){		
				String tt = (String)i.next();
				Features.maplist.get(17).put(Features.tag.get(tt)+"|Y", Features.tag.get(tt));
				Features.maplist.get(17).put(Features.tag.get(tt)+"|N", new Integer(Features.tag.get(tt).intValue()+Features.tag.size()));
			}
			for(Iterator i=Features.tag.keySet().iterator(); i.hasNext(); ){		
				String tt = (String)i.next();
				Features.maplist.get(18).put(Features.tag.get(tt)+"|Y", Features.tag.get(tt));
				Features.maplist.get(18).put(Features.tag.get(tt)+"|N", new Integer(Features.tag.get(tt).intValue()+Features.tag.size()));
			}
			for(Iterator i=Features.tag.keySet().iterator(); i.hasNext(); ){		
				String tt = (String)i.next();
				Features.maplist.get(19).put(Features.tag.get(tt)+"|Y", Features.tag.get(tt));
				Features.maplist.get(19).put(Features.tag.get(tt)+"|N", new Integer(Features.tag.get(tt).intValue()+Features.tag.size()));
			}
			Features.m = Features.tag.size() - 1; //number of tags
			perceptron(truth_sentences_wt, truth_sentences, avg_flag); // averaged perceptron flag is set to true
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private static void add_to_tagdictionary(String w,int t) {
		Collection<Integer> tags = Features.tagDict.get(w);
		if (tags==null) {
			tags = new HashSet<Integer>();
		}
		tags.add(new Integer(t));
		Features.tagDict.put(w, tags);
	}

	private static void add_to_word_tag_set(String wordp2, String wordp, String wordcur, 
			String wordq, String wordq2,
			int tag1){

		Features.wordset.add(wordcur);
		int wCount = (wordcount.get(wordcur) == null) ? 1:wordcount.get(wordcur).intValue()+1;
		wordcount.put(wordcur, new Integer(wCount));

		if (!Features.maplist.get(0).containsKey(tag1+"|"+wordcur)) {
			Features.maplist.get(0).put(tag1+"|"+wordcur, new Integer(Features.bNumber++));
		}
		if (!Features.maplist.get(1).containsKey(tag1+"|"+wordp)) {
			Features.maplist.get(1).put(tag1+"|"+wordp, new Integer(Features.pNumber++));
		}
		if (!Features.maplist.get(2).containsKey(tag1+"|"+wordq)) {
			Features.maplist.get(2).put(tag1+"|"+wordq, new Integer(Features.qNumber++));
		}
		if (!Features.maplist.get(3).containsKey(tag1+"|"+wordp+" "+wordcur)) {
			Features.maplist.get(3).put(tag1+"|"+wordp+" "+wordcur, new Integer(Features.bpNumber++));
		}
		if (!Features.maplist.get(4).containsKey(tag1+"|"+wordcur+" "+wordq)) {
			Features.maplist.get(4).put(tag1+"|"+wordcur+" "+wordq, new Integer(Features.bqNumber++));
		}
		if (!Features.maplist.get(5).containsKey(tag1+"|"+wordp2)) {
			Features.maplist.get(5).put(tag1+"|"+wordp2, new Integer(Features.p2Number++));
		}
		if (!Features.maplist.get(6).containsKey(tag1+"|"+wordp2+" "+wordp)) {
			Features.maplist.get(6).put(tag1+"|"+wordp2+" "+wordp, new Integer(Features.pp2Number++));
		}
		if (!Features.maplist.get(7).containsKey(tag1+"|"+wordq2)) {
			Features.maplist.get(7).put(tag1+"|"+wordq2, new Integer(Features.q2Number++));
		}
		if (!Features.maplist.get(8).containsKey(tag1+"|"+wordq+" "+wordq2)) {
			Features.maplist.get(8).put(tag1+"|"+wordq+" "+wordq2, new Integer(Features.qq2Number++));
		}
		/*if (!Features.maplist.get(20).containsKey(tag1+"|"+wordcur+" "+wordp+" "+wordq)) {
				Features.maplist.get(20).put(tag1+"|"+wordcur+" "+wordp+" "+wordq, new Integer(Features.bpqNumber++));
			}

			if (!Features.maplist.get(21).containsKey(tag1+"|"+wordcur+" "+wordp+" "+wordp2)) {
				Features.maplist.get(21).put(tag1+"|"+wordcur+" "+wordp+" "+wordp2, new Integer(Features.bpp2Number++));
			}

			if (!Features.maplist.get(22).containsKey(tag1+"|"+wordcur+" "+wordq+" "+wordq2)) {
				Features.maplist.get(22).put(tag1+"|"+wordcur+" "+wordq+" "+wordq2, new Integer(Features.bqq2Number++));
			}*/
		if (wordcur.length() >= 4) {
			if (!Features.maplist.get(9).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-4, wordcur.length()))) {
				Features.maplist.get(9).put(tag1+"|"+wordcur.substring(wordcur.length()-4, wordcur.length()), new Integer(Features.suf4Number++));
			}
			if (!Features.maplist.get(10).containsKey(tag1+"|"+wordcur.substring(0,4))) {
				Features.maplist.get(10).put(tag1+"|"+wordcur.substring(0,4), new Integer(Features.pref4Number++));
			}
			if (!Features.maplist.get(11).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-3, wordcur.length()))) {
				Features.maplist.get(11).put(tag1+"|"+wordcur.substring(wordcur.length()-3, wordcur.length()), new Integer(Features.suf3Number++));
			}
			if (!Features.maplist.get(12).containsKey(tag1+"|"+wordcur.substring(0,3))) {
				Features.maplist.get(12).put(tag1+"|"+wordcur.substring(0,3), new Integer(Features.pref3Number++));
			}
			if (!Features.maplist.get(13).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-2, wordcur.length()))) {
				Features.maplist.get(13).put(tag1+"|"+wordcur.substring(wordcur.length()-2, wordcur.length()), new Integer(Features.suf2Number++));
			}
			if (!Features.maplist.get(14).containsKey(tag1+"|"+wordcur.substring(0,2))) {
				Features.maplist.get(14).put(tag1+"|"+wordcur.substring(0,2), new Integer(Features.pref2Number++));
			}
			if (!Features.maplist.get(15).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()))) {
				Features.maplist.get(15).put(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()), new Integer(Features.suf1Number++));
			}
			if (!Features.maplist.get(16).containsKey(tag1+"|"+wordcur.substring(0,1))) {
				Features.maplist.get(16).put(tag1+"|"+wordcur.substring(0,1), new Integer(Features.pref1Number++));
			}
		}
		else if (wordcur.length() >= 3) {
			if (!Features.maplist.get(11).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-3, wordcur.length()))) {
				Features.maplist.get(11).put(tag1+"|"+wordcur.substring(wordcur.length()-3, wordcur.length()), new Integer(Features.suf3Number++));
			}
			if (!Features.maplist.get(12).containsKey(tag1+"|"+wordcur.substring(0,3))) {
				Features.maplist.get(12).put(tag1+"|"+wordcur.substring(0,3), new Integer(Features.pref3Number++));
			}
			if (!Features.maplist.get(13).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-2, wordcur.length()))) {
				Features.maplist.get(13).put(tag1+"|"+wordcur.substring(wordcur.length()-2, wordcur.length()), new Integer(Features.suf2Number++));
			}
			if (!Features.maplist.get(14).containsKey(tag1+"|"+wordcur.substring(0,2))) {
				Features.maplist.get(14).put(tag1+"|"+wordcur.substring(0,2), new Integer(Features.pref2Number++));
			}
			if (!Features.maplist.get(15).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()))) {
				Features.maplist.get(15).put(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()), new Integer(Features.suf1Number++));
			}
			if (!Features.maplist.get(16).containsKey(tag1+"|"+wordcur.substring(0,1))) {
				Features.maplist.get(16).put(tag1+"|"+wordcur.substring(0,1), new Integer(Features.pref1Number++));
			}
		} else if (wordcur.length() >= 2) {
			if (!Features.maplist.get(13).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-2, wordcur.length()))) {
				Features.maplist.get(13).put(tag1+"|"+wordcur.substring(wordcur.length()-2, wordcur.length()), new Integer(Features.suf2Number++));
			}
			if (!Features.maplist.get(14).containsKey(tag1+"|"+wordcur.substring(0,2))) {
				Features.maplist.get(14).put(tag1+"|"+wordcur.substring(0,2), new Integer(Features.pref2Number++));
			}
			if (!Features.maplist.get(15).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()))) {
				Features.maplist.get(15).put(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()), new Integer(Features.suf1Number++));
			}
			if (!Features.maplist.get(16).containsKey(tag1+"|"+wordcur.substring(0,1))) {
				Features.maplist.get(16).put(tag1+"|"+wordcur.substring(0,1), new Integer(Features.pref1Number++));
			}
		} else { // (wordcur.length() >= 1) 
			if (!Features.maplist.get(15).containsKey(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()))) {
				Features.maplist.get(15).put(tag1+"|"+wordcur.substring(wordcur.length()-1, wordcur.length()), new Integer(Features.suf1Number++));
			}
			if (!Features.maplist.get(16).containsKey(tag1+"|"+wordcur.substring(0,1))) {
				Features.maplist.get(16).put(tag1+"|"+wordcur.substring(0,1), new Integer(Features.pref1Number++));
			}
		}
	}

	private static void perceptron(List truth_sentences_wt, List truth_sentences, boolean averaged){

		int count = 0;
		int t_itruth=-1, t_iguess=-1, t_iminus1truth=-1, t_iminus1guess=-1;
		String w_iminus1_en="", w_iplus1_en="", w_iminus2_en="", w_iplus2_en="";
		//initialize a
		Features.a = new double[Features.m][Features.m];
		Features.best_avg_a = new double[Features.m][Features.m];
		Features.sum_a = new double[Features.m][Features.m];
		Features.changes_a = new double[Features.m][Features.m];
		Features.bestiter_a = new double[Features.m][Features.m];
		Features.bestiter_avg_a = new double[Features.m][Features.m];
		int t = 0;
		int noimprove = 0; // number of iterations wo acc improvement
		while (t<max_iter) {
			if (noimprove < 5) {
				long startTime = System.currentTimeMillis();
				System.err.println("Iteration "+ (t+1));
				for (int i=0; i<truth_sentences_wt.size(); i++) {
					String truth = (String)truth_sentences_wt.get(i);
					String[] truth_wt = truth.split(" ");
					String w_i;
					count = (truth_sentences_wt.size()*t) + (i+1);
					// call viterbi to tag sentence based on current parameters
					// test is set to false to indicate we're in training phase
					String guess = "";
					guess = Features.viterbi((String)truth_sentences.get(i), false, "dummy", false);
					String[] guess_wt = guess.split(" ");
					if (!truth_wt.equals(guess_wt)) {
						for (int n=1; n<=truth_wt.length-2; n++) {
							t_itruth = Features.tag.get(truth_wt[n].substring(truth_wt[n].indexOf("\t")+1));
							w_i = truth_wt[n].substring(0,truth_wt[n].indexOf("\t"));
							t_iminus1truth = Features.tag.get(truth_wt[n-1].substring(truth_wt[n-1].indexOf("\t")+1));
							w_iminus1_en = truth_wt[n-1].substring(0,truth_wt[n-1].indexOf("\t"));
							w_iplus1_en = truth_wt[n+1].substring(0,truth_wt[n+1].indexOf("\t"));
							if (n < truth_wt.length-2)	{
								w_iplus2_en = truth_wt[n+2].substring(0,truth_wt[n+2].indexOf("\t"));
							}
							if (n>=2)
								w_iminus2_en = truth_wt[n-2].substring(0,truth_wt[n-2].indexOf("\t"));
							Features ftruth = new Features(w_i,w_iminus1_en,w_iplus1_en,w_iminus2_en,
									w_iplus2_en,t_itruth,t_iminus1truth);
							Features.updatefeats(ftruth, 1);

							t_iguess = Features.tag.get(guess_wt[n].substring(guess_wt[n].indexOf("\t")+1));
							t_iminus1guess = Features.tag.get(guess_wt[n-1].substring(guess_wt[n-1].indexOf("\t")+1));
							add_to_word_tag_set(w_iminus2_en,w_iminus1_en,w_i,w_iplus1_en,w_iplus2_en,t_iguess);
							Features fguess = new Features(w_i,w_iminus1_en,w_iplus1_en,w_iminus2_en,
									w_iplus2_en,t_iguess,t_iminus1guess);
							Features.updatefeats(fguess, -1);

							if (averaged) {
								Features.average_feats(ftruth,count);
								Features.average_feats(fguess,count);
							}
						}
					}
				}
				long estimatedTime = System.currentTimeMillis() - startTime;
				System.err.println("    Time elapsed: "+estimatedTime+" ms");

				double curr_acc = evaluate_dev(count, t);
				System.err.println("    Accuracy: "+curr_acc);
				if (curr_acc > best_acc_so_far) {
					best_acc_so_far = curr_acc;
					best_iter_so_far = (t+1);
					noimprove = 0;
				} else {
					noimprove++;
				}
				System.err.println("Best accuracy (iteration "+ best_iter_so_far +"): "+ best_acc_so_far);
				t++;
			} else 
				return;
		}
		if (averaged) {
			Features.final_average(count);
		}
	}

	private static double evaluate_dev(int count, int t) {
		// save before final average values
		double[][] tmp_featweights = new double[Features.num_feat][600000];
		double[][] tmp_best_avg_featVal = new double[Features.num_feat][600000];
		double[][] tmp_sum_featVal = new double[Features.num_feat][600000];
		double[][] tmp_changes_featVal = new double[Features.num_feat][600000];
		for (int ii = 0; ii < Features.num_feat; ii++) {
			tmp_featweights[ii] = Arrays.copyOf(Features.featweights[ii], Features.featweights[ii].length);
			tmp_best_avg_featVal[ii] = Arrays.copyOf(Features.best_avg_featVal[ii], Features.best_avg_featVal[ii].length);
			tmp_sum_featVal[ii] = Arrays.copyOf(Features.sum_featVal[ii], Features.sum_featVal[ii].length);
			tmp_changes_featVal[ii] = Arrays.copyOf(Features.changes_featVal[ii], Features.changes_featVal[ii].length);
		}
		double[][] tmp_a = new double[Features.m][Features.m];
		double[][] tmp_best_avg_a = new double[Features.m][Features.m];
		double[][] tmp_sum_a = new double[Features.m][Features.m];
		double[][] tmp_changes_a = new double[Features.m][Features.m];
		for (int ii = 0; ii < Features.m; ii++) {
			tmp_a[ii] = Arrays.copyOf(Features.a[ii], Features.a[ii].length);
			tmp_best_avg_a[ii] = Arrays.copyOf(Features.best_avg_a[ii], Features.best_avg_a[ii].length);
			tmp_sum_a[ii] = Arrays.copyOf(Features.sum_a[ii], Features.sum_a[ii].length);
			tmp_changes_a[ii] = Arrays.copyOf(Features.changes_a[ii], Features.changes_a[ii].length);
		}
		// final average
		Features.final_average(count);
		try {
			BufferedReader test_file = new BufferedReader(new FileReader(dev_file));
			String sentence = "";
			String line = null;
			while ((line=test_file.readLine()) != null) {
				if (line.isEmpty()) {
					Features.viterbi("<s> "+sentence.substring(0,sentence.length()-1)+" </s>", true, mdl_path+"/dev_output_"+ (t+1), avg_flag);
					sentence = null;
				} else {
					sentence += line.substring(0,line.indexOf("\t")) + " ";
				}
			}
			test_file.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		double dev_acc = tagging_accuracy(dev_file, mdl_path+"/dev_output_"+ (t+1));
		if (dev_acc > best_acc_so_far) {
			Features.bestiter_a = Features.a;
			Features.bestiter_featweights = Features.featweights;
			Features.bestiter_avg_a = Features.best_avg_a;
			Features.bestiter_avg_featVal = Features.best_avg_featVal;
		}
		// restore before evaluation values
		Features.featweights = tmp_featweights;
		Features.best_avg_featVal = tmp_best_avg_featVal;
		Features.sum_featVal = tmp_sum_featVal;
		Features.changes_featVal = tmp_changes_featVal;
		Features.a = tmp_a;
		Features.best_avg_a = tmp_best_avg_a;
		Features.sum_a = tmp_sum_a;
		Features.changes_a = tmp_changes_a;
		return dev_acc;
	}

	private static double tagging_accuracy(String ref, String tagged) {
		double accuracy = 0.0;
		try{
			BufferedReader reader1 = new BufferedReader(new FileReader(ref));
			BufferedReader reader2 = new BufferedReader(new FileReader(tagged));
			String line1 = "";
			String line2 = "";
			double difference = 0.0;
			double total = 0.0;
			String prevline1 = "";
			while ((line1 = reader1.readLine()) != null && (line2=reader2.readLine())!= null) {
				if (!line1.isEmpty() && !prevline1.isEmpty())
				{
					total++;
					if (!line1.equals(line2)) {
						difference++;
					}
				}
				prevline1 = line1;
			}
			//System.err.println("total: "+ total);
			//System.err.println("difference: "+ difference);
			accuracy = (total - difference)/total;
			reader1.close();
			reader2.close();
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return accuracy;
	}
}
