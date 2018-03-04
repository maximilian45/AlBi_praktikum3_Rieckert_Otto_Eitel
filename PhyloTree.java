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
		System.out.println("OLDMATRIX IN CALCDIST");
		for(int i = 0;i< oldMatrix.size();i++){
			System.out.println(oldMatrix.get(i));
		}
		
		ArrayList<ArrayList<Integer>> newMatrix = matrix;
		int delZeile = matMin[0]; //Zeilenindex der zu löschenden Zeile
		int delSpalte = matMin[1];// Index der zu löschenden Spalte
		//Aufbauen der neuen Zeile (die letzte spalte kriegt nun auch eine zeile, da der neue knoten die neue letzte spalte ohne zeile bilden wird)
		ArrayList<Integer> newZeile = new ArrayList<Integer>();
		for(int i =0; i<matrix.size(); i++){
			ArrayList<Integer> temp = matrix.get(i);
			for(int j =0; j < temp.size();j++){       // Verbesserungspotential: Muss wahrscheinlich nicht in einer schleife passieren 
				if(j == (temp.size())-1){
					newZeile.add(temp.get(j));
				}
			}
		}
		newMatrix.add(newZeile); // Anfügen der neuen Zeile
		System.out.println("Matrixsize " + newMatrix.size());
		System.out.println("OLD Matrixsize " + oldMatrix.size());
		System.out.println("headersize " + header.size());
		System.out.println(header);
		System.out.println("OLD headersize " + oldHeader.size());
		System.out.println(oldHeader);
		System.out.println("clusterCountsize " + clusterCount.size());
		// Hier findet dann für die Neue Spalte die distanzberechnung statt, indem jeder zeile der entsprechende Betrag angefügt wird
		for(int i =0; i< header.size()-1; i++){ // -1 damit ich nicht den header des neuen knotens bearbeite, newMatrix ist noch NxN und die header sind n+1 lang 
			ArrayList<Integer> temp = newMatrix.get(i);
			int dist =0;
			// clustercount(matMin[0]) gibt mir den N wert des ersten gelöschten wertes ; dann hole ich mir aus der alten matrix den wert aus der ersten gelöschten zeile, an der stelle an der der wert der j-ten zeile der neuen matrix steht
			//und verrechne dies mit dem wert der zweiten gelöschten zeile aus dem zweiten wert der im neuen knoten steckt
			//für den fall dass das minimum in der letzten spalte stand ist die ermittlung dessen indexes nicht nötig und man kann einfach das letzte element nehmen
			if(matMin[1] == oldMatrix.size()){
				dist =( (((clusterCount.get(matMin[0])) * (oldMatrix.get(matMin[0]).get(oldHeader.indexOf(header.get(i))))) + ((clusterCount.get(matMin[1])) * (oldMatrix.get(i).get(matMin[1])))) / ( (clusterCount.get(matMin[0]))+(clusterCount.get(matMin[1])) ) ) ;
			}else{
			dist =( (((clusterCount.get(matMin[0])) * (oldMatrix.get(matMin[0]).get(oldHeader.indexOf(header.get(i))))) + ((clusterCount.get(matMin[1])) * (oldMatrix.get(matMin[1]).get(oldHeader.indexOf(header.get(i)))))) / ( (clusterCount.get(matMin[0]))+(clusterCount.get(matMin[1])) ) ) ;
			}
			temp.add(dist);
			newMatrix.set(i,temp);
		}
		//Für die letzte Zeile mache ich das separat um mir die iteration der schleife oben nicht zu versauen. MUSS NOCH MAL GENAU ABGECHACKT WERDEN OB DIESER SCHRITT AuCH ZUM GEWÜSCHTEN ERGEBNISS FÜHRT 
		ArrayList<Integer> temp = new ArrayList<Integer>();
		ArrayList<Integer> temp2 = newMatrix.get((newMatrix.size()-1));
		for(int i =0;i<(temp2.size()) ;i++ ){
			temp.add(temp2.get(i));
		}
		int dist = 0;
		int k = (newMatrix.size()-1);
		if(matMin[1] == oldMatrix.size()){
			dist =( (((clusterCount.get(matMin[0])) * (oldMatrix.get(matMin[0]).get(oldHeader.indexOf(header.get(k))))) + ((clusterCount.get(matMin[1])) * (oldMatrix.get(k).get(matMin[1])))) / ( (clusterCount.get(matMin[0]))+(clusterCount.get(matMin[1])) ) ) ;
		}else{
		dist =( (((clusterCount.get(matMin[0])) * (oldMatrix.get(matMin[0]).get(oldHeader.indexOf(header.get(k))))) + ((clusterCount.get(matMin[1])) * (oldMatrix.get(matMin[1]).get(oldHeader.indexOf(header.get(k)))))) / ( (clusterCount.get(matMin[0]))+(clusterCount.get(matMin[1])) ) ) ;
		}
		temp.add(dist);
		newMatrix.set(k, temp);
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
		
		//Beginn UPGMA	
		
		//Reverser Ansatz! Da das removen mir den index kaputt macht. UND ER WORKT!!!!!!
		int[] matMin = new int[2];
		matMin = matrixMin(matrix);
		int delZeile = matMin[0]; //Zeilenindex der zu löschenden Zeile
		int delSpalte = matMin[1];// Index der zu löschenden Spalte
		//ArrayList<ArrayList<Integer>> oldMatrix = matrix; // Merken der unmodifizierten matrix für die Distanzberechnung
		ArrayList<ArrayList<Integer>> oldMatrix = new ArrayList<ArrayList<Integer>>();
		
		//UNGLAUBLICH ABER WAHR, zum kopieren der matrix bracuhe ich dieses dumme schleifenkonstrukt sonst kopiert er nur referenzen -.-
		for(int i = 0; i< matrix.size();i++){
			ArrayList<Integer> temp = matrix.get(i);
			ArrayList<Integer> temp2 = new ArrayList<Integer>();
			for(int j = 0; j<temp.size();j++){
				temp2.add(temp.get(j));
				
			}
			oldMatrix.add(temp2);
		}
		
		
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
		ArrayList<Integer> clusterCountNew = new ArrayList<Integer>();
		for(int i = 0; i< clusterCount.size();i++){
			clusterCountNew.add(clusterCount.get(i));
		}
		
		
		
		clusterCountNew.add( (clusterCount.get(delZeile) + clusterCount.get(delSpalte)));
		clusterCountNew.set(delZeile, null);
		clusterCountNew.set(delSpalte, null);
		clusterCountNew.removeAll(Collections.singleton(null));
		//clusterCount = clusterCount2;
		//Gleiche Modifikation für die header Liste welche die spaltenköpfe enthält (da ab dem punkt des anfügens des neuen Knotens header der zeilen und der spalten nicht mehr gleich sind)
		ArrayList<String> oldHeader = new ArrayList<String>();
		for(int i = 0; i< header.size();i++){
			oldHeader.add(header.get(i));
		}
		
		
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
