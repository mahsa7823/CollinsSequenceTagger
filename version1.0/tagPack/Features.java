package tagPack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.Integer;

public class Features{
	public static HashMap<String, Integer> tag = new HashMap<String, Integer>();
	public static HashMap<Integer, String> tau = new HashMap<Integer, String>();
	public static HashMap<String, Integer> word = new HashMap<String, Integer>();
	public static HashMap<String, Integer> wordpair = new HashMap<String, Integer>();
	public static Set<String> rare_words = new HashSet<String>();
	
	public static double del;
	public static double[][] a; // ti-1~ti
	public static double[][] b; // ti~wi
	public static double[][] suff4; // ti~wilast4chars
	public static double[][] suff3; // ti~wilast3chars
	public static double[][] suff2; // ti~wilast2chars
	public static double[][] suff1; // ti~wilast1chars
	public static double[][] pref4; // ti~wifirst4chars
	public static double[][] pref3; // ti~wifirst3chars
	public static double[][] pref2; // ti~wifirst2chars
	public static double[][] pref1; // ti~wifirst1chars
	public static double[][] p; // ti~wi-1
	public static double[][] bp; // ti~wi-1,wi
	public static double[][] bq; // ti~wi,wi+1
	public static double[][] q; // ti~wi+1
	public static double[][] p2; // ti~wi-2
	public static double[][] q2; // ti~wi+2
	public static double[][] pp2; // ti~wi-2,wi-1
	public static double[][] qq2; // ti~wi+1,wi+2
	public static double[] dig; //ti of wi containing digit
	public static double[] hyp; //ti of wi containing hyphen
	public static double[] upr; //ti of wi containing an upper-case letter
	
	public static double[][] best_avg_a;
	public static double [][] best_avg_b;
	public static double [][] best_avg_suff4;
	public static double [][] best_avg_suff3;
	public static double [][] best_avg_suff2;
	public static double [][] best_avg_suff1;
	public static double [][] best_avg_pref4;
	public static double [][] best_avg_pref3;
	public static double [][] best_avg_pref2;
	public static double [][] best_avg_pref1;
	public static double [][] best_avg_p;
	public static double [][] best_avg_q;
	public static double [][] best_avg_p2;
	public static double [][] best_avg_q2;
	public static double [][] best_avg_bp;
	public static double [][] best_avg_bq;
	public static double [][] best_avg_pp2;
	public static double [][] best_avg_qq2;
	public static double[] best_avg_dig;
	public static double[] best_avg_hyp;
	public static double[] best_avg_upr;
	
	public static double[][] sum_a;
	public static double [][] sum_b;
	public static double [][] sum_suff4;
	public static double [][] sum_suff3;
	public static double [][] sum_suff2;
	public static double [][] sum_suff1;
	public static double [][] sum_pref4;
	public static double [][] sum_pref3;
	public static double [][] sum_pref2;
	public static double [][] sum_pref1;
	public static double [][] sum_p;
	public static double [][] sum_q;
	public static double [][] sum_p2;
	public static double [][] sum_q2;
	public static double [][] sum_pp2;
	public static double [][] sum_qq2;
	public static double [][] sum_bp;
	public static double [][] sum_bq;
	public static double[] sum_dig;
	public static double[] sum_upr;
	public static double[] sum_hyp;
	
	public static double[][] changes_a;
	public static double [][] changes_b;
	public static double [][] changes_suff4;
	public static double [][] changes_suff3;
	public static double [][] changes_suff2;
	public static double [][] changes_suff1;
	public static double [][] changes_pref4;
	public static double [][] changes_pref3;
	public static double [][] changes_pref2;
	public static double [][] changes_pref1;
	public static double [][] changes_p;
	public static double [][] changes_q;
	public static double [][] changes_p2;
	public static double [][] changes_q2;
	public static double [][] changes_bp;
	public static double [][] changes_bq;
	public static double [][] changes_pp2;
	public static double [][] changes_qq2;
	public static double[] changes_dig;
	public static double[] changes_hyp;
	public static double[] changes_upr;
	
		public static HashMap<String, Integer> word_4suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_3suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_2suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_1suffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_4preffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_3preffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_2preffix = new HashMap<String, Integer>();
	public static HashMap<String, Integer> word_1preffix = new HashMap<String, Integer>();
	
	public static boolean suf_flag = false;
	public static boolean pref_flag = false;
	public static boolean prev_flag = false;
	public static boolean next_flag = false;
	public static boolean dig_flag = false;
	public static boolean bprevnext_flag = false;
	
	public static int m;
	public static int wordlabelNumber;
	public static int wordpairlabelNumber;
	
