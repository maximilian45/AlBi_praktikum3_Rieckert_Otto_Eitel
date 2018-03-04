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
	
	
	// OldMatrix = matrix ohne löschungen , matrix= matrix mit löschungen allerdings fehlt neue zeile und spalte für neuen knoten
	// clustercount sind die n werte der alten matrix und old header die dazugehörigen header
	// clustercountnew sind die n werte der neuen matrix und header die dazugehörigen header (neuer knoten bereits inbegriffen)
	public static ArrayList<ArrayList<Integer>> calcDist(ArrayList<ArrayList<Integer>> oldMatrix, ArrayList<ArrayList<Integer>> matrix, int[] matMin,ArrayList<Integer> clusterCount,ArrayList<Integer> clusterCountNew,ArrayList<String> oldHeader,ArrayList<String> header){
		ArrayList<ArrayList<Integer>> newMatrix = matrix;
		int delZeile = matMin[0]; //Zeilenindex der zu löschenden Zeile
		int delSpalte = matMin[1];// Index der zu löschenden Spalte
		//Aufbauen der neuen Zeile (die letzte spalte kriegt nun auch eine zeile, da der neue knoten die neue letzte spalte ohne zeile bilden wird)
		ArrayList<Integer> newZeile = new ArrayList<Integer>();
		for(int i =0; i<matrix.size(); i++){
			ArrayList<Integer> temp = matrix.get(i);
			for(int j =0; j < temp.size();j++){       // Verbesserungspotential: Muss wahrscheinlich nicht in einer schleife passieren und könnte direkt auf der matrix laufen
				if(j == (temp.size())-1){
					newZeile.add(temp.get(j));
				}
			}
		}
		newMatrix.add(newZeile); // Anfügen der neuen Zeile
		System.out.println("Matrixsize " + newMatrix.size());
		System.out.println("headersize " + header.size());
		System.out.println("clusterCountsize " + clusterCount.size());
		// Hier findet dann für die Neue Spalte die distanzberechnung statt, indem jeder zeile der entsprechende Betrag angefügt wird
		for(int i =0; i< header.size()-1; i++){ // -1 damit ich nicht den header des neuen knotens bearbeite, newMatrix ist noch NxN und die header sind n+1 lang 
			ArrayList<Integer> temp = newMatrix.get(i);
			int dist =0;
			// clustercount(matMin[0]) gibt mir den N wert des ersten gelöschten wertes ; dann hole ich mir aus der alten matrix den wert aus der ersten gelöschten zeile, an der stelle an der der wert der j-ten zeile der neuen matrix steht
			//und verrechne dies mit dem wert der zweiten gelöschten zeile aus dem zweiten wert der im neuen knoten steckt
			
			System.out.println("((clusterCount.get(matMin[0])) "+(clusterCount.get(matMin[0])));
			System.out.println("(oldMatrix.get(matMin[0]).get(oldHeader.indexOf(header.get(i)))) "+(oldMatrix.get(matMin[0]).get(oldHeader.indexOf(header.get(i)))));
			System.out.println("((clusterCount.get(matMin[1])) "+(clusterCount.get(matMin[1])));
			System.out.println("(oldMatrix.get(matMin[1]).get(oldHeader.indexOf(header.get(i)))) "+(oldMatrix.get(matMin[1]).get(oldHeader.indexOf(header.get(i)))));
			
			dist =( (((clusterCount.get(matMin[0])) * (oldMatrix.get(matMin[0]).get(oldHeader.indexOf(header.get(i))))) + ((clusterCount.get(matMin[1])) * (oldMatrix.get(matMin[1]).get(oldHeader.indexOf(header.get(i)))))) / ( (clusterCount.get(matMin[0]))+(clusterCount.get(matMin[1])) ) ) ;
			temp.add(dist);
			newMatrix.set(i,temp);
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
		//Stellt die werte für die spaltenheader dar
		ArrayList<Integer> clusterCountNew = clusterCount;
		clusterCountNew.add( (clusterCount.get(delZeile) + clusterCount.get(delSpalte)));
		clusterCountNew.set(delZeile, null);
		clusterCountNew.set(delSpalte, null);
		clusterCountNew.removeAll(Collections.singleton(null));
		//clusterCount = clusterCount2;
		//Gleiche Modifikation für die header Liste welche die spaltenköpfe enthält (da ab dem punkt des anfügens des neuen Knotens header der zeilen und der spalten nicht mehr gleich sind)
		ArrayList<String> oldHeader = header;
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
		
		ArrayList<ArrayList<Integer>> newMatrix = calcDist( oldMatrix,  matrix, matMin, clusterCount, clusterCountNew, oldHeader, header);

		System.out.println("newMatrix size " + newMatrix.size());
		for(int i = 0;i< newMatrix.size();i++){
			System.out.println(newMatrix.get(i));
		}
		
		
	}
	


	public static void main(String[] args) throws IOException{
		ArrayList<ArrayList<String>> test = new ArrayList<ArrayList<String>>();

		buildTree();
		
		
	}
}
