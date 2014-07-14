package tagPack;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.lang.Integer;

public class Features{
	public static int ti;
	public static int ti_1;
	public static int b; // ti~wi
	public static int suff4; // ti~wilast4chars
	public static int suff3; // ti~wilast3chars
	public static int suff2; // ti~wilast2chars
	public static int suff1; // ti~wilast1chars
	public static int pref4; // ti~wifirst4chars
	public static int pref3; // ti~wifirst3chars
	public static int pref2; // ti~wifirst2chars
	public static int pref1; // ti~wifirst1chars
	public static int p; // ti~wi-1
	public static int bp; // ti~wi-1,wi
	public static int bq; // ti~wi,wi+1
	public static int q; // ti~wi+1
	public static int p2; // ti~wi-2
	public static int q2; // ti~wi+2
	public static int pp2; // ti~wi-2,wi-1
	public static int qq2; // ti~wi+1,wi+2
	//	public static int bpq; // ti~wi-1,wi,wi+1
	//	public static int bpp2; // ti~wi-2,wi-1,wi
	//	public static int bqq2; // ti~wi,wi+1,wi+2
	public static int dig; //ti of wi containing digit
	public static int hyp; //ti of wi containing hyphen
	public static int upr; //ti of wi containing an upper-case letter

	public static HashMap<String, Integer> tag = new HashMap<String, Integer>();
	public static HashMap<Integer, String> tau = new HashMap<Integer, String>();
	public static Set<String> rare_words = new HashSet<String>();
	public static Set<String> wordset = new HashSet<String>(); // set of training words
	public static HashMap<String, Collection<Integer>> tagDict = new HashMap<String, Collection<Integer>>();
	public static Collection<Integer> alltags = new HashSet<Integer>();
	public static HashMap<String, Integer> featMap = new HashMap<String, Integer>();
	public static int num_feat = 20; // number of feature templates
	public static ArrayList[] featVal = new ArrayList[num_feat];
	public static double[][] featweights = new double[num_feat][600000];
	public static double[][] best_avg_featVal = new double[num_feat][600000];
	public static double[][] sum_featVal = new double[num_feat][600000];
	public static double[][] changes_featVal = new double[num_feat][600000];
	public static double[][] bestiter_featweights = new double[num_feat][600000];
	public static double[][] bestiter_avg_featVal = new double[num_feat][600000];
	public static double[][] a; // ti-1~ti
	public static double[][] bestiter_a;
	public static double[][] best_avg_a;
	public static double[][] bestiter_avg_a;
	public static double[][] sum_a;
	public static double[][] changes_a;
	public static int[] feat_count = new int[num_feat]; // number of each feature
	public static int m;
	public static int pp2Number;
	public static int bpp2Number;
	public static int bqq2Number;
	public static int q2Number;
	public static int bpNumber;
	public static int bqNumber;
	public static int bpqNumber;
	public static int qq2Number;
	public static int p2Number;
	public static int pNumber;
	public static int bNumber;
	public static int qNumber;
	public static int q2bNumber;
	public static int suf4Number;
	public static int suf3Number;
	public static int suf2Number;
	public static int suf1Number;
	public static int pref4Number;
	public static int pref3Number;
	public static int pref2Number;
	public static int pref1Number;
	public static HashMap<String, Integer> word_4suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_3suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_2suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_1suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_4preffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_3preffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_2preffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_1preffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word = new HashMap<String, Integer>();
	public static List<HashMap<String, Integer>> maplist = new ArrayList<HashMap<String, Integer>>();

