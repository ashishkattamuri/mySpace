import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class RandomTimeTableGenerator {
	
	static int numberOfStudents=500;
	static int numberOfCourses=150;
	static int maxLimitOfCoursesForStudent=6;
	//static int minLimitOfCoursesForStudent=2;
	static int totalEnrollment=1800;
	
	static ArrayList<String> courseEnrolled= new ArrayList<String>(); 
	static File file;
	
	private static BufferedReader studentFile;
	static int[][] conflictMarix;
	
	public static void main(String[] args) throws IOException
	{
		
		
		//randomGenerator();
		//CheckIfAllCoursesEnrolled();
		readStudentAndGenerateCourseFile();
		
		String stuFileName="randomInstance.stu";
		calculateConflictDensity(stuFileName,numberOfCourses);
		
		
		
		
		
	}
	
	public static double calculateConflictDensity(String stuFileName, int numberOfCourses) throws IOException{
		
		conflictMarix= new int[numberOfCourses][numberOfCourses];
		for(int i=0;i<numberOfCourses;i++)
		{
			for(int j=0;j<numberOfCourses;j++)
			{
				conflictMarix[i][j]=0;
			}
		}
		
		String sCurrentLine;
		studentFile = new BufferedReader(new FileReader(stuFileName));
		
		while ((sCurrentLine = studentFile.readLine()) != null) {
			
			//System.out.println(""+sCurrentLine);
			String[] list= sCurrentLine.split("\t");
			
			//System.out.println("\n");
			
			for(int i=0;i<list.length; i++)
			{
				for(int j=0;j<list.length ;j++)
				{
					//System.out.println("checking for :"+list[i]+" and "+list[j]+":"+ (!list[i].trim().equals(list[j].trim())));
					if(!list[i].trim().equals(list[j].trim()))
					{
						//System.out.println("Incrementing");
						int temp1= Integer.parseInt(list[i]);
						int temp2= Integer.parseInt(list[j]);
						
						conflictMarix[temp1-1][temp2-1]++; 
						
					}
				}
			}
			
			
		}
		
		
		for(int i=1;i<=numberOfCourses;i++)
		{
			//System.out.print(i+"\t");
		}
		//System.out.println("\n______________________________________________________________________________________________________________________________________________\n");
		
		double conflictDensityCount=0;
		for(int i=0;i<numberOfCourses;i++)
		{
			for(int j=0;j<numberOfCourses;j++)
			{
				//System.out.print(conflictMarix[i][j]+"\t");
				if(conflictMarix[i][j]>=1)
				{
					conflictDensityCount++;
				}
			}
			//System.out.println("");
		}
		
		System.out.println("conflictDensityCount:"+conflictDensityCount);
		double conflictDensity= conflictDensityCount/(numberOfCourses*numberOfCourses);
		
		System.out.println("conflict Density:"+ conflictDensity);
		
		return conflictDensity;
		
	}
	
	
	
	public static void readStudentAndGenerateCourseFile() throws IOException
	{
		file= new File("randomInstance.crs");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		
		
		String sCurrentLine;
		studentFile = new BufferedReader(new FileReader("randomInstance.stu"));
		int[] courseCapaciy= new int[numberOfCourses];
		
		for(int i=0;i<numberOfCourses;i++)
		{
			courseCapaciy[i]=0;
		}
		
		while ((sCurrentLine = studentFile.readLine()) != null) {
			
			//System.out.println(""+sCurrentLine);
			String[] line= sCurrentLine.split("\t");
			
			for(int i=0;i<line.length;i++)
			{
				int temp= Integer.parseInt(line[i]);
				courseCapaciy[temp-1]++;
				
			}
			
		}
		
		bw.write("30\t100\n");
		for(int courseNo=1;courseNo<=numberOfCourses;courseNo++)
		{
			String course="";
			if(courseNo<10)
			{
				course="00"+courseNo;
			}
			else if(courseNo<100)
			{
				course="0"+courseNo;
			}
			else{
				course=""+courseNo;
			}
			
			//System.out.println(course+"\t"+courseCapaciy[courseNo-1]);
			bw.write(course+"\t"+courseCapaciy[courseNo-1]+"\n");
		}
		bw.close();
		
		
	}
	
	
	public static void randomGenerator() throws IOException
	{
		file= new File("HighConflictDensity.stu");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		
		
		Random rand= new Random();
		for(int i=0;i< numberOfStudents; i++)
		{
			int randomNumber= rand.nextInt(maxLimitOfCoursesForStudent-2)+2;
			for(int j=0;j<randomNumber;j++)
			{
				int temp= rand.nextInt(numberOfCourses)+1;
				String course="";
				if(temp<10)
				{
					course="00"+temp;
				}
				else if(temp<100)
				{
					course="0"+temp;
				}
				else{
					course=""+temp;
				}
				//System.out.print(course+"\t");
				bw.write(course+"\t");
				if(!courseEnrolled.contains(course))
				{
					courseEnrolled.add(course);
				}
			}
			
			//System.out.println("");
			bw.write("\n");
			
			
		}
		
		bw.close();
		
	}
	
	
	public static void CheckIfAllCoursesEnrolled() throws IOException
	{
		boolean AllcoursesEnrolled= true;
		for(int i=1;i<=numberOfCourses;i++)
		{
			String course="";
			if(i<10)
			{
				course="00"+i;
			}
			else if(i<100)
			{
				course="0"+i;
			}
			else{
				course=""+i;
			}
			
			if(!courseEnrolled.contains(course))
			{
				//System.out.println("Course Not enrolled:"+course);
				AllcoursesEnrolled=false;
				break;
			}
			
			
		}
		
		if(AllcoursesEnrolled)
		{
			//System.out.println("Generated stu file");
		}
		else
		{
			randomGenerator();
		}
	}
	
	

}