	public static void init_feats(int word1preffixNumber, int word2preffixNumber 
																, int word3preffixNumber, int word4preffixNumber
																, int word1suffixNumber, int word2suffixNumber
																, int word3suffixNumber, int word4suffixNumber) {
		a = new double[m][m];
		b = new double[m][wordlabelNumber];
		suff4 = new double[m][word4suffixNumber];
		suff3 = new double[m][word3suffixNumber];
		suff2 = new double[m][word2suffixNumber];
		suff1 = new double[m][word1suffixNumber];
		pref4 = new double[m][word4preffixNumber];
		pref3 = new double[m][word3preffixNumber];
		pref2 = new double[m][word2preffixNumber];
		pref1 = new double[m][word1preffixNumber];
		p = new double[m][wordlabelNumber];
		q = new double[m][wordlabelNumber];
		p2 = new double[m][wordlabelNumber];
		q2 = new double[m][wordlabelNumber];
		pp2 = new double[m][wordpairlabelNumber];
		qq2 = new double[m][wordpairlabelNumber];
		bp = new double[m][wordpairlabelNumber];
		bq = new double[m][wordpairlabelNumber];
		hyp = new double[m];
		upr = new double[m];
		dig = new double[m];
		
		best_avg_a = new double[m][m];
		best_avg_b = new double[m][wordlabelNumber];
		best_avg_suff4 = new double[m][word4suffixNumber];
		best_avg_suff3 = new double[m][word3suffixNumber];
		best_avg_suff2 = new double[m][word2suffixNumber];
		best_avg_suff1 = new double[m][word1suffixNumber];
		best_avg_pref4 = new double[m][word4preffixNumber];
		best_avg_pref3 = new double[m][word3preffixNumber];
		best_avg_pref2 = new double[m][word2preffixNumber];
		best_avg_pref1 = new double[m][word1preffixNumber];
		best_avg_p = new double[m][wordlabelNumber];
		best_avg_q = new double[m][wordlabelNumber];
		best_avg_p2 = new double[m][wordlabelNumber];
		best_avg_q2 = new double[m][wordlabelNumber];
		best_avg_bp = new double[m][wordpairlabelNumber];
		best_avg_bq = new double[m][wordpairlabelNumber];
		best_avg_pp2 = new double[m][wordpairlabelNumber];
		best_avg_qq2 = new double[m][wordpairlabelNumber];
		best_avg_hyp = new double[m];
		best_avg_upr = new double[m];
		best_avg_dig = new double[m];

		sum_a = new double[m][m];
		sum_b = new double[m][wordlabelNumber];
		sum_suff4 = new double[m][word4suffixNumber];
		sum_suff3 = new double[m][word3suffixNumber];
		sum_suff2 = new double[m][word2suffixNumber];
		sum_suff1 = new double[m][word1suffixNumber];
		sum_pref4 = new double[m][word4preffixNumber];
		sum_pref3 = new double[m][word3preffixNumber];
		sum_pref2 = new double[m][word2preffixNumber];
		sum_pref1 = new double[m][word1preffixNumber];
		sum_p = new double[m][wordlabelNumber];
		sum_q = new double[m][wordlabelNumber];
		sum_p2 = new double[m][wordlabelNumber];
		sum_q2 = new double[m][wordlabelNumber];
		sum_bp = new double[m][wordpairlabelNumber];
		sum_bq = new double[m][wordpairlabelNumber];
		sum_pp2 = new double[m][wordpairlabelNumber];
		sum_qq2 = new double[m][wordpairlabelNumber];
		sum_hyp = new double[m];
		sum_upr = new double[m];
		sum_dig = new double[m];
		
		changes_a = new double[m][m];
		changes_b = new double[m][wordlabelNumber];
		changes_suff4 = new double[m][word4suffixNumber];
		changes_suff3 = new double[m][word3suffixNumber];
		changes_suff2 = new double[m][word2suffixNumber];
		changes_suff1 = new double[m][word1suffixNumber];
		changes_pref4 = new double[m][word4preffixNumber];
		changes_pref3 = new double[m][word3preffixNumber];
		changes_pref2 = new double[m][word2preffixNumber];
		changes_pref1 = new double[m][word1preffixNumber];
		changes_p = new double[m][wordlabelNumber];
		changes_q = new double[m][wordlabelNumber];
		changes_p2 = new double[m][wordlabelNumber];
		changes_q2 = new double[m][wordlabelNumber];
		changes_bp = new double[m][wordpairlabelNumber];
		changes_bq = new double[m][wordpairlabelNumber];
		changes_pp2 = new double[m][wordpairlabelNumber];
		changes_qq2 = new double[m][wordpairlabelNumber];
		changes_hyp = new double[m];
		changes_upr = new double[m];
		changes_dig = new double[m];
	}
	