	public Features() {super();}
	public Features(String w_i,String w_iminus1_en,String w_iplus1_en,String w_iminus2_en,
			String w_iplus2_en,int t_i,int t_iminus1) {
		ti = t_i;
		ti_1 = t_iminus1;
		if (maplist.get(0).containsKey(t_i +"|"+ w_i))
			b = maplist.get(0).get(t_i +"|"+ w_i);
		else b = -1;
		if (maplist.get(1).containsKey(t_i +"|"+ w_iminus1_en))
			p = maplist.get(1).get(t_i +"|"+ w_iminus1_en);
		else p = -1;
		if (maplist.get(2).containsKey(t_i +"|"+ w_iplus1_en))
			q = maplist.get(2).get(t_i +"|"+ w_iplus1_en);
		else q = -1;
		if (maplist.get(3).containsKey(t_i +"|"+ w_iminus1_en +" "+ w_i))
			bp = maplist.get(3).get(t_i +"|"+ w_iminus1_en +" "+ w_i);
		else bp = -1;
		if (maplist.get(4).containsKey(t_i +"|"+ w_i +" "+ w_iplus1_en))
			bq = maplist.get(4).get(t_i +"|"+ w_i +" "+ w_iplus1_en);
		else bq = -1;
		if (maplist.get(5).containsKey(t_i +"|"+ w_iminus2_en))
			p2 = maplist.get(5).get(t_i +"|"+ w_iminus2_en);
		else p2 = -1;
		if (maplist.get(6).containsKey(t_i +"|"+ w_iminus2_en+" "+ w_iminus1_en))
			pp2 = maplist.get(6).get(t_i +"|"+ w_iminus2_en+" "+ w_iminus1_en);
		else pp2 = -1;
		if (maplist.get(7).containsKey(t_i +"|"+ w_iplus2_en))
			q2 = maplist.get(7).get(t_i +"|"+ w_iplus2_en);
		else q2 = -1;
		if (maplist.get(8).containsKey(t_i +"|"+ w_iplus1_en+" "+ w_iplus2_en))
			qq2 = maplist.get(8).get(t_i +"|"+ w_iplus1_en+" "+ w_iplus2_en);
		else qq2 = -1;
		/*if (maplist.get(20).containsKey(t_i +"|"+ w_i+" "+w_iminus1_en+" "+ w_iplus1_en))
		bpq = maplist.get(20).get(t_i +"|"+ w_i+" "+w_iminus1_en+" "+ w_iplus1_en);
	else bpq = -1;
	if (maplist.get(21).containsKey(t_i +"|"+ w_i+" "+w_iminus1_en+" "+ w_iminus2_en))
		bpp2 = maplist.get(21).get(t_i +"|"+ w_i+" "+w_iminus1_en+" "+ w_iminus2_en);
	else bpp2 = -1;
	if (maplist.get(22).containsKey(t_i +"|"+ w_i+" "+w_iplus1_en+" "+ w_iplus2_en))
		bqq2 = maplist.get(22).get(t_i +"|"+ w_i+" "+w_iplus1_en+" "+ w_iplus2_en);
	else bqq2 = -1;*/
		dig = -1;
		hyp = -1;
		upr = -1;
		suff4 = -1;
		suff3 = -1;
		suff2 = -1;
		suff1 = -1;
		pref4 = -1;
		pref3 = -1;
		pref2 = -1;
		pref1 = -1;
		if (w_i.matches(".*\\d+.*"))
			dig = maplist.get(17).get(t_i +"|Y");
		else
			dig = maplist.get(17).get(t_i +"|N");
		if (w_i.matches(".*-+.*"))
			hyp = maplist.get(18).get(t_i +"|Y");
		else
			hyp = maplist.get(18).get(t_i +"|N");
		if (w_i.matches(".*[A-Z]+.*"))
			upr = maplist.get(19).get(t_i +"|Y");
		else
			upr = maplist.get(19).get(t_i +"|N");
		int wilen = w_i.length();
		if (wilen >= 4) {
			if (maplist.get(9).containsKey(t_i +"|"+w_i.substring(wilen-4, wilen)))
				suff4 = maplist.get(9).get(t_i +"|"+w_i.substring(wilen-4, wilen));
			if (maplist.get(11).containsKey(t_i +"|"+w_i.substring(wilen-3, wilen)))
				suff3 = maplist.get(11).get(t_i +"|"+w_i.substring(wilen-3, wilen));
			if (maplist.get(13).containsKey(t_i +"|"+w_i.substring(wilen-2, wilen)))
				suff2 = maplist.get(13).get(t_i +"|"+w_i.substring(wilen-2, wilen));
			if (maplist.get(15).containsKey(t_i +"|"+w_i.substring(wilen-1, wilen)))
				suff1 = maplist.get(15).get(t_i +"|"+w_i.substring(wilen-1, wilen));
			if (maplist.get(10).containsKey(t_i +"|"+w_i.substring(0,4)))
				pref4 = maplist.get(10).get(t_i +"|"+w_i.substring(0,4));
			if (maplist.get(12).containsKey(t_i +"|"+w_i.substring(0,3)))
				pref3 = maplist.get(12).get(t_i +"|"+w_i.substring(0,3));
			if (maplist.get(14).containsKey(t_i +"|"+w_i.substring(0,2)))
				pref2 = maplist.get(14).get(t_i +"|"+w_i.substring(0,2));
			if (maplist.get(16).containsKey(t_i +"|"+w_i.substring(0,1)))
				pref1 = maplist.get(16).get(t_i +"|"+w_i.substring(0,1));
		} else if (wilen >= 3) {
			if (maplist.get(11).containsKey(t_i +"|"+w_i.substring(wilen-3, wilen)))
				suff3 = maplist.get(11).get(t_i +"|"+w_i.substring(wilen-3, wilen));
			if (maplist.get(13).containsKey(t_i +"|"+w_i.substring(wilen-2, wilen)))
				suff2 = maplist.get(13).get(t_i +"|"+w_i.substring(wilen-2, wilen));
			if (maplist.get(15).containsKey(t_i +"|"+w_i.substring(wilen-1, wilen)))
				suff1 = maplist.get(15).get(t_i +"|"+w_i.substring(wilen-1, wilen));
			if (maplist.get(12).containsKey(t_i +"|"+w_i.substring(0,3)))
				pref3 = maplist.get(12).get(t_i +"|"+w_i.substring(0,3));
			if (maplist.get(14).containsKey(t_i +"|"+w_i.substring(0,2)))
				pref2 = maplist.get(14).get(t_i +"|"+w_i.substring(0,2));
			if (maplist.get(16).containsKey(t_i +"|"+w_i.substring(0,1)))
				pref1 = maplist.get(16).get(t_i +"|"+w_i.substring(0,1));
		} else if (wilen >= 2) {
			if (maplist.get(13).containsKey(t_i +"|"+w_i.substring(wilen-2, wilen)))
				suff2 = maplist.get(13).get(t_i +"|"+w_i.substring(wilen-2, wilen));
			if (maplist.get(15).containsKey(t_i +"|"+w_i.substring(wilen-1, wilen)))
				suff1 = maplist.get(15).get(t_i +"|"+w_i.substring(wilen-1, wilen));
			if (maplist.get(14).containsKey(t_i +"|"+w_i.substring(0,2)))
				pref2 = maplist.get(14).get(t_i +"|"+w_i.substring(0,2));
			if (maplist.get(16).containsKey(t_i +"|"+w_i.substring(0,1)))
				pref1 = maplist.get(16).get(t_i +"|"+w_i.substring(0,1));
		} else { // w_i.length() >= 1
			if (maplist.get(15).containsKey(t_i +"|"+w_i.substring(wilen-1, wilen)))
				suff1 = maplist.get(15).get(t_i +"|"+w_i.substring(wilen-1, wilen));
			if (maplist.get(16).containsKey(t_i +"|"+w_i.substring(0,1)))
				pref1 = maplist.get(16).get(t_i +"|"+w_i.substring(0,1));
		}
	}

