import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PhyloTree2 {

	
	public static ArrayList<ArrayList<String>> einLesen() throws IOException{
		ArrayList<String> outputSeq = new ArrayList<String>();
		ArrayList<String> outputHeader = new ArrayList<String>();
		ArrayList<String> input = new ArrayList<String>();
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI_Projekt_3/file1.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI_Projekt_3/file2.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI_Projekt_3/file3.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI_Projekt_3/file4.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI_Projekt_3/file5.fasta");
		input.add("C:/Users/Acedon/Desktop/Uni/ALBI_Projekt_3/file6.fasta");
		
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

	public static int[] matrixMin(float[][] matrix){
		int[] output = new int[2];
		float min = 9999999;
		for(int i = 0;i<matrix.length;i++){
			for(int j = 0; j <matrix[i].length;j++){
				if(matrix[i][j] != 0 && matrix[i][j] < min){
					min = matrix[i][j];
					output[0] = i;
					output[1] = j;
				}
			}
		}
		
		return output;
	}
	
	
	public static void buildTree() throws IOException{
	//	ArrayList<ArrayList<String>> input = einLesen();
	//	Map<String,String> knoten = new HashMap<String,String>();
		ArrayList<String> seq = new ArrayList<String>();
		seq.add("ATTTGCGGTA");
		seq.add("ATCTGCGATA");
		seq.add("ATTGCCGTTT");
		seq.add("TTCGCTGTTT");
		float[][] dist = distance(seq);
		String tree = "";
		BufferedWriter writer = new BufferedWriter(new FileWriter("PhyloTree.tree"));

		Map<String,Float> kantenl�ngen = new HashMap<String,Float>();
		for(int i= 0; i< seq.size();i++){
			//knoten.put((input.get(0).get(i)), (input.get(1).get(i)));
			kantenl�ngen.put((seq.get(i)),(float)0.0);
		}
		for(int i = 0;i<dist.length;i++){
			System.out.print("ZEILE " + i +" ");
			for(int j = 0; j <dist[i].length;j++){
				System.out.print(dist[i][j] + " ");
			}
			System.out.print("\r\n");
		}
		
		writer.write("#NEXUS"+"\r\n");
		writer.write("\r\n");
		writer.write("BEGIN TREES;\r\n");
		writer.write("TREE TREE1 = \r\n");

		while(seq.size()>=2){
			int[] matMin = matrixMin(dist);
			//l�schen der Zwei verschmolzenen Knoten sowohl aus der maatrix 
			float[][] distMod = new float[seq.size()-2][seq.size()-2];
			int k = 0;
			for(int i = 0; i <dist.length;i++){
				float[] temp = new float[seq.size()-2];
				if(i != matMin[0] && i!= matMin[1]){
					int k2 = 0;
					for(int j = 0; j< dist[i].length;j++){
						
						if(j != matMin[0]&& j!= matMin[1]){
							distMod[k][k2] = dist[i][j];
							k2+=1;
						}
					}
					k+=1;
				}
			}
			String neuerKnoten = seq.get(matMin[0]) + " " + seq.get(matMin[1]);
			//Erstellen der Kantenl�nge f�r unseren neuen Knoten
			kantenl�ngen.put(neuerKnoten  ,5*(kantenl�ngen.get(seq.get(matMin[0])) + kantenl�ngen.get(seq.get(matMin[1]))+ dist[matMin[0]][matMin[1]]) );
			
			//als auch aus der knotenliste seq

			seq.set(matMin[0], null);
			seq.set(matMin[1], null);
			seq.removeAll(Collections.singleton(null));
			
			//Einf�gen der Kantenl�ngen in metadaten des Baumes nach dem schema von neuerknoten zu matmin[0] kantenl�angen[neuerknoten] -kl�nge[matMin[0]]
			// und neuerknoten zu matMin[1] kantenl�angen[neuerknoten] -kl�nge[matMin[1]]
			
			float[] neueZeile = new float[distMod.length +1];
			int k3 = 0;
			for(int i = 0; i<dist.length;i++){
				if(i!=matMin[0]&& i!= matMin[1]){
					neueZeile[k3] = (dist[i][matMin[0]] + dist[i][matMin[1]]) /2;
					k3+=1;
				}
			}
			neueZeile[k3] = (float)0.0;

			float[][] distMod2 = new float[neueZeile.length][neueZeile.length];
			for(int i =0; i<distMod.length; i++){
				for(int j = 0; j<distMod[i].length; j++){
					distMod2[i][j] = distMod[i][j];
					if(j== (distMod.length-1)){
						distMod2[i][j+1] = neueZeile[i];
					}
				}
				if(i ==(distMod.length-1)){
					distMod2[i+1] = neueZeile;
				}
			}
						
			dist = distMod2;
			seq.add(neuerKnoten);
			
			for(int i = 0;i<dist.length;i++){
				System.out.print("ZEILE " + i +" ");
				for(int j = 0; j <dist[i].length;j++){
					System.out.print(dist[i][j] + " ");
				}
				System.out.print("\r\n");
			}
			
			
			
		}
		writer.write(tree);
		writer.write("\r\n");
		writer.write(";\r\n");
		writer.write("END;\r\n");
		writer.close();
		
		
				
	}
	public static void main(String[] args) throws IOException{
		buildTree();
	}
	
}
