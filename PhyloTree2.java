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
		String file = "C:/Users/Acedon/Desktop/Uni/ALBI_Projekt_3/aln-fasta.txt";
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
	
	
	public static void buildTree(int x) throws IOException{
		ArrayList<ArrayList<String>> input = einLesen();
	//	Map<String,String> knoten = new HashMap<String,String>();
		ArrayList<String> seq = input.get(1);
		ArrayList<String> header = input.get(0);
	//	seq.add("ATTTGCGGTA");
	//	seq.add("ATCTGCGATA");
	//	seq.add("ATTGCCGTTT");
	//	seq.add("TTCGCTGTTT");
		float[][] dist = distance(seq);
		if(x==2){
		dist = distance2(seq);
		}
		String tree = "";
		BufferedWriter writer = new BufferedWriter(new FileWriter("PhyloTree.tree"));

		Map<String,Float> kantenlängen = new HashMap<String,Float>();
		for(int i= 0; i< seq.size();i++){
			//knoten.put((input.get(0).get(i)), (input.get(1).get(i)));
			kantenlängen.put((seq.get(i)),(float)0.0);
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
			//löschen der Zwei verschmolzenen Knoten sowohl aus der maatrix 
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
			header.add(neuerKnoten);
			//Erstellen der Kantenlänge für unseren neuen Knoten
			kantenlängen.put(neuerKnoten  ,5*(kantenlängen.get(seq.get(matMin[0])) + kantenlängen.get(seq.get(matMin[1]))+ dist[matMin[0]][matMin[1]]) );
			
			//als auch aus der knotenliste seq

			seq.set(matMin[0], null);
			seq.set(matMin[1], null);
			seq.removeAll(Collections.singleton(null));
			
			//Einfügen der Kantenlängen in metadaten des Baumes nach dem schema von neuerknoten zu matmin[0] kantenläangen[neuerknoten] -klänge[matMin[0]]
			// und neuerknoten zu matMin[1] kantenläangen[neuerknoten] -klänge[matMin[1]]
			
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
		buildTree(1);
		buildTree(2);
	}
	
}