	int getTi() {return ti;}
	int getTi_1() {return ti_1;}
	int getB() {return b;}
	int getSuff4() {return suff4;}
	int getSuff3() {return suff3;}
	int getSuff2() {return suff2;}
	int getSuff1() {return suff1;}
	int getPref4() {return pref4;}
	int getPref3() {return pref3;}
	int getPref2() {return pref2;}
	int getPref1() {return pref1;}
	int getP() {return p;}
	int getQ() {return q;}
	int getP2() {return p2;}
	int getQ2() {return q2;}
	int getBq() {return bq;}
	int getBp() {return bp;}
	int getPp2() {return pp2;}
	int getQq2() {return qq2;}
	//	int getBpq() {return bpq;}
	//	int getBpp2() {return bpp2;}
	//	int getBqq2() {return bqq2;}
	int getDig() {return dig;}
	int getUpr() {return upr;}
	int getHyp() {return hyp;}

	public static void use_averaged_feats() {
		featweights = best_avg_featVal;
		a = best_avg_a;
	}

	public static void updatefeats(Features ft, int dir) {
		int feat_idxs[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};//,20,21,22};
		int feat_types[] = {ft.getB(),ft.getP(),ft.getQ(),ft.getBp(),
				ft.getBq(),ft.getP2(),ft.getPp2(),ft.getQ2(),ft.getQq2(),
				ft.getSuff4(),ft.getPref4(),ft.getSuff3(),ft.getPref3(),
				ft.getSuff2(),ft.getPref2(),ft.getSuff1(),ft.getPref1(),
				ft.getDig(),ft.getHyp(),ft.getUpr()};//,ft.getBpq(),ft.getBpp2(),ft.getBqq2()};
		for (int i=0; i<feat_idxs.length; i++) {
			int key = feat_types[i];
			if (key != -1)	{
				featweights[i][key] += dir;
			}
		}
		a[ft.getTi_1()][ft.getTi()] += dir;
	}

