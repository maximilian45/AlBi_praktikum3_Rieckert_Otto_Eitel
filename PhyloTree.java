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
	
	public static int[] matrixMin(ArrayList<ArrayList<Integer>> matrix){
		int[] output = new int[2];
		int min = 99999999;
		for(int i=0; i< matrix.size();i++){
			ArrayList<Integer> temp = matrix.get(i);
			for(int j= 0; j< temp.size();j++){
				if(temp.get(j)<min){
					min = temp.get(j);
					output[0] = i;
					output[1] = j;
				}
			}
		}
		
		return output;
	}
	
	public static ArrayList<ArrayList<Integer>> calcDist(ArrayList<ArrayList<Integer>> oldMatrix, ArrayList<ArrayList<Integer>> matrix, int[] matMin,ArrayList<Integer> clusterCount,ArrayList<Integer> clusterCountNew){
		ArrayList<ArrayList<Integer>> newMatrix = matrix;// new ArrayList<ArrayList<Integer>>();
		int delZeile = matMin[0]; //Zeilenindex der zu löschenden Zeile
		int delSpalte = matMin[1];// Index der zu löschenden Spalte
		for(int i = 0; i < oldMatrix.size();i++){
			if(i!=delZeile && i!=delSpalte){
			//N erster unterknoten *
		//	clusterCount[matMin[0]]* (oldMatrix.get(matMin[0]).get(i))
			}
		}
		
		return newMatrix;
	}
	
	
	public static void buildTree() throws IOException{
		ArrayList<ArrayList<String>> input = einLesen();
		ArrayList<String> header = input.get(0);
		ArrayList<Integer> clusterCount = new ArrayList<Integer>(); // Liste zum Merken wie viele Unterknoten jeder Knoten hat
		for(int i = 0; i<header.size();i++){
			clusterCount.add(1);
		}
		String tree = "";
		
		//Erstellen eines temporären Testmatrix
		int matrixZeilen = 5;
		int matrixSpalten = 6;
		ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
		Random random = new Random();
		int min =0;
		int max = 10;
		 
		for(int i = 0; i<matrixZeilen; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0; j<matrixSpalten; j++){
				int rand = random.nextInt(max-min+1)+min;
				temp.add(rand);
			}
			matrix.add(temp);
		}
		
		System.out.println(matrix.size());
		for(int i = 0;i< matrix.size();i++){
			System.out.println(matrix.get(i));
		}
		//Beginn UPGMA
		
		
		/*
		int[] matMin = new int[2];
		matMin = matrixMin(matrix);
		boolean del = false;
		int k = 0;
		int delZeile = matMin[0]; //Zeilenindex der zu löschenden Zeile
		int delSpalte = matMin[1];// Index der zu löschenden Spalte
		//Erstes Löschen einer Zeile und einer Spalte
		for(int i = 0; i <matrix.size();i++){
			if(i == delZeile){ // Entfernen der ersten zu löschenden Zeile
				matrix.remove(i);
				del = true;
			}else{
				if(i== delSpalte){ // Löschend der zweiten zuz  löschenden Zeile
				matrix.remove(i);
				del = true;
				}else{
					
			if(del == true){ // Absichern, dass temp nicht auf den Index einer gelöschten zeile zugreift (Zeile 138)
				k = i-1;
			}else{
				k = i;
			}		
			ArrayList<Integer> temp = matrix.get(k);
			temp.set(delZeile, null);
			temp.set(delSpalte, null);
			temp.removeAll(Collections.singleton(null));
			matrix.set(i, temp);
			del = false;
		}}}*/
		
		
		//Reverser Ansatz! Da das removen mir den index kaputt macht. UND ER WORKT!!!!!!
		int[] matMin = new int[2];
		matMin = matrixMin(matrix);
		int delZeile = matMin[0]; //Zeilenindex der zu löschenden Zeile
		int delSpalte = matMin[1];// Index der zu löschenden Spalte
		ArrayList<ArrayList<Integer>> oldMatrix = matrix; // Merken der unmodifizierten matrix für die Distanzberechnung
		//Erstes Löschen einer Zeile und einer Spalte
		for(int i = (matrix.size() - 1); i >=0;i--){
			if(i == delZeile){ // Entfernen der ersten zu löschenden Zeile
				matrix.remove(i);
			}else{
				if(i== delSpalte){ // Löschend der zweiten zuz  löschenden Zeile
				matrix.remove(i);
				}else{		
			ArrayList<Integer> temp = matrix.get(i);
			temp.set(delZeile, null);
			temp.set(delSpalte, null);
			temp.removeAll(Collections.singleton(null));
			matrix.set(i, temp);
		}
		}
		}
		//Modifizieren der Clustercount liste um sich zu merken dass aus zwei Knoten ein Knoten mit der Summe ihrer Unterknotenwerte sind (alte wird erstmal behalten für distanz berechnung)
		// In der letzten Stelle von clusterCountNew steht dann der neue Knotenwert, genauso wie in header an letzter stelle der header für den neuen Knoten steht.
		ArrayList<Integer> clusterCountNew = clusterCount;
		clusterCountNew.add( (clusterCount.get(delZeile) + clusterCount.get(delSpalte)));
		clusterCountNew.set(delZeile, null);
		clusterCountNew.set(delSpalte, null);
		clusterCountNew.removeAll(Collections.singleton(null));
		//clusterCount = clusterCount2;
		//Gleiche Modifikation für die header Liste
		header.add( header.get(delZeile) + "+" + header.get(delSpalte) );
		header.set(delZeile, null);
		header.set(delSpalte, null);
		header.removeAll(Collections.singleton(null));
		
	//	calcDist(oldMatrix,matrix,matMin,clusterCount,clusterCountNew);
		
		
		System.out.println("Matrixminimum");
		System.out.println("Matrixminimum Zeile 1= "+ matMin[0]);
		System.out.println("Matrixminimum Zeile 2 = "+ matMin[1]);
		System.out.println("Matrixminimum Spalte 1= "+ matMin[0]);
		System.out.println("Matrixminimum Spalte 2 = "+ matMin[1]);
		
		
		for(int i = 0;i< matrix.size();i++){
			System.out.println(matrix.get(i));
		}
		
		
		
		
		/*
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
		
		
		*/
		
		
		/*	System.out.println("matmin!! "+"x= "+delZeile+"y= "+delSpalte +" " + matrix[delZeile][delSpalte]);
		
		for(int i = 0; i<5; i++){
			for(int j = 0; j<5; j++){
				System.out.println("NEUE MATRIX"+"x= "+i+"y="+j +" "+newMatrix[i][j]);
			}
		}
		
		for(int i = 0; i<6; i++){
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
