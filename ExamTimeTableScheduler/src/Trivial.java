import java.io.File;
import java.io.IOException;


public class Trivial {
	
	public static void main(String args[]) throws IOException
	{
		String directory="D:\\EclipseProjects\\CS540\\assign4problems";
		listf(directory);
	
			
	
	}
	
	public static void listf(String directoryName) throws IOException {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	        	if(file.getName().endsWith(".crs")){
	        		String tempFileName= file.getName().replace(".crs", "");
	        		//for(int i=1;i<=8;i++)
	        			//System.out.println("date;timeout -sHUP 10m java timetabling.ExamSchedule ../assign4problems/"+tempFileName+".crs ../assign4problems/"+tempFileName+".stu "+tempFileName+"_"+i+".sol 1 "+i+";date");
	        			System.out.println("java timetabling.ExamSchedule ../assign4problems/"+tempFileName+".crs ../assign4problems/"+tempFileName+".stu "+tempFileName+".sol 0 ");
	        	}
	        } else if (file.isDirectory()) {
	            //listf(file.getAbsolutePath(), files);
	        }
	    }
	}

}
