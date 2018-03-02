import java.util.*;
import java.io.*;

public class PhyloTree {

	
	public static ArrayList<ArrayList<String>> einLesen() throws IOException{
		ArrayList<String> outputSeq = new ArrayList<String>();
		ArrayList<String> outputHeader = new ArrayList<String>();
		ArrayList<String> input = new ArrayList<String>();
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI Projekt 3/file1.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI Projekt 3/file2.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI Projekt 3/file3.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI Projekt 3/file4.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI Projekt 3/file5.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI Projekt 3/file6.fasta");
		
		//Einlesen der Proteinsequenz-FASTA Datein und ausgabe mitsammt der Metadatein als ArrayList von ArrayLists
		for(int i = 0; i<input.size();i++){
			String file = input.get(i);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String header = "";
			String line = "";
			String seq = "";
			int k = 1;
			while((line=br.readLine())!=null){
				
				if(k==2){
					header = line;
				}
				if(line.charAt(0) == 'X'){
					break;
				}
				if(line.charAt(0) != '>'){
					seq = seq + line;
				}
				k+=1;
			}
			outputHeader.add(header);
			outputSeq.add(seq);
			fr.close();
			br.close();
		}
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		output.add(outputHeader);
		output.add(outputSeq);
		return output;
	}
	
	public static int[] matrixMin(int[][] matrix){
		int[] output = new int[2];
		int min = 99999999;
		for(int i = 0; i< matrix.length; i++){
			for(int j = 0; j<matrix[i].length;j++){
				if(matrix[i][j] < min){
					min = matrix[i][j];
					output[0] = i;
					output[1] = j;
					
				}
			}
		}
		
		
		return output;
	}
	
	public static void buildTree() throws IOException{
		ArrayList<ArrayList<String>> input = einLesen();
		String tree = "";
		
		//Erstellen eines temporären Testmatrix
		int matrixZeilen = 6;
		int matrixSpalten = 6;
		int[][] matrix = new int[matrixZeilen][matrixSpalten];
		Random random = new Random();
		int min =0;
		int max = 10;
		for(int i = 0; i<matrixZeilen; i++){
			for(int j = 0; j<matrixSpalten; j++){
				int rand = random.nextInt(max-min+1)+min;
				matrix[i][j] = rand;
			}
		}
		System.out.println("Matrixlänge " + matrix.length);
		for(int i = 0; i<6; i++){
			for(int j = 0; j<6; j++){
				System.out.println("ALTE MATRIX"+"x= "+i+"y="+j +" "+matrix[i][j]);
			}
		}

		
		int delZeile = 0;
		int delSpalte = 0;
		//Beginn der ersten Iteration von UPGMA NOCH NICHT MAL IM ANSATZ FERTIG!!!!!!
		// IDEE für mich: verschachteln via while schleife die zählvariable als termination nutzt
		int[] matMin = new int[2];
		matMin = matrixMin(matrix);
		delZeile = matMin[0]; // Zeilenindex der zu löschenden Zeile
		delSpalte = matMin[1]; // Index der zu löschenden Spalte

		int[][] newMatrix = new int[(matrixZeilen-1)][(matrixSpalten -1)];
		System.out.println("newMatrixlänge " + newMatrix.length);
		for(int i = 0; i<matrix.length;i++){
			if(i!= delZeile){
				for(int j = 0;j<matrix[i].length;j++){
					if(j!=delSpalte){
						if(i==(matrix.length-1) && (j!=(matrix[i].length-1))){
							newMatrix[i-1][j] = matrix[i][j];
						}
						if(j==(matrix[i].length-1) && (i!=(matrix.length-1)) ){
							newMatrix[i][j-1] = matrix[i][j];
						}
						if(i==(matrix[i].length-1) && j==(matrix[i].length-1)){
							newMatrix[i-1][j-1] = matrix[i][j];
						}else{
							newMatrix[i][j] =matrix[i][j]; // Hier läuft er aus dem index raus kp warum sollte eig abgefangen sein.
						}
					}
				}
			}
		}
		
		System.out.println("matmin!! "+"x= "+delZeile+"y= "+delSpalte +" " + matrix[delZeile][delSpalte]);
		
		for(int i = 0; i<5; i++){
			for(int j = 0; j<5; j++){
				System.out.println("NEUE MATRIX"+"x= "+i+"y="+j +" "+newMatrix[i][j]);
			}
		}
		
	/*	for(int i = 0; i<6; i++){
			for(int j = 0; j<6; j++){
				System.out.println("x= "+i+"y="+j +" "+matrix[i][j]);
			}
		}
		
		System.out.println("matmin!! "+"x= "+delZeile+"y= "+delSpalte +" " + matrix[delZeile][delSpalte]); */
		
		
	}
	
	public static void main(String[] args) throws IOException{
		ArrayList<ArrayList<String>> test = new ArrayList<ArrayList<String>>();

		buildTree();
		
		
	}
}