	public static void use_averaged_feats() {
		a = best_avg_a;
		b = best_avg_b;
		suff4 = best_avg_suff4;
		suff3 = best_avg_suff3;
		suff2 = best_avg_suff2;
		suff1 = best_avg_suff1;
		pref4 = best_avg_pref4;
		pref3 = best_avg_pref3;
		pref2 = best_avg_pref2;
		pref1 = best_avg_pref1;
		p = best_avg_p;
		q = best_avg_q;
		p2 = best_avg_p2;
		q2 = best_avg_q2;
		pp2 = best_avg_pp2;
		qq2 = best_avg_qq2;
		bp = best_avg_bp;
		bq = best_avg_bq;
		hyp = best_avg_hyp;
		upr = best_avg_upr;
		dig = best_avg_dig;
	}
	
	
	public static void reward_penalize(int dir,int count,int n, int truth_wtlength, String w_i,int w_iminus1,String w_iminus1_en,int w_iplus1,String w_iplus1_en,int w_iminus2,String w_iminus2_en,
										int w_iplus2,String w_iplus2_en,int t_i,int t_iminus1,int wi_4suff,int wi_3suff,
										int wi_2suff,int wi_1suff,int wi_4pref,int wi_3pref,int wi_2pref,int wi_1pref)
	{
		//dir=1 reward,  dir=-1 penalize
		a[t_iminus1][t_i] += dir;
		
		b[t_i][word.get(w_i)] += dir;
		
		if (rare_words.contains(w_i)) { // ortho features are only applied to rare words
			if (w_i.length() >= 4) {
				if (suf_flag){
					if (wi_4suff != -1)
						suff4[t_i][wi_4suff] += dir;
					if (wi_3suff != -1)
						suff3[t_i][wi_3suff] += dir;
					if (wi_2suff != -1)
						suff2[t_i][wi_2suff] += dir;
					if (wi_1suff != -1)
						suff1[t_i][wi_1suff] += dir;
				}
				if (pref_flag){
					if (wi_4pref != -1)
						pref4[t_i][wi_4pref] += dir;
					if (wi_3pref != -1)
						pref3[t_i][wi_3pref] += dir;
					if (wi_2pref != -1)
						pref2[t_i][wi_2pref] += dir;
					if (wi_1pref != -1)
						pref1[t_i][wi_1pref] += dir;
				}
			} else if (w_i.length() >= 3) {
				if (suf_flag){
					if (wi_3suff != -1)
						suff3[t_i][wi_3suff] += dir;
					if (wi_2suff != -1)
						suff2[t_i][wi_2suff] += dir;
					if (wi_1suff != -1)
						suff1[t_i][wi_1suff] += dir;
				}
				if (pref_flag){
					if (wi_3pref != -1)
						pref3[t_i][wi_3pref] += dir;
					if (wi_2pref != -1)
						pref2[t_i][wi_2pref] += dir;
					if (wi_1pref != -1)
						pref1[t_i][wi_1pref] += dir;
				}
			} else if (w_i.length() >= 2) {
				if (suf_flag){
					if (wi_2suff != -1)
						suff2[t_i][wi_2suff] += dir;
					if (wi_1suff != -1)
						suff1[t_i][wi_1suff] += dir;
				}
				if (pref_flag){
					if (wi_2pref != -1)
						pref2[t_i][wi_2pref] += dir;
					if (wi_1pref != -1)
						pref1[t_i][wi_1pref] += dir;
				}
			} else { // (w_i.length() >= 1) 
				if (suf_flag){
					if (wi_1suff != -1)
						suff1[t_i][wi_1suff] += dir;
				}
				if (pref_flag){
					if (wi_1pref != -1)
						pref1[t_i][wi_1pref] += dir;
				}
			}
			
			if (dig_flag) {
				if (w_i.matches(".*\\d+.*")) {
					dig[t_i] += dir;
				}
				if (w_i.matches(".*-+.*")) {
					hyp[t_i] += dir;
				}
				if (w_i.matches(".*[A-Z]+.*")) {
					upr[t_i] += dir;
				}
			}
		}
		
		if (prev_flag) {
			p[t_i][w_iminus1] += dir;
			if (w_iminus2 != -1)
				p2[t_i][w_iminus2] += dir;
			bp[t_i][wordpair.get(w_iminus1_en+" "+w_i)] += dir;
		}
		if (next_flag) {
			q[t_i][w_iplus1] += dir;
			if (w_iplus2 != -1)
				q2[t_i][w_iplus2] += dir;
			bq[t_i][wordpair.get(w_i+" "+w_iplus1_en)] += dir;
		}
		if (bprevnext_flag) {
			if (w_iminus2 != -1) {
				pp2[t_i][wordpair.get(w_iminus2_en+" "+w_iminus1_en)] += dir;
			}
			if (w_iplus2 != -1) {
				qq2[t_i][wordpair.get(w_iplus1_en+" "+w_iplus2_en)] += dir;
			}
		}

	}
	
