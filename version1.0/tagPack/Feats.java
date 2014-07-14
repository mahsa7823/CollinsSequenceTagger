package tagPack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.Integer;

public class Feats{
	public static HashMap<String, Integer> tag = new HashMap<String, Integer>();
	public static HashMap<Integer, String> tau = new HashMap<Integer, String>();
	public static HashMap<String, Integer> word = new HashMap<String, Integer>();
	
	public static double[][] a; // ti-1~ti
	public static double[][] b; // ti~wi
	public static double[][] p; // ti~wi-1
	public static double[][] q; // ti~wi+1
	
	// used in "averaging" -- begin
	public static double[][] best_avg_a;
	public static double [][] best_avg_b;
	public static double [][] best_avg_p;
	public static double [][] best_avg_q;
	
	public static double[][] sum_a;
	public static double [][] sum_b;
	public static double [][] sum_p;
	public static double [][] sum_q;
	
	public static double[][] changes_a;
	public static double [][] changes_b;
	public static double [][] changes_p;
	public static double [][] changes_q;
	// used in "averaging" -- end
	
	public static int m; // number of tags
	public static int wordlabelNumber; // number if words
	
	public static void init_feats() {
		a = new double[m][m];
		b = new double[m][wordlabelNumber];
		p = new double[m][wordlabelNumber];
		q = new double[m][wordlabelNumber];
		
		best_avg_a = new double[m][m];
		best_avg_b = new double[m][wordlabelNumber];
		best_avg_p = new double[m][wordlabelNumber];
		best_avg_q = new double[m][wordlabelNumber];
		
		sum_a = new double[m][m];
		sum_b = new double[m][wordlabelNumber];
		sum_p = new double[m][wordlabelNumber];
		sum_q = new double[m][wordlabelNumber];
		
		changes_a = new double[m][m];
		changes_b = new double[m][wordlabelNumber];
		changes_p = new double[m][wordlabelNumber];
		changes_q = new double[m][wordlabelNumber];
		
	}
	
	public static void use_averaged_feats() {
		a = best_avg_a;
		b = best_avg_b;
		
		p = best_avg_p;
		q = best_avg_q;
		
	}
	
	public static void reward_penalize(int dir,int count,int n, int truth_wtlength, String w_i,int w_iminus1,int w_iplus1,
																		 int t_i,int t_iminus1)
	{
		a[t_iminus1][t_i] += dir;
		b[t_i][word.get(w_i)] += dir;
		p[t_i][w_iminus1] += dir;
		q[t_i][w_iplus1] += dir;
	}
	
	public static void average_feats(int t_iminus1truth, int t_itruth, int t_iminus1guess
																	 , int t_iguess, String w_i, int w_iminus1, int w_iplus1,int count) {
		
		average_a(t_iminus1truth,t_itruth,count);
		average_a(t_iminus1guess,t_iguess,count);
		
		average_b(t_itruth,word.get(w_i),count);
		average_b(t_iguess,word.get(w_i),count);
		
		
		average_p(t_itruth,w_iminus1,count);
		average_p(t_iguess,w_iminus1,count);
		
		average_q(t_itruth,w_iplus1,count);
		average_q(t_iguess,w_iplus1,count);
		
	}
	
	public static void final_average(int count) {
		for (int ii=0; ii<m; ii++) {
			for (int j=0; j<m; j++)
				average_a(j,ii,count);
			for (int k=0; k<wordlabelNumber; k++)
				average_b(ii,k,count);
			for (int k=0; k<wordlabelNumber; k++)	{
				average_p(ii,k,count);
			}
			for (int k=0; k<wordlabelNumber; k++) {	
				average_q(ii,k,count);
			}
			
		}
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
				
				double add_all = add_all_feats(j, t, n, sentence_word);
				
				Object[] alpha_zeta = new Object[2];
				alpha_zeta = findMax(alpha, t, a, j, add_all);
				// zeta_j(t) = argmax_i(alpha_i(t-1)*a_ij)
				zeta[j][t] = ((Integer)alpha_zeta[0]).intValue();
				// alpha_j(t) = max_i(alpha_i(t-1)*a_ij)*b_j(w_t)
				alpha[j][t] = ((Double)alpha_zeta[1]).doubleValue();
			}
		}
		
		zeta[0][n+1] = findZeta0nplus1(alpha, a, n);
		
		rho[n+1] = 0;
		
		// building rho array
		for (int t=n; t>=1; t--) {
			rho[t] = zeta[rho[t+1]][t+1];
		}
		
		// writting words of the sentence and their corresponding tags into the output file: (tag word)
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
	
	
	private static Object[] findMax(double[][] alpha, int t, double a[][], int j, double add_all){
		Object[] return_values = new Object[2];
		int max_index = 0;
		double max= alpha[0][t-1]+a[0][j]+add_all;
		
		for (int i=1; i<m; i++) {
			double potential_max = max;
			potential_max = alpha[i][t-1]+a[i][j]+add_all;
			if (potential_max > max) {
				max_index = i;
				max = potential_max;
			}
		}
		return_values[0] = max_index; //zeta
		return_values[1] = max; //alpha
		
		return return_values;
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
				double add_all = add_all_feats(j, t, n, sentence_word);
				
				//if (bw != Double.NEGATIVE_INFINITY){
				double sum = Double.NEGATIVE_INFINITY;
				for (int i=0; i<m; i++) {
					//sum += alpha[i][t-1]*a[i][j];
					sum = logAdd(sum, alpha[i][t-1]+a[i][j]);
				}
				//alpha[j][t] = sum * bw;
				alpha[j][t] = sum + add_all;
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
					double add_all = add_all_feats(j, t+1, n, sentence_word);
					//s += beta[j][t+1]*a[i][j]*bw;
					s = logAdd(s, beta[j][t+1]+a[i][j]+add_all);
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
	
	
	private static int findZeta0nplus1(double[][] alpha, double a[][], int n){
		int max_index = 0;
		double max = alpha[0][n]+a[0][0];
		for (int i=1; i<m; i++) {
			double potential_max = max;
			potential_max = alpha[i][n]+a[i][0];
			if (potential_max > max) {
				max_index = i;
				max = potential_max;
			}
		}
		return max_index;
	}
	
	private static double add_all_feats(int j, int t, int n, String[] sentence_word) {
		double bw = 0;
		double pw = 0;
		double qw = 0;
		
		if (word.get(sentence_word[t]) != null){
			bw = b[j][word.get(sentence_word[t])];
		}
		
		if (word.get(sentence_word[t-1]) != null){
			pw = p[j][word.get(sentence_word[t-1])];
		}
		
		if (word.get(sentence_word[t+1]) != null){
			qw = q[j][word.get(sentence_word[t+1])];
		}
				
		
		return bw + pw + qw;
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
	
	public static void average_p(int t_i,int w_iminus1,int count){
		sum_p[t_i][w_iminus1] += (count - changes_p[t_i][w_iminus1])*p[t_i][w_iminus1];
		best_avg_p[t_i][w_iminus1] = sum_p[t_i][w_iminus1] / (count);
		changes_p[t_i][w_iminus1] = count;
	}
	
	public static void average_q(int t_i,int w_iplus1,int count){
		sum_q[t_i][w_iplus1] += (count - changes_q[t_i][w_iplus1])*q[t_i][w_iplus1];
		best_avg_q[t_i][w_iplus1] = sum_q[t_i][w_iplus1] / (count);
		changes_q[t_i][w_iplus1] = count;
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