	public static void average_feats(Features ft, int count){
		int feat_idxs[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};//,20,21,22};
		int feat_types[] = {ft.getB(),ft.getP(),ft.getQ(),ft.getBp(),
				ft.getBq(),ft.getP2(),ft.getPp2(),ft.getQ2(),ft.getQq2(),
				ft.getSuff4(),ft.getPref4(),ft.getSuff3(),ft.getPref3(),
				ft.getSuff2(),ft.getPref2(),ft.getSuff1(),ft.getPref1(),
				ft.getDig(),ft.getHyp(),ft.getUpr()};//,ft.getBpq(),ft.getBpp2(),ft.getBqq2()};	

		for (int i=0; i<feat_idxs.length; i++) {
			int key = feat_types[i];
			if (key != -1) {
				sum_featVal[i][key] += (count-changes_featVal[i][key])*featweights[i][key];
				best_avg_featVal[i][key] = sum_featVal[i][key] / count;
				changes_featVal[i][key] = count;
			}
		}
		//average a
		sum_a[ft.getTi_1()][ft.getTi()] += (count-changes_a[ft.getTi_1()][ft.getTi()])*a[ft.getTi_1()][ft.getTi()];
		best_avg_a[ft.getTi_1()][ft.getTi()] = sum_a[ft.getTi_1()][ft.getTi()] / (count);
		changes_a[ft.getTi_1()][ft.getTi()] = count;
	}

	public static void final_average(int count){
		int feat_idxs[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};//,20,21,22};
		for (int i=0; i<feat_idxs.length; i++) {
			for (int j=0; j<600000; j++) {
				sum_featVal[i][j] += (count-changes_featVal[i][j])*featweights[i][j];
				best_avg_featVal[i][j] = sum_featVal[i][j] / count;
				changes_featVal[i][j] = count;
			}
		}
		for (int ii=0; ii<m; ii++)
			for (int j=0; j<m; j++) {
				sum_a[j][ii] += (count-changes_a[j][ii])*a[j][ii];
				best_avg_a[j][ii] = sum_a[j][ii] / (count);
				changes_a[j][ii] = count;
			}
	}

