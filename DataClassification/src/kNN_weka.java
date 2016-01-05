
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.neighboursearch.NearestNeighbourSearch;
 
public class kNN_weka {
	
	
	
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
		
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
			
			
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
 
	public static void CSV2Arff(String inputFile, String outputFile) throws IOException
	{
		   // load CSV
	    CSVLoader loader = new CSVLoader();
	    loader.setSource(new File(inputFile));
	    
	    
	    Instances data = loader.getDataSet();
	 
	    // save ARFF
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(data);
	    saver.setFile(new File(outputFile));
	    //saver.setDestination(new File(outputFile));
	    saver.writeBatch();
		
	}
	
	
	public static void main(String[] args) throws Exception {

		
		
		IBk ibk =  (IBk) (new ObjectInputStream(new FileInputStream("kNNModel.model")).readObject());
		//IBk ibk = (IBk) SerializationHelper.read(new FileInputStream("kNNModel.model"));
		ibk.setKNN(2);
		//CSV2Arff("Master.csv","Master.arff");
		//BufferedReader trainDataFile = readDataFile("Master.arff");
		//Instances data = new Instances(trainDataFile);
		//data.setClassIndex(data.numAttributes() - 1);
		
		//BufferedReader datafile = readDataFile(args[0]);
		CSV2Arff(args[0],"testData.arff");
		BufferedReader testDataFile = readDataFile("testData.arff");
		Instances testData = new Instances(testDataFile);
		//testData.setClassIndex(data.numAttributes()-1);
		
  
		//LinearNNSearch knn = new LinearNNSearch(data);
		
		FastVector classList = new FastVector();
		//ArrayList<String> classList = new ArrayList<>();
		classList.addElement("parrot");
		classList.addElement("chickadee");
		classList.addElement("sparrow");
		classList.addElement("plover");
		classList.addElement("goose");
		classList.addElement("robin");
		classList.addElement("hummingbird");
		classList.addElement("roadrunner");
		classList.addElement("falcon");
		
		
		testData.insertAttributeAt(new Attribute("NewNominal", classList), testData.numAttributes());
		testData.setClassIndex(testData.numAttributes()-1);
	    
		File file= new File(args[1]);
		if (!file.exists()) {
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileWriter fw;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("");
		}
		catch(Exception e)
		{
			
		}
		
		
		
		for(int i=0;i< testData.numInstances();i++)
		{
			Instance first = testData.instance(i);
			//System.out.println("ibk:"+ibk.classifyInstance(first));
			//System.out.println(ibk.getNearestNeighbourSearchAlgorithm());
		
			//System.out.println("ibk:"+ classList.elementAt((int) ibk.classifyInstance(first) ) );
			//bw.append(classList.elementAt((int) ibk.classifyInstance(first) )+"\n");
			
			NearestNeighbourSearch nns = ibk.getNearestNeighbourSearchAlgorithm();//new NearestNeighbourSearch();
		  
			
			//System.out.println(nns.kNearestNeighbours(first,2));
			
			Instances nearestInstances= nns.kNearestNeighbours(first,10);
			
			String output="";
			ArrayList<String> list = new ArrayList<String>();
			
			for(int j=0;j<nearestInstances.numInstances();j++)
			{
				//System.out.println("TEST:"+classList.elementAt((int)ibk.classifyInstance(nearestInstances.instance(j)) ));
				String classInstance= classList.elementAt((int)ibk.classifyInstance(nearestInstances.instance(j)) ).toString(); 
			
				if(!list.contains(classInstance))
				{
					list.add(classInstance);
				}
				
			}
			
			if(list.size()<2)
			{
				for(int k=0;k< classList.size();k++)
				{
					if(!list.contains(classList.elementAt(k)))
					{
						list.add(classList.elementAt(k).toString());
						if(list.size()==2) break;
					}
					
				}
			}
			
			
			int random = 2;
			
			for(int j=0;j< random;j++)
			{
				if(j!=random-1)
				{
					System.out.print(list.get(j)+",");
					bw.append(list.get(j)+",");
				}
				else
				{
					System.out.print(list.get(j)+"\n");
					bw.append(list.get(j)+"\n");
				}
					
				//System.out.println(list.get(0)+","+list.get(1));
			}
			
			
			//knn.setSkipIdentical(false);
			
			//System.out.println("Test:"+first);
			//Instances nearestInstances= knn.kNearestNeighbours(first, 10);
			
		//	Instance nearestInstances= knn.nearestNeighbour(first);
			
			
			//Instance tempInstance= nearestInstances.firstInstance();
		    
			
			//double class1 = ibk.classifyInstance(first);
		 //   double class1 = ibk.classifyInstance(nearestInstances);
		 
		    //System.out.println(""+class1);
		//    System.out.println(classList.elementAt((int)class1 ));
		//	bw.append(classList.elementAt((int)class1 )+"\n");
		    
			
		}
		
		bw.close();
		
		
		}
}