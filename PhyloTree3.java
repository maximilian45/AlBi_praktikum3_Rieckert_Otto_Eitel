package a3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PhyloTree3 {

	
	public static ArrayList<ArrayList<String>> einLesen() throws IOException{
		ArrayList<String> outputSeq = new ArrayList<String>();
		ArrayList<String> outputHeader = new ArrayList<String>();
		String file = "C:/Users/eitel_j1/Downloads/aln-fasta1.txt";
		String line = "";
		String seq = "";
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		int k =0;


		
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		while((line=br.readLine())!=null){
			if(line.charAt(0) == '>'){
				outputHeader.add(line);
			}
		}
		line = "";
		br.close();
		FileReader fr2 = new FileReader(file);
		BufferedReader br2 = new BufferedReader(fr2);
		while((line=br2.readLine())!=null){
			if(line.charAt(0)!= '>'){
				seq+=line;
			}
			if(line.charAt(0)== '>'){
				if(k != 0){
					outputSeq.add(seq);
					seq = "";
				}
			}
			k+=1;
		}
		br2.close();
		outputSeq.add(seq);	
		output.add(outputHeader);
		output.add(outputSeq);
		return output;
	}
	
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

	public static float[][] distance2(ArrayList<String> sequences){
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
					distance[i][j] = distance[j][i] = (float)(-0.75*Math.log(1-((4/3)*(p/l))));
				}
			}
		}		
		return distance;
	}

	// returns indices i, j of minimal entry in matrix
	public static int[] matrixMin(float[][] matrix){
		int[] output = new int[2];
		float min = 9999999;
		for(int i = 0;i<matrix.length;i++){
			for(int j = i+1; j <matrix[i].length;j++){
				if(matrix[i][j] < min){
					min = matrix[i][j];
					output[0] = i;
					output[1] = j;
				}
			}
		}
		
		return output;
	}
	
	public static float[][] updateDistMatrix(float[][] distMatrix, int i, int j){
		int k = distMatrix.length;
		float[][] newMatrix = new float[k-1][k-1];
		
//		Zeilen loeschen
		int m1 = 0;
		for(int l = 0; l < k ; l++){
			if(l != i && l != j){
				int m2 = 0;
				for(int n = 0; n < k; n++){				
					if(n != i && n!= j){
						newMatrix[m1][m2] = distMatrix[l][n];
						m2++;
					}
					// letzte Zeile/Spalte anfuegen
					if(m2 == (k-2)) {
						newMatrix[m1][m2] = newMatrix[m2][m1] =
								(float)((distMatrix[l][i] + distMatrix[l][j])/2);
													// TODO korrekte Distanzen!
					}
				}
				m1++;
			}
		}
		
		
		return newMatrix;
	}
	
	public static void buildTree(int x) throws IOException{
		ArrayList<ArrayList<String>> input = einLesen();
	//	Map<String,String> knoten = new HashMap<String,String>();
		ArrayList<String> seq = input.get(1);
		ArrayList<String> header = input.get(0);
		
		for(int i = 0; i < header.size(); i++) {
			System.out.println(header.get(i));
		}
		
		// TODO array numberOfLeaves, damit wir Distanzen korrekt berechnen k�nnen
		// - hat immer gleiche laenge wie header
		
		float[][] distMatrix = distance(seq);
		
		if(x==2){
			distMatrix = distance2(seq);
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("PhyloTree.tree"));

//		Map<String,Float> kantenlngen = new HashMap<String,Float>();
//		for(int i= 0; i< seq.size();i++){
//			//knoten.put((input.get(0).get(i)), (input.get(1).get(i)));
//			kantenlngen.put((seq.get(i)),(float)0.0);
//		}
		

		
		writer.write("#NEXUS"+"\r\n");
		writer.write("\r\n");
		writer.write("BEGIN TREES;\r\n");
		writer.write("TREE TREE1 = \r\n");

		while(header.size()>=2){
			int[] matrixMinima = matrixMin(distMatrix);
			int matrixMin1 = matrixMinima[0];
			int matrixMin2 = matrixMinima[1];

			
			float kantenlaenge = distMatrix[matrixMin1][matrixMin2]*100;
			String neuerKnoten = "(" + header.get(matrixMin1) + ", "
									+ header.get(matrixMin2) + "):" + kantenlaenge ;
//			System.out.println(neuerKnoten);
//			System.out.println("knoten1: " + header.get(matrixMin1));
//			System.out.println("knoten2: " + header.get(matrixMin2));
			
			header.add(neuerKnoten);
			header.remove(matrixMin2);
			header.remove(matrixMin1);
			
			distMatrix = updateDistMatrix(distMatrix, matrixMin1, matrixMin2);
						
		}
		
        
		writer.write(header.get(0));
		writer.write("\r\n");
		writer.write(";\r\n");
		writer.write("END;\r\n");
		writer.close();
						
	}
	
	
	public static void main(String[] args) throws IOException{
		//buildTree(1);
		buildTree(2);
	}
	
}