	public static String viterbi(String sentence, boolean test, String output_file, boolean use_avg){

		String[] sentence_word = sentence.split(" "); //split sentence into its words
		int n = sentence_word.length-2;
		double[][] alpha = new double[m][n+2];
		int[][] zeta = new int[m][n+2];
		int[] rho = new int[n+2];
		alpha[0][0] = 0;
		// building zeta and alpha matrices
		for (int t=1; t<=n ; t++) {
			Collection<Integer> tagset = Features.tagDict.get(sentence_word[t]);
			if (tagset == null || rare_words.contains(sentence_word[t]))
				tagset = alltags;
			for (Iterator iterator = tagset.iterator(); iterator.hasNext();) {
				Integer j_obj = (Integer) iterator.next();
				int j = j_obj.intValue();
				String wtplus2 = "";
				String wtminus2 = "";
				if (t<=n-2) {
					wtplus2 = sentence_word[t+2];
				}
				if (t>=2) {
					wtminus2 = sentence_word[t-2];
				}
				Features ft = new Features(sentence_word[t],sentence_word[t-1],
						sentence_word[t+1],wtminus2,
						wtplus2,j,-1);
				double times_all = times_all_feats(ft, use_avg);
				Object[] alpha_zeta = new Object[2];
				alpha_zeta = findMax(alpha, t, j, times_all, use_avg);
				zeta[j][t] = ((Integer)alpha_zeta[0]).intValue();
				alpha[j][t] = ((Double)alpha_zeta[1]).doubleValue();
			}
		}
		zeta[0][n+1] = findZeta0nplus1(alpha, n, use_avg);
		rho[n+1] = 0;
		// building rho array
		for (int t=n; t>=1; t--) {
			rho[t] = zeta[rho[t+1]][t+1];
		}
		// writting words of the sentence and their corresponding tags into the output file
		String tagged_sentence = "";
		try {
			if (test) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(output_file, true));
				for (int i=1; i<=n; i++) {
					String assigned_tag = tau.get(new Integer(rho[i]));
					writer.write(sentence_word[i] +"\t"+ assigned_tag + "\n");
					if (i==n)
						writer.write("\n");
				}
				writer.close();
			} else {
				for (int i=1; i<=n; i++) {
					String assigned_tag = tau.get(new Integer(rho[i]));
					tagged_sentence += sentence_word[i] +"\t"+ assigned_tag +" ";
				}
			}
		}
		catch (Exception ex) {
		}
		return "<s>\tSS " + tagged_sentence + "</s>\tSE";
	}

	private static Object[] findMax(double[][] alpha, int t, int j, double times_all, boolean use_avg){
		Object[] return_values = new Object[2];
		int max_index = 0;
		double a0j = use_avg ? best_avg_a[0][j] : a[0][j];
		double max= (alpha[0][t-1])+a0j+times_all;
		for (int i=1; i<m; i++) {
			double potential_max = max;
			double aij = use_avg ? best_avg_a[i][j] : a[i][j];
			potential_max = (alpha[i][t-1])+aij+times_all;
			if (potential_max > max) {
				max_index = i;
				max = potential_max;
			}
		}
		return_values[0] = max_index; //zeta
		return_values[1] = max; //alpha
		return return_values;
	}

	private static int findZeta0nplus1(double[][] alpha, int n, boolean use_avg){
		int max_index = 0;
		double a00 = use_avg ? best_avg_a[0][0] : a[0][0];
		double max = (alpha[0][n])+a00;
		for (int i=1; i<m; i++) {
			double potential_max = max;
			double ai0 = use_avg ? best_avg_a[i][0] : a[i][0];
			potential_max = (alpha[i][n])+ai0;
			if (potential_max > max) {
				max_index = i;
				max = potential_max;
			}
		}
		return max_index;
	}

	private static double times_all_feats(Features ft, boolean use_avg) {
		int feat_idxs[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};//,20,21,22};
		int feat_types[] = {ft.getB(),ft.getP(),ft.getQ(),ft.getBp(),
				ft.getBq(),ft.getP2(),ft.getPp2(),ft.getQ2(),ft.getQq2(),
				ft.getSuff4(),ft.getPref4(),ft.getSuff3(),ft.getPref3(),
				ft.getSuff2(),ft.getPref2(),ft.getSuff1(),ft.getPref1(),
				ft.getDig(),ft.getHyp(),ft.getUpr()};//,ft.getBpq(),ft.getBpp2(),ft.getBqq2()};
		double ret = 0;
		for (int i=0; i<feat_idxs.length; i++) {
			int key = feat_types[i];
			if (key != -1)
				ret += use_avg ? best_avg_featVal[i][key] : featweights[i][key];
		}
		return ret; 
	}
}