	public static void average_feats(int t_iminus1truth, int t_itruth, int t_iminus1guess
		, int t_iguess, String w_i, int w_iminus1,String w_iminus1_en, int w_iminus2,String w_iminus2_en, int w_iplus1,String w_iplus1_en, int w_iplus2,String w_iplus2_en, int wi_1suff, int wi_2suff, int wi_3suff
		, int wi_4suff, int wi_1pref, int wi_2pref, int wi_3pref, int wi_4pref
		,int count){
		
		average_a(t_iminus1truth,t_itruth,count);
		average_a(t_iminus1guess,t_iguess,count);
		
		average_b(t_itruth,word.get(w_i),count);
		average_b(t_iguess,word.get(w_i),count);
		
		if (rare_words.contains(w_i)){
			if (w_i.length() >= 4) {
				if (suf_flag){
					if (wi_4suff != -1) {
						average_4suffix(t_itruth,wi_4suff,count);
						average_4suffix(t_iguess,wi_4suff,count);
					}
					if (wi_3suff != -1) {
						average_3suffix(t_itruth,wi_3suff,count);
						average_3suffix(t_iguess,wi_3suff,count);
					}
					if (wi_2suff != -1) {
						average_2suffix(t_itruth,wi_2suff,count);
						average_2suffix(t_iguess,wi_2suff,count);
					}
					if (wi_1suff != -1) {
						average_1suffix(t_itruth,wi_1suff,count);
						average_1suffix(t_iguess,wi_1suff,count);
					}
				}
				if (pref_flag){
					if (wi_4pref != -1) {
						average_4prefix(t_itruth,wi_4pref,count);
						average_4prefix(t_iguess,wi_4pref,count);
					}
					if (wi_3pref != -1) {
						average_3prefix(t_itruth,wi_3pref,count);
						average_3prefix(t_iguess,wi_3pref,count);
					}
					if (wi_2pref != -1) {
						average_2prefix(t_itruth,wi_2pref,count);
						average_2prefix(t_iguess,wi_2pref,count);
					}
					if (wi_1pref != -1) {
						average_1prefix(t_itruth,wi_1pref,count);
						average_1prefix(t_iguess,wi_1pref,count);
					}
				}
			} else if (w_i.length() >= 3) {
				if (suf_flag){
					if (wi_3suff != -1) {
						average_3suffix(t_itruth,wi_3suff,count);
						average_3suffix(t_iguess,wi_3suff,count);
					}
					if (wi_2suff != -1) {
						average_2suffix(t_itruth,wi_2suff,count);
						average_2suffix(t_iguess,wi_2suff,count);
					}
					if (wi_1suff != -1) {
						average_1suffix(t_itruth,wi_1suff,count);
						average_1suffix(t_iguess,wi_1suff,count);
					}
				}
				if (pref_flag){
					if (wi_3pref != -1) {
						average_3prefix(t_itruth,wi_3pref,count);
						average_3prefix(t_iguess,wi_3pref,count);
					}
					if (wi_2pref != -1) {
						average_2prefix(t_itruth,wi_2pref,count);
						average_2prefix(t_iguess,wi_2pref,count);
					}
					if (wi_1pref != -1) {
						average_1prefix(t_itruth,wi_1pref,count);
						average_1prefix(t_iguess,wi_1pref,count);
					}
				}
			} else if (w_i.length() >= 2) {
				if (suf_flag){
					if (wi_2suff != -1) {
						average_2suffix(t_itruth,wi_2suff,count);
						average_2suffix(t_iguess,wi_2suff,count);
					}
					if (wi_1suff != -1) {
						average_1suffix(t_itruth,wi_1suff,count);
						average_1suffix(t_iguess,wi_1suff,count);
					}
				}
				if (pref_flag){
					if (wi_2pref != -1) {
						average_2prefix(t_itruth,wi_2pref,count);
						average_2prefix(t_iguess,wi_2pref,count);
					}
					if (wi_1pref != -1) {
						average_1prefix(t_itruth,wi_1pref,count);
						average_1prefix(t_iguess,wi_1pref,count);
					}
				}
			} else { // (w_i.length() >= 1) 
				if (suf_flag){
					if (wi_1suff != -1) {
						average_1suffix(t_itruth,wi_1suff,count);
						average_1suffix(t_iguess,wi_1suff,count);
					}
				}
				if (pref_flag){
					if (wi_1pref != -1) {
						average_1prefix(t_itruth,wi_1pref,count);
						average_1prefix(t_iguess,wi_1pref,count);
					}
				}
			}
			if (dig_flag){
				average_dig(t_itruth,count);
				average_dig(t_iguess,count);
				
				average_hyp(t_itruth,count);
				average_hyp(t_iguess,count);
				
				average_upr(t_itruth,count);
				average_upr(t_iguess,count);
			}
		}
		if (prev_flag){
			//if (n>1) {
			average_p(t_itruth,w_iminus1,count);
			average_p(t_iguess,w_iminus1,count);
			
			average_bp(t_itruth,wordpair.get(w_iminus1_en+" "+w_i),count);
			average_bp(t_iguess,wordpair.get(w_iminus1_en+" "+w_i),count);
			
			if (w_iminus2 != -1) {
				average_p2(t_itruth,w_iminus2,count);
				average_p2(t_iguess,w_iminus2,count);
			}
			
		}
		if (next_flag){
			average_q(t_itruth,w_iplus1,count);
			average_q(t_iguess,w_iplus1,count);
			
			average_bq(t_itruth,wordpair.get(w_i+" "+w_iplus1_en),count);
			average_bq(t_iguess,wordpair.get(w_i+" "+w_iplus1_en),count);
			
			if (w_iplus2 != -1) {
				average_q2(t_itruth,w_iplus2,count);
				average_q2(t_iguess,w_iplus2,count);
			}
			
		}
		if (bprevnext_flag) {
			if (w_iminus2 != -1) {
			average_pp2(t_itruth,wordpair.get(w_iminus2_en+" "+w_iminus1_en),count);
			average_pp2(t_iguess,wordpair.get(w_iminus2_en+" "+w_iminus1_en),count);
			}
			if (w_iplus2 != -1) {
			average_qq2(t_itruth,wordpair.get(w_iplus1_en+" "+w_iplus2_en),count);
			average_qq2(t_iguess,wordpair.get(w_iplus1_en+" "+w_iplus2_en),count);
			}
		}
		
	}
	
	public static void final_average(int word1preffixNumber, int word2preffixNumber, int word3preffixNumber
																	 , int word4preffixNumber, int word1suffixNumber, int word2suffixNumber, int word3suffixNumber
																	 , int word4suffixNumber, int count) {
		for (int ii=0; ii<m; ii++) {
			for (int j=0; j<m; j++)
				average_a(j,ii,count);
			for (int k=0; k<wordlabelNumber; k++)
				average_b(ii,k,count);
			if (suf_flag){
				for (int l=0; l<word4suffixNumber; l++)
					average_4suffix(ii,l,count);
				for (int l=0; l<word3suffixNumber; l++)
					average_3suffix(ii,l,count);
				for (int l=0; l<word2suffixNumber; l++)
					average_2suffix(ii,l,count);
				for (int l=0; l<word1suffixNumber; l++)
					average_1suffix(ii,l,count);
			}
			if (pref_flag){
				for (int l=0; l<word4preffixNumber; l++)
					average_4prefix(ii,l,count);
				for (int l=0; l<word3preffixNumber; l++)
					average_3prefix(ii,l,count);
				for (int l=0; l<word2preffixNumber; l++)
					average_2prefix(ii,l,count);
				for (int l=0; l<word1preffixNumber; l++)
					average_1prefix(ii,l,count);
			}
			if (prev_flag) {
				for (int k=0; k<wordlabelNumber; k++)	{
					average_p(ii,k,count);
					average_p2(ii,k,count);
				}
				for (int k=0; k<wordpairlabelNumber; k++)	{
					average_bp(ii,k,count);
				}

			}
			if (next_flag) {
				for (int k=0; k<wordlabelNumber; k++)	 {
					average_q(ii,k,count);
					average_q2(ii,k,count);
				}
				for (int k=0; k<wordpairlabelNumber; k++)	{
					average_bq(ii,k,count);
				}
			}
			
			if (bprevnext_flag) {
				for (int k=0; k<wordpairlabelNumber; k++)	{
					average_pp2(ii,k,count);
					average_qq2(ii,k,count);
				}
			}
			if (dig_flag) {
				average_dig(ii,count);
				average_hyp(ii,count);
				average_upr(ii,count);
			}
			
		}
	}
	
