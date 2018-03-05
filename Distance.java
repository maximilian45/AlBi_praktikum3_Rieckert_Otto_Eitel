package a3;

import java.util.*;
//import java.io.*;

public class Distance {
	
	public static float[][] distance(ArrayList<String> sequences){
		int k = sequences.size();
		int l = sequences.get(0).length();
		float[][] distance = new float[k][k];
		
		for(int i = 0; i < k; i++) {
			String seq1 = sequences.get(i);
			for(int j = i+1; j < k; j++) {
				String seq2 = sequences.get(j);
				if(seq1.equals(seq2)) {
					distance[i][j] = distance[j][i] = 0;
				} else {
					float p = 0;
					for(int m = 0; m < l; m++) {
						if(seq1.charAt(m) != seq2.charAt(m)) {
							p += 1;
						}
					}
					distance[i][j] = distance[j][i] = p/l;
				}
			}
		}		
		return distance;
	}

	public static void main(String[] args) {
		/*TODO Auto-generated method stub*/
		ArrayList<String> seq = new ArrayList<String>();
		seq.add("ATTTGCGGTA");
		seq.add("ATCTGCGATA");
		seq.add("ATTGCCGTTT");
		seq.add("TTCGCTGTTT");
		float[][] dist = distance(seq);
		System.out.println(dist[0][0] + " " + dist[0][1] + " " + dist[0][2] + " " + dist[0][3]);
		System.out.println(dist[1][0] + " " + dist[1][1] + " " + dist[1][2] + " " + dist[1][3]);
		System.out.println(dist[2][0] + " " + dist[2][1] + " " + dist[2][2] + " " + dist[2][3]);
		System.out.println(dist[3][0] + " " + dist[3][1] + " " + dist[3][2] + " " + dist[3][3]);
	}

}