	public static String forward_backward(String sentence, boolean test, String output_file){
		
		String[] sentence_word = sentence.split(" ");
		int n = sentence_word.length-2;
		double[][] alpha = new double[m][n+2];
		double[][] beta = new double[m][n+2];
		double[][] gamma = new double[m][n+2];
		double[][][] xi = new double[m][m][n+2];
		
		//alpha[0][0] = 1;
		alpha[0][0] = 0;
		
		for (int idx1=0; idx1<m; idx1++) {
			for (int idx2=0; idx2<n+2; idx2++) {
				gamma[idx1][idx2] = Double.NEGATIVE_INFINITY;
				//alpha[idx1][idx2] = Double.NEGATIVE_INFINITY;
				//beta[idx1][idx2] = Double.NEGATIVE_INFINITY;
			}
		}
		
		// alpha_j(t) = (Sigma_i=0-m(alpha_i(t-1)*a_ij))*b_j(w_t)
		for (int t=1; t<=n ; t++) {
			for (int j=1; j<m; j++) {
				
				double times_all = times_all_feats(j, t, n, sentence_word);
							
				//if (bw != Double.NEGATIVE_INFINITY){
					double sum = Double.NEGATIVE_INFINITY;
					for (int i=0; i<m; i++) {
						//sum += alpha[i][t-1]*a[i][j];
						sum = logAdd(sum, alpha[i][t-1]+a[i][j]);
					}
					//alpha[j][t] = sum * bw;
					alpha[j][t] = sum + times_all;
				//}
			}
		}
		
		// beta_i(n) = a_i0
		for (int i=1; i<m ; i++) {
			beta[i][n] = a[i][0];
		}
		
		// gamma_i(n) = alpha_i(n)*beta_i(n) / Sigma_j=1-m(alpha_j(n)*beta_j(n))
		for (int i=1; i<m ; i++) {
			double s = Double.NEGATIVE_INFINITY;
			for (int j=1; j<m; j++) {
				//s += alpha[j][n]*beta[j][n];
				s = logAdd(s, alpha[j][n]+beta[j][n]);
			}
			//if (s != Double.NEGATIVE_INFINITY)
				//gamma[i][n] = (alpha[i][n]*beta[i][n])/s;
				gamma[i][n] = (alpha[i][n]+beta[i][n])-s;
		}
		
		
		for (int t=n-1; t>=1; t--) {
			
			// beta_i(t) = Sigma_j=i-m(beta_j(t+1)*a_ij*b_j(w_t+1))
			for (int i=1; i<m; i++) {
				double s = Double.NEGATIVE_INFINITY;
				for (int j=1; j<m; j++) {
					double times_all = times_all_feats(j, t+1, n, sentence_word);
					//s += beta[j][t+1]*a[i][j]*bw;
					s = logAdd(s, beta[j][t+1]+a[i][j]+times_all);
				}
				beta[i][t] = s;
			}
			
			// gamma_i(t) = alpha_i(t)*beta_i(t) / Sigma_j=1-m(alpha_j(t)*beta_j(t))
			for (int i=1; i<m; i++) {
				double s = Double.NEGATIVE_INFINITY;
				for (int j=1; j<m; j++) {
					//s += alpha[j][t]*beta[j][t];
					s = logAdd(s, alpha[j][t]+beta[j][t]);
				}
				//if (s != Double.NEGATIVE_INFINITY){
					//gamma[i][t] = (alpha[i][t]*beta[i][t])/s;
					gamma[i][t] = (alpha[i][t]+beta[i][t])-s;
				//}
				
			}
		}
		
		String tagged_sentence = "";
		try {
			if (test) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(output_file, true));
				for (int i=1; i<=n; i++) {
					int max_gamma_index = findMaxGamma(gamma, i);
					String assigned_tag = tau.get(max_gamma_index);

					writer.write(sentence_word[i] +"\t"+ assigned_tag + "\n");
					if (i==n)
						writer.write("\n");
				}
				writer.close();
			} else {
				for (int i=1; i<=n; i++) {
					int max_gamma_index = findMaxGamma(gamma, i);
					String assigned_tag = tau.get(max_gamma_index);
					tagged_sentence += sentence_word[i] +"\t"+ assigned_tag +" ";
				}
			}
		}
		catch (Exception ex) {
			
		}
		
		return "<s>\tSS " + tagged_sentence + "</s>\tSE";
	}
	
	private static int findMaxGamma(double[][] gamma, int t){
		double max_gamma = gamma[0][0];
		int max_gamma_index = 0;
		for (int j=1; j<m; j++) {
			if (gamma[j][t] > max_gamma) {
				max_gamma = gamma[j][t];
				max_gamma_index = j;
			}
		}
		return max_gamma_index;
	}
	
	public static String viterbi(String sentence, boolean test, String output_file){

		String[] sentence_word = sentence.split(" "); //split sentence into its words
		int n = sentence_word.length-2;
		double[][] alpha = new double[m][n+2];
		int[][] zeta = new int[m][n+2];
		int[] rho = new int[n+2];
				
		alpha[0][0] = 0;
		
		// building zeta and alpha matrices
		for (int t=1; t<=n ; t++) {
			for (int j=1; j<m; j++) {
				
				
				double times_all = times_all_feats(j, t, n, sentence_word);
				
				Object[] alpha_zeta = new Object[2];
				alpha_zeta = findMax(alpha, t, a, j, times_all);
				zeta[j][t] = ((Integer)alpha_zeta[0]).intValue();
				alpha[j][t] = ((Double)alpha_zeta[1]).doubleValue();
			}
		}
		
		zeta[0][n+1] = findZeta0nplus1(alpha, a, n);
		
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
	
	
	private static Object[] findMax(double[][] alpha, int t, double a[][], int j, double times_all){
		Object[] return_values = new Object[2];
		int max_index = 0;
		double max= (alpha[0][t-1])+(a[0][j])+times_all;
		
		for (int i=1; i<m; i++) {
			double potential_max = max;
			potential_max = (alpha[i][t-1])+(a[i][j])+times_all;
			if (potential_max > max) {
				max_index = i;
				max = potential_max;
			}
		}
		return_values[0] = max_index; //zeta
		return_values[1] = max; //alpha
		
		return return_values;
	}
	
	
	private static int findZeta0nplus1(double[][] alpha, double a[][], int n){
		int max_index = 0;
		double max = (alpha[0][n])+(a[0][0]);
		for (int i=1; i<m; i++) {
			double potential_max = max;
			potential_max = (alpha[i][n])+(a[i][0]);
			if (potential_max > max) {
				max_index = i;
				max = potential_max;
			}
		}
		return max_index;
	}
	
	private static double times_all_feats(int j, int t, int n, String[] sentence_word) {
		double bw = 0;
		double pw = 0;
		double qw = 0;
		double p2w = 0;
		double q2w = 0;
		double pp2w = 0;
		double qq2w = 0;
		double bpw = 0;
		double bqw = 0;
		double times_ortho = 0;

		if (word.get(sentence_word[t]) != null){
			bw = b[j][word.get(sentence_word[t])];
			
			if (rare_words.contains(sentence_word[t])) 
				times_ortho = times_ortho_feats(j, t, n, sentence_word);
		}else { // OOV (unknown) word
			times_ortho = times_ortho_feats(j, t, n, sentence_word);
			
		}
		
		if (prev_flag && word.get(sentence_word[t-1]) != null){
			pw = (p[j][word.get(sentence_word[t-1])]);
		}
		if (prev_flag && wordpair.get(sentence_word[t-1]+" "+sentence_word[t]) != null){
			bpw = bp[j][wordpair.get(sentence_word[t-1]+" "+sentence_word[t])];
		}
		if (prev_flag && t>=2 && word.get(sentence_word[t-2]) != null){
			p2w = (p2[j][word.get(sentence_word[t-2])]);
		}
		
		if (next_flag && word.get(sentence_word[t+1]) != null){
			qw = (q[j][word.get(sentence_word[t+1])]);
		}
		if (next_flag && wordpair.get(sentence_word[t]+" "+sentence_word[t+1]) != null){
			bqw = bq[j][wordpair.get(sentence_word[t]+" "+sentence_word[t+1])];
		}
		if (next_flag && t<=n-2 && word.get(sentence_word[t+2]) != null){
			q2w = (q2[j][word.get(sentence_word[t+2])]);
		}
		if (bprevnext_flag && t>=2 && wordpair.get(sentence_word[t-2]+" "+sentence_word[t-1]) != null){
			pp2w = pp2[j][wordpair.get(sentence_word[t-2]+" "+sentence_word[t-1])];
		}
		if (bprevnext_flag && t<=n-2 && wordpair.get(sentence_word[t+1]+" "+sentence_word[t+2]) != null){
			qq2w = qq2[j][wordpair.get(sentence_word[t+1]+" "+sentence_word[t+2])];
		}	
		return (bw)
		+(times_ortho)+(pw)+(qw)+(p2w)+(q2w)
		+	bpw+bqw+pp2w+qq2w;
	}
	
	private static double times_ortho_feats(int j, int t, int n, String[] sentence_word) { // OOV (unknown) word
		double bw4suff = 0;
		double bw3suff = 0;
		double bw2suff = 0;
		double bw1suff = 0;
		double bw4preff = 0;
		double bw3preff = 0;
		double bw2pref = 0;
		double bw1pref = 0;
		double digw = 0;
		double hypw = 0;
		double uprw = 0;
		
		if (sentence_word[t].length() >= 4) {
			String suf4 = sentence_word[t].substring(sentence_word[t].length()-4, sentence_word[t].length());
			String suf3 = sentence_word[t].substring(sentence_word[t].length()-3, sentence_word[t].length());
			String suf2 = sentence_word[t].substring(sentence_word[t].length()-2, sentence_word[t].length());
			String suf1 = sentence_word[t].substring(sentence_word[t].length()-1, sentence_word[t].length());
			if (suf_flag){
				if (word_4suffix.get(suf4) != null)
					bw4suff = (suff4[j][word_4suffix.get(suf4)]);
				if (word_3suffix.get(suf3) != null)
					bw3suff = (suff3[j][word_3suffix.get(suf3)]);
				if (word_2suffix.get(suf2) != null)
					bw2suff = (suff2[j][word_2suffix.get(suf2)]);
				if (word_1suffix.get(suf1) != null)
					bw1suff = (suff1[j][word_1suffix.get(suf1)]);
			}
			String prf4 = sentence_word[t].substring(0, 4);
			String prf3 = sentence_word[t].substring(0, 3);
			String prf2 = sentence_word[t].substring(0, 2);
			String prf1 = sentence_word[t].substring(0, 1);
			if (pref_flag){
				if (word_4preffix.get(prf4) != null)
					bw4preff = (pref4[j][word_4preffix.get(sentence_word[t].substring(0, 4))]);
				if (word_3preffix.get(prf3) != null)
					bw3preff = (pref3[j][word_3preffix.get(sentence_word[t].substring(0, 3))]);
				if (word_2preffix.get(prf2) != null)
					bw2pref = (pref2[j][word_2preffix.get(sentence_word[t].substring(0, 2))]);
				if (word_1preffix.get(prf1) != null)
					bw1pref = (pref1[j][word_1preffix.get(sentence_word[t].substring(0, 1))]);
			}
		} else if (sentence_word[t].length() >= 3) {
			String suf3 = sentence_word[t].substring(sentence_word[t].length()-3, sentence_word[t].length());
			String suf2 = sentence_word[t].substring(sentence_word[t].length()-2, sentence_word[t].length());
			String suf1 = sentence_word[t].substring(sentence_word[t].length()-1, sentence_word[t].length());
			if (suf_flag){
				if (word_3suffix.get(suf3) != null)
					bw3suff = (suff3[j][word_3suffix.get(suf3)]);
				if (word_2suffix.get(suf2) != null)
					bw2suff = (suff2[j][word_2suffix.get(suf2)]);
				if (word_1suffix.get(suf1) != null)
					bw1suff = (suff1[j][word_1suffix.get(suf1)]);
			}
			String prf3 = sentence_word[t].substring(0, 3);
			String prf2 = sentence_word[t].substring(0, 2);
			String prf1 = sentence_word[t].substring(0, 1);
			if (pref_flag){
				if (word_3preffix.get(prf3) != null)
					bw3preff = (pref3[j][word_3preffix.get(sentence_word[t].substring(0, 3))]);
				if (word_2preffix.get(prf2) != null)
					bw2pref = (pref2[j][word_2preffix.get(sentence_word[t].substring(0, 2))]);
				if (word_1preffix.get(prf1) != null)
					bw1pref = (pref1[j][word_1preffix.get(sentence_word[t].substring(0, 1))]);
			}
		} else if (sentence_word[t].length() >= 2) {
			String suf2 = sentence_word[t].substring(sentence_word[t].length()-2, sentence_word[t].length());
			String suf1 = sentence_word[t].substring(sentence_word[t].length()-1, sentence_word[t].length());
			if (suf_flag){
				if (word_2suffix.get(suf2) != null)
					bw2suff = (suff2[j][word_2suffix.get(suf2)]);
				if (word_1suffix.get(suf1) != null)
					bw1suff = (suff1[j][word_1suffix.get(suf1)]);
			}
			String prf2 = sentence_word[t].substring(0, 2);
			String prf1 = sentence_word[t].substring(0, 1);
			if (pref_flag){
				if (word_2preffix.get(prf2) != null)
					bw2pref = (pref2[j][word_2preffix.get(sentence_word[t].substring(0, 2))]);
				if (word_1preffix.get(prf1) != null)
					bw1pref = (pref1[j][word_1preffix.get(sentence_word[t].substring(0, 1))]);
			}
		} else { // (sentence_word[t].length() >= 1) 
			String suf1 = sentence_word[t].substring(sentence_word[t].length()-1, sentence_word[t].length());
			if (suf_flag){
				if (word_1suffix.get(suf1) != null)
					bw1suff = (suff1[j][word_1suffix.get(suf1)]);
			}
			String prf1 = sentence_word[t].substring(0, 1);
			if (pref_flag){
				if (word_1preffix.get(prf1) != null)
					bw1pref = (pref1[j][word_1preffix.get(sentence_word[t].substring(0, 1))]);
			}
		}
		
		if (dig_flag){
			if (sentence_word[t].matches(".*\\d+.*")) {
				digw = dig[j];
			}
			if (sentence_word[t].matches(".*-+.*")) {
				hypw = hyp[j];
			}
			if (sentence_word[t].matches(".*[A-Z]+.*")) {
				uprw = upr[j];
			}
		}
		
		return (bw4suff)+(bw4preff)+(bw3suff)+(bw3preff)+(bw2suff)+(bw2pref)
		+(bw1suff)+(bw1pref)+(digw)+hypw+uprw;
	}
	
	public static void average_a(int t_iminus1,int t_i,int count){
		sum_a[t_iminus1][t_i] += (count-changes_a[t_iminus1][t_i])*a[t_iminus1][t_i];
		best_avg_a[t_iminus1][t_i] = sum_a[t_iminus1][t_i] / (count);
		changes_a[t_iminus1][t_i] = count;
	}
	
	public static void average_b(int t_i,int w_i,int count){
		sum_b[t_i][w_i] += (count-changes_b[t_i][w_i])*b[t_i][w_i];
		best_avg_b[t_i][w_i] = sum_b[t_i][w_i] / (count);
		changes_b[t_i][w_i] = count;
	}
	
	public static void average_4suffix(int t_i,int wi_4suff,int count){
		sum_suff4[t_i][wi_4suff] += (count-changes_suff4[t_i][wi_4suff])*suff4[t_i][wi_4suff];
		best_avg_suff4[t_i][wi_4suff] = sum_suff4[t_i][wi_4suff] / (count);
		changes_suff4[t_i][wi_4suff] = count;
	}
	
	public static void average_3suffix(int t_i,int wi_3suff,int count){
		sum_suff3[t_i][wi_3suff] += (count-changes_suff3[t_i][wi_3suff])*suff3[t_i][wi_3suff];
		best_avg_suff3[t_i][wi_3suff] = sum_suff3[t_i][wi_3suff] / (count);
		changes_suff3[t_i][wi_3suff] = count;
	}
	
	public static void average_2suffix(int t_i,int wi_2suff,int count){
		sum_suff2[t_i][wi_2suff] += (count-changes_suff2[t_i][wi_2suff])*suff2[t_i][wi_2suff];	
		best_avg_suff2[t_i][wi_2suff] = sum_suff2[t_i][wi_2suff] / (count);
		changes_suff2[t_i][wi_2suff] = count;
	}
	
	public static void average_1suffix(int t_i,int wi_1suff,int count){
		sum_suff1[t_i][wi_1suff] += (count-changes_suff1[t_i][wi_1suff])*suff1[t_i][wi_1suff];
		best_avg_suff1[t_i][wi_1suff] = sum_suff1[t_i][wi_1suff] / (count);
		changes_suff1[t_i][wi_1suff] = count;
	}
	
	public static void average_4prefix(int t_i,int wi_4pref,int count){
		sum_pref4[t_i][wi_4pref] += (count-changes_pref4[t_i][wi_4pref])*pref4[t_i][wi_4pref];
		best_avg_pref4[t_i][wi_4pref] = sum_pref4[t_i][wi_4pref] / (count);
		changes_pref4[t_i][wi_4pref] = count;
	}
	
	public static void average_3prefix(int t_i,int wi_3pref,int count){
		sum_pref3[t_i][wi_3pref] += (count-changes_pref3[t_i][wi_3pref])*pref3[t_i][wi_3pref];
		best_avg_pref3[t_i][wi_3pref] = sum_pref3[t_i][wi_3pref] / (count);
		changes_pref3[t_i][wi_3pref] = count;
	}
	
	public static void average_2prefix(int t_i,int wi_2pref,int count){
		sum_pref2[t_i][wi_2pref] += (count-changes_pref2[t_i][wi_2pref])*pref2[t_i][wi_2pref];	
		best_avg_pref2[t_i][wi_2pref] = sum_pref2[t_i][wi_2pref] / (count);
		changes_pref2[t_i][wi_2pref] = count;
	}
	
	public static void average_1prefix(int t_i,int wi_1pref,int count){
		sum_pref1[t_i][wi_1pref] += (count-changes_pref1[t_i][wi_1pref])*pref1[t_i][wi_1pref];
		best_avg_pref1[t_i][wi_1pref] = sum_pref1[t_i][wi_1pref] / (count);
		changes_pref1[t_i][wi_1pref] = count;
	}
	
	public static void average_p(int t_i,int w_iminus1,int count){
		sum_p[t_i][w_iminus1] += (count - changes_p[t_i][w_iminus1])*p[t_i][w_iminus1];
		best_avg_p[t_i][w_iminus1] = sum_p[t_i][w_iminus1] / (count);
		changes_p[t_i][w_iminus1] = count;
	}
	
	public static void average_p2(int t_i,int w_iminus2,int count){
		sum_p2[t_i][w_iminus2] += (count - changes_p2[t_i][w_iminus2])*p2[t_i][w_iminus2];
		best_avg_p2[t_i][w_iminus2] = sum_p2[t_i][w_iminus2] / (count);
		changes_p2[t_i][w_iminus2] = count;
	}
	
	public static void average_q(int t_i,int w_iplus1,int count){
		sum_q[t_i][w_iplus1] += (count - changes_q[t_i][w_iplus1])*q[t_i][w_iplus1];
		best_avg_q[t_i][w_iplus1] = sum_q[t_i][w_iplus1] / (count);
		changes_q[t_i][w_iplus1] = count;
	}
	
	public static void average_q2(int t_i,int w_iplus2,int count){
		sum_q2[t_i][w_iplus2] += (count - changes_q2[t_i][w_iplus2])*q2[t_i][w_iplus2];
		best_avg_q2[t_i][w_iplus2] = sum_q2[t_i][w_iplus2] / (count);
		changes_q2[t_i][w_iplus2] = count;
	}
	
	public static void average_bp(int t_i,int w_iminus1w_i,int count){
		sum_bp[t_i][w_iminus1w_i] += (count - changes_bp[t_i][w_iminus1w_i])*bp[t_i][w_iminus1w_i];
		best_avg_bp[t_i][w_iminus1w_i] = sum_bp[t_i][w_iminus1w_i] / (count);
		changes_bp[t_i][w_iminus1w_i] = count;
	}
	
	public static void average_bq(int t_i,int w_iw_iplus1,int count){
		sum_bq[t_i][w_iw_iplus1] += (count - changes_bq[t_i][w_iw_iplus1])*bq[t_i][w_iw_iplus1];
		best_avg_bq[t_i][w_iw_iplus1] = sum_bq[t_i][w_iw_iplus1] / (count);
		changes_bq[t_i][w_iw_iplus1] = count;
	}
	
	public static void average_pp2(int t_i,int w_iminus2w_iminus1,int count){
		sum_pp2[t_i][w_iminus2w_iminus1] += (count - changes_pp2[t_i][w_iminus2w_iminus1])*pp2[t_i][w_iminus2w_iminus1];
		best_avg_pp2[t_i][w_iminus2w_iminus1] = sum_pp2[t_i][w_iminus2w_iminus1] / (count);
		changes_pp2[t_i][w_iminus2w_iminus1] = count;
	}
	
	public static void average_qq2(int t_i,int w_iplus1w_iplus2,int count){
		sum_qq2[t_i][w_iplus1w_iplus2] += (count - changes_qq2[t_i][w_iplus1w_iplus2])*qq2[t_i][w_iplus1w_iplus2];
		best_avg_qq2[t_i][w_iplus1w_iplus2] = sum_qq2[t_i][w_iplus1w_iplus2] / (count);
		changes_qq2[t_i][w_iplus1w_iplus2] = count;
	}
	
	public static void average_dig(int t_i,int count){
		sum_dig[t_i] += (count - changes_dig[t_i])*dig[t_i];
		best_avg_dig[t_i] = sum_dig[t_i] / (count);
		changes_dig[t_i] = count;
	}
	
	public static void average_hyp(int t_i,int count){
		sum_hyp[t_i] += (count - changes_hyp[t_i])*hyp[t_i];
		best_avg_hyp[t_i] = sum_hyp[t_i] / (count);
		changes_hyp[t_i] = count;
	}
	
	public static void average_upr(int t_i,int count){
		sum_upr[t_i] += (count - changes_upr[t_i])*upr[t_i];
		best_avg_upr[t_i] = sum_upr[t_i] / (count);
		changes_upr[t_i] = count;
	}
	
	private static double logAdd(double x, double y)
	{
    if ( x <= Double.NEGATIVE_INFINITY ) return y;
    if ( y <= Double.NEGATIVE_INFINITY ) return x;
    if ( x < y ) {
			return (y + Math.log(Math.exp(x-y) + 1));
    } else {
			return (x + Math.log(Math.exp(y-x) + 1));
    }
	}
	
}

