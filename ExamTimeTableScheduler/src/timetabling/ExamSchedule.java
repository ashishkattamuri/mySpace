package timetabling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;



public class ExamSchedule {



	private static BufferedReader courseFile;
	private static BufferedReader studentFile;
	static ArrayList<String> courseList= new ArrayList<String>();
	static ArrayList<Integer> courseCapacity= new ArrayList<Integer>();

	static int roomCapacity = 0;
	static int maxNoOfTimeSlots = 0;
	static int noOfTimeSlotsAday=5;

	static ArrayList<StudentEnrollment> studentCourseList = new ArrayList<>();
	static ArrayList<String[]> initialSolution= new ArrayList<String[]>();
	static ArrayList<String[]> bestSolution= new ArrayList<String[]>();
	static int[][] schedule;
	static ArrayList<String[]> coursesAssignedForTimeSlot;
	static File file;
	
	
	static String inputCourseFile;
	static String inputStudentFile;
	static String outputExamFile ;
	static String optimizationFunction;
	static int[] capacityRemainingPerTimeSlot;
	static long startTime;
	
	static int noOfNeighboursParameter;
	
	static ArrayList<String> UnassignedcourseList= new ArrayList<String>();
	

	public static void main(String args[]) throws NumberFormatException, IOException
	{
		
		//System.out.println("Executing....");
		
		 inputCourseFile= args[0];
		 inputStudentFile = args[1];
		 outputExamFile = args[2];
		 if(args.length==4)
		 optimizationFunction = args[3];
		 optimizationFunction="1";
		 //noOfNeighboursParameter= Integer.parseInt(args[4]);
		 
		 //System.out.println(inputCourseFile+":"+inputStudentFile+":"+outputExamFile+":"+optimizationFunction);
		 
		
		startTime = System.nanoTime();
		//Reading CourseFile and Student Info file
		readInputfromFiles();

		//int[][] schedule= new int[maxNoOfTimeSlots/noOfTimeSlotsAday][noOfTimeSlotsAday];
		schedule= new int[courseList.size()][3];

		capacityRemainingPerTimeSlot= new int[maxNoOfTimeSlots];
		for(int timeSlot=0;timeSlot< maxNoOfTimeSlots ; timeSlot++)
		{
			capacityRemainingPerTimeSlot[timeSlot]= roomCapacity;
		}

		int assignedCoursesCount=0;


		//String[] assignedCourses= new String[courseList.size()];
		ArrayList<String> assignedCourses =new ArrayList<String>(); 


		//String[][] coursesAssignedForTimeSlot= new String[maxNoOfTimeSlots][courseList.size()];

		coursesAssignedForTimeSlot= new ArrayList<String[]>();

		/*
		for(int i=0;i< maxNoOfTimeSlots; i++)
		{
			String[] temp = null;
			coursesAssignedForTimeSlot.add(temp);

		}
		 */


		while(assignedCoursesCount<courseList.size()){

			/*
			System.out.println("assignedCoursesCount:"+assignedCoursesCount);
			System.out.println("Already assigned Courses:");
			for(int i=0;i<assignedCourses.size(); i++)
			{
				System.out.print(assignedCourses.get(i)+",");
			}
			System.out.println("");

			//System.out.println("pick up an unassigned Course");
			 */

			int  currCourseCodeIndex= pickOneUnassignedCourseIndex(assignedCourses,courseList);


			int courseEnrollement = courseCapacity.get(currCourseCodeIndex);
			String courseCode= courseList.get(currCourseCodeIndex);

			
			//System.out.println("\npicked courseDetails:("+ courseCode +","+courseEnrollement +")" );

			int conflictCount=0;
			
			for(int timeSlot=0; timeSlot<maxNoOfTimeSlots ;timeSlot++)
			{
				//System.out.println("Checking for asssignment in timeSlot: "+timeSlot);
				if(courseEnrollement <= capacityRemainingPerTimeSlot[timeSlot] )
				{
					/*
					 * 
					 Before assigning a course to timeSlot- check if there is no overlap in the student schedule
					 * 
					 */
					
					if(optimizationFunction.equals("1") && (coursesAssignedForTimeSlot.size()< maxNoOfTimeSlots-1) )
					{
						String[] temp= new String[1];
						temp[0]= courseCode ;
						//coursesAssignedForTimeSlot.set(timeSlot,temp);
						int assignedTimeSlot=0;
						if(coursesAssignedForTimeSlot.size()>0)
						{
							assignedTimeSlot= coursesAssignedForTimeSlot.size()-1;
						}
						
						coursesAssignedForTimeSlot.add(assignedTimeSlot,temp);
						
						capacityRemainingPerTimeSlot[assignedTimeSlot]= capacityRemainingPerTimeSlot[assignedTimeSlot]-courseEnrollement;
						schedule[assignedCoursesCount][0]= currCourseCodeIndex;
						schedule[assignedCoursesCount][1]= (assignedTimeSlot/ maxNoOfTimeSlots )+1 ;
						schedule[assignedCoursesCount][2]= (assignedTimeSlot% (noOfTimeSlotsAday-1))+1;
						UnassignedcourseList.remove(courseCode);
						assignedCourses.add(courseCode); 
						assignedCoursesCount++;
					//	System.out.println("#Assigning courseCode :"+courseCode+ " to TimeSlot:"+assignedTimeSlot);
						
						break;
						
					}
						

					else
					{
					String[] courseForGivenTimeSlot;
					//if(coursesAssignedForTimeSlot.get(timeSlot)!=null)
					if(coursesAssignedForTimeSlot.size() >= (timeSlot+1))
					{
						courseForGivenTimeSlot= coursesAssignedForTimeSlot.get(timeSlot).clone();
					}
					else
					{
						courseForGivenTimeSlot=null;
					}

					boolean conflict=checkOverlapOfCourses(courseCode, courseForGivenTimeSlot);

					if(conflict)
					{
						//System.out.print("conflict with timeslot:"+timeSlot+" for course:"+courseCode+"\n");
						
					}
					if(!conflict)
					{
						
						//System.out.println("*********************************************");
						//System.out.println("Assigning courseCode :"+courseCode+ " to TimeSlot:"+timeSlot);
						//System.out.println("*********************************************");
						 

						//if(coursesAssignedForTimeSlot.get(timeSlot)!=null)
						if(coursesAssignedForTimeSlot.size() >= (timeSlot+1) )
						{
							String[] alreadyAssignedcourses= coursesAssignedForTimeSlot.get(timeSlot);
							String[] temp= new String[alreadyAssignedcourses.length+1];

							for(int k=0;k<alreadyAssignedcourses.length;k++)
							{
								temp[k] = alreadyAssignedcourses[k];
							}

							temp[alreadyAssignedcourses.length]= courseCode ;
							//coursesAssignedForTimeSlot.get(timeSlot)= temp;
							coursesAssignedForTimeSlot.set(timeSlot,temp);
						}
						else
						{
							String[] temp= new String[1];
							temp[0]= courseCode ;
							//coursesAssignedForTimeSlot.set(timeSlot,temp);
							coursesAssignedForTimeSlot.add(timeSlot,temp);

						}

						capacityRemainingPerTimeSlot[timeSlot]= capacityRemainingPerTimeSlot[timeSlot]-courseEnrollement;
						schedule[assignedCoursesCount][0]= currCourseCodeIndex;
						schedule[assignedCoursesCount][1]= (timeSlot/ maxNoOfTimeSlots )+1 ;
						schedule[assignedCoursesCount][2]= (timeSlot% (noOfTimeSlotsAday-1))+1;
						UnassignedcourseList.remove(courseCode);
						assignedCourses.add(courseCode); 
						assignedCoursesCount++;

						break;
					}
				}
				}
			}

			initialSolution = coursesAssignedForTimeSlot;



			/*
			for(int i=0;i<courseList.size() ;i++)
			{
				if(i==0 && courseCapacity.get(i)<= roomCapacity)
				{
					assignedCourses[assignedCoursesCount]= courseList.get(i);
					assignedCoursesCount++;

				}

			}
			 */


		}
		//System.out.println("Assigned an Initial Solution");

		/*
		ArrayList<String[]> testCaseSolution= new ArrayList<String[]>();
		String[] temp1= {"001","004","006"};
		String[] temp2= {"002"};
		String[] temp3= {"003"};
		String[] temp4= {"005","007"};
		String[] temp5= {"008"};
		String[] temp6= {"009"};
		String[] temp7= {"010"};
		testCaseSolution.add(temp1);
		testCaseSolution.add(temp2);
		testCaseSolution.add(temp3);
		testCaseSolution.add(temp4);
		testCaseSolution.add(temp5);
		testCaseSolution.add(temp6);
		testCaseSolution.add(temp7);
		 
	    System.out.println("#####Testcase:"+ getCalculatedStudentCost(testCaseSolution, studentCourseList));
		*/

		//System.out.println("Done Scheduling");
		//System.out.println("\nCourse\t\tDay\t\tTimeSlot");
		file= new File(outputExamFile);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(""+getNoOfTimeSlotsOfSolution(initialSolution)+"\t"+getCalculatedStudentCost(initialSolution,studentCourseList)+"\n");
		//System.out.println(""+getNoOfTimeSlotsOfSolution(initialSolution)+"\t"+getCalculatedStudentCost(initialSolution,studentCourseList));
		//System.out.println(""+getNoOfTimeSlotsOfSolution(testCaseSolution)+"\t"+getCalculatedStudentCost(testCaseSolution,studentCourseList));

		for(int i=0;i<initialSolution.size();i++)
		{
			String[] courses= initialSolution.get(i);

			//System.out.println(i+":"+Arrays.toString(courses));
			for(int j=0;j< courses.length ;j++)
			{
				bw.write(courses[j]+"\t"+ ((i/noOfTimeSlotsAday)+1) + "\t"+ ((i%noOfTimeSlotsAday)+1)+"\n" );
				//System.out.println(courses[j]+"\t"+ ((i/noOfTimeSlotsAday)+1) + "\t"+ ((i%noOfTimeSlotsAday)+1) );
			}


		}

		bw.close();


		//VariableNeighbourHoodSearch(initialSolution);

		//long endTime = System.nanoTime();
		//long duration = endTime - startTime;

		writeSolutionToFile(initialSolution);
		//System.out.println("End of Execution");
		//System.out.println("\nDuration:"+duration/100000000);


		/*
		for(int i=0;i<testCaseSolution.size();i++)
		{
			String[] courses= testCaseSolution.get(i);

			//System.out.println(i+":"+Arrays.toString(courses));
			for(int j=0;j< courses.length ;j++)
			{
				System.out.println(courses[j]+"\t"+ ((i/noOfTimeSlotsAday)+1) + "\t"+ (i+1)%noOfTimeSlotsAday );
			}


		}
		 */
		/*
		System.out.println("\n\n");
		for(int i=0;i<schedule.length; i++)
		{
			for(int j=0; j<3; j++)
			{
				if(j==0)
				{
					System.out.print(courseList.get(schedule[i][j])+"\t\t");
				}
				else
				{
					System.out.print(schedule[i][j]+"\t\t");
				}
			}

			System.out.println("");

		}
		 */

		//System.out.println("coursesAssignedForTimeSlot.size():"+coursesAssignedForTimeSlot.size());



	}


	public static void VariableNeighbourHoodSearch (ArrayList<String[]> solution )
	{

		bestSolution= (ArrayList<String[]>)initialSolution;
		Neighbourhood neighbourhood= new Neighbourhood(studentCourseList, maxNoOfTimeSlots, courseList,courseCapacity,capacityRemainingPerTimeSlot);

		//String optimizationChoice="timeSlots";
		//String optimizationChoice="studentCost";
		
		if(optimizationFunction.equals("1"))
		{
			System.out.println("Generating the solution");
			double currentSolutionCost;

			int noOfNeighbourHoods=8;
			int k=1;
			while(k<=noOfNeighbourHoods )
			{
				//System.out.println("k:"+k);
				//generating a random solution from k-th neighbour
				ArrayList<String[]> incumbentSolution =  neighbourhood.returnNeighbour(k, bestSolution, optimizationFunction);
				double incumbentSolutionCost=getCalculatedStudentCost(incumbentSolution, studentCourseList);
				double bestSolutionCost=getCalculatedStudentCost(bestSolution, studentCourseList);
				if(incumbentSolutionCost< bestSolutionCost)
				{
					bestSolution= (ArrayList<String[]>)incumbentSolution.clone();
					writeSolutionToFile(bestSolution);
					//System.out.println("solution changed");
				}

				boolean bestSolutionFound=false;
				for(int i=0;i<20;i++)
				{
					//ArrayList<String[]> tempSolution =  neighbourhood.returnNeighbour(k, incumbentSolution,optimizationFunction);
					ArrayList<String[]> tempSolution =  neighbourhood.returnNeighbour(k,bestSolution ,optimizationFunction);
					currentSolutionCost= getCalculatedStudentCost(tempSolution, studentCourseList);
					bestSolutionCost = getCalculatedStudentCost(bestSolution, studentCourseList);
					if(currentSolutionCost< bestSolutionCost)
					{
						bestSolution= (ArrayList<String[]>) tempSolution.clone();
						writeSolutionToFile(bestSolution);
						bestSolutionFound=true;
						//System.out.println("best solution changed in k:"+k);
						//printSolution(bestSolution);
					}

				}

				if(!bestSolutionFound)
				{
					System.out.println(inputCourseFile+":best solution found in k="+k+" is:"+getCalculatedStudentCost(bestSolution, studentCourseList));
					//printSolution(bestSolution);
					k=k+1;
				}


			}


			//System.out.println("#Best Solution:");
			//printSolution(bestSolution);

		}
		
		else if(optimizationFunction.equals("0"))
		{

			double currentSolutionCost;

			int noOfNeighbourHoods=8;
			int k=1;
			
			//System.out.print(""+inputCourseFile+",");
			
			while(k<=noOfNeighbourHoods)
			{
			//	System.out.println("k:"+k);
				//generating a random solution from k-th neighbour
				ArrayList<String[]> incumbentSolution =  neighbourhood.returnNeighbour(k, bestSolution,optimizationFunction);
				double incumbentSolutionCost=getNoOfTimeSlotsOfSolution(incumbentSolution);
				double bestSolutionCost=getNoOfTimeSlotsOfSolution(incumbentSolution);
				if(incumbentSolutionCost< bestSolutionCost)
				{
					bestSolution= (ArrayList<String[]>)incumbentSolution.clone();
					//System.out.println("solution changed");
				}

				boolean bestSolutionFound=false;
				for(int i=0;i<courseList.size();i++)
				{
					ArrayList<String[]> tempSolution =  neighbourhood.returnNeighbour(k, incumbentSolution,optimizationFunction);
					currentSolutionCost= getNoOfTimeSlotsOfSolution(incumbentSolution);
					bestSolutionCost = getNoOfTimeSlotsOfSolution(incumbentSolution);
					if(currentSolutionCost< bestSolutionCost)
					{
						bestSolution= (ArrayList<String[]>) tempSolution.clone();
						bestSolutionFound=true;
						//System.out.println("best solution changed in k:"+k);
					}

				}

				if(!bestSolutionFound)
				{
					//System.out.println("####best solution found in k="+k+"->"+getNoOfTimeSlotsOfSolution(bestSolution));
					//printSolution(bestSolution);
					///System.out.print(getNoOfTimeSlotsOfSolution(bestSolution)+",");
					k=k+1;
				}


			}


			//System.out.println("####Temp Best Solution:");
			//printSolution(bestSolution);

			//System.out.println("");
		
			
		}
		
		
		

	}



	public static void readInputfromFiles() throws NumberFormatException, IOException
	{
		int courseCount=0;
		String sCurrentLine;

		//courseFile = new BufferedReader(new FileReader("D:\\Artificial_Intelligence_CS540\\Assignment2\\instance1_crs.txt"));
		//studentFile = new BufferedReader(new FileReader("D:\\Artificial_Intelligence_CS540\\Assignment2\\studentFile.txt"));

		
		courseFile = new BufferedReader(new FileReader(inputCourseFile));
		studentFile = new BufferedReader(new FileReader(inputStudentFile));

		while ((sCurrentLine = courseFile.readLine()) != null) {
			courseCount=courseCount+1;
			String[] courses=sCurrentLine.split("\t");
			if(courseCount==1)
			{
				roomCapacity=Integer.parseInt(courses[0]);
				maxNoOfTimeSlots=Integer.parseInt(courses[1]);
				continue;
			}

			courseList.add(courses[0]);
			UnassignedcourseList.add(courses[0]);
			courseCapacity.add(Integer.parseInt(courses[1]));

		}

		//ArrayList<StudentEnrollment> studentCourseList = new ArrayList<>();

		//System.out.println("#####Printing student courses######");

		int StudentNoIndex=1;
		while ((sCurrentLine = studentFile.readLine()) != null) {
			//System.out.println(sCurrentLine);
			String[] studentCourses =  sCurrentLine.split("\t");

			/*
			for(int i=0;i<studentCourses.length;i++)
			{
				System.out.print(studentCourses[i]+"\t");

			}
			System.out.println("");

			 */

			StudentEnrollment studentInfo = new StudentEnrollment();
			studentInfo.setStudentNo(StudentNoIndex);
			studentInfo.setCoursesEnrolled(studentCourses);
			studentCourseList.add(studentInfo);


		}

		//System.out.println("###################################");


	}

	public static int pickOneUnassignedCourseIndex(ArrayList<String> assignedCourses,ArrayList<String> courseList)
	{
	
		int limit= UnassignedcourseList.size();
		Random rand= new Random();
		int randomNo= rand.nextInt(limit);
		
		String courseCode= UnassignedcourseList.get(randomNo);
		
		int returnIndex=0;
		for(int i=0;i<courseList.size();i++)
		{
			if(courseList.get(i).equals(courseCode))
			{
				returnIndex=i;
				break;
			}
		}
		
		return returnIndex;
		
		
		
		/*
		int index = 0;
		for(int i=0;i< courseList.size();i++)
		{
			if(!assignedCourses.contains(courseList.get(i)))
			{
				index=i;
				break;
			}

		}
		return index;
		
		*/
		
	}

	public static boolean checkOverlapOfCourses(String courseCode, String[] coursesAssignedForTimeSlot)
	{
		//get all the overlapping courses of currentCoursecode and 
		//Check none of the courses are assigned previously in sametimeslot

		boolean conflict=false;


		ArrayList<String> commonCoursesWithCurrentCourse= new ArrayList<String>();

		for(int i=0;i<studentCourseList.size();i++)
		{
			StudentEnrollment student= studentCourseList.get(i);
			String[] courses= student.getCoursesEnrolled();

			//System.out.println("courses of student "+i+ "are: "+Arrays.toString(courses));


			for(int j=0;j< courses.length ; j++)
			{
				if(courses[j].equals(courseCode))
				{
					for(int k=0;k< courses.length ;k++){
						commonCoursesWithCurrentCourse.add(courses[k]);
					}
					break;
				}
			}


		}

		/*
		System.out.println("\n overlapping courses with  "+courseCode);

		for(int j=0;j<commonCoursesWithCurrentCourse.size();j++)
		{
			System.out.print(commonCoursesWithCurrentCourse.get(j)+",");
		}
		System.out.println("");


		System.out.println("courses assigned from timeSlot:"+Arrays.toString(coursesAssignedForTimeSlot));
		 */

		if(coursesAssignedForTimeSlot==null)
		{
			return false;
		}
		//check if any of the assigned courses matches overlap with matching curses of student
		for(int assignedCrs=0 ; assignedCrs< coursesAssignedForTimeSlot.length ; assignedCrs++ )
		{
			if(  commonCoursesWithCurrentCourse.contains(coursesAssignedForTimeSlot[assignedCrs]))
			{
				conflict= true;
				break;
			}

		}



		return conflict;
	}

	public static int getNoOfTimeSlotsOfSolution(ArrayList<String[]> solution)
	{
		return solution.size();
	}


	public static double getCalculatedStudentCost(ArrayList<String[]> solution, ArrayList<StudentEnrollment> stEnrollments)
	{
		double studentCost=0;

		double ConsecutiveAssignmentPenalty=0;
		double OvernightAssignmentPenalty=0;

		for(int i=0;i<  stEnrollments.size() ;i++)
		{
			String[] studentCourses= stEnrollments.get(i).getCoursesEnrolled();
			for(int j=0; j<studentCourses.length; j++)
			{
				for(int k=0;k<studentCourses.length ;k++)
				{
					if(!studentCourses[j].equals(studentCourses[k]))
					{
						String course1= studentCourses[j];
						String course2= studentCourses[k];

						int t1=0,t2=0;
						int d1 = 0,d2=0;

						boolean f1= false;
						boolean f2= false;

						for(int timeSlot=0;timeSlot < solution.size(); timeSlot++ )
						{
							ArrayList<String> courseList = new ArrayList<String>(Arrays.asList(solution.get(timeSlot)));
							if(courseList.contains(course1))
							{
								t1= ( (timeSlot+1)% noOfTimeSlotsAday);
								if(t1==0)
								{
									t1=5;
								}
								d1= (timeSlot/noOfTimeSlotsAday)+1;
								f1= true;
							}
							if(courseList.contains(course2))
							{
								t2= ((timeSlot+1)% noOfTimeSlotsAday);
								if(t2==0)
								{
									t2=5;
								}
								d2= (timeSlot/noOfTimeSlotsAday)+1;
								f2= true;
							}


							if(f1 && f2)
							{
								break;
							}

						}

						if(d1==d2)
						{
							int diff= t1-t2;
							if(diff<0) diff= -diff;
							ConsecutiveAssignmentPenalty= ConsecutiveAssignmentPenalty+ Math.pow(2, -diff);
						}
						else
						{
							int diff= d1-d2;
							if(diff<0) diff= -diff;
							OvernightAssignmentPenalty=OvernightAssignmentPenalty+Math.pow(2, -diff);
						}



					}

				}

			}


		}
		studentCost= studentCost+ ((10*ConsecutiveAssignmentPenalty)+ OvernightAssignmentPenalty);



		return studentCost;
	}

	public static void printSolution(ArrayList<String[]> solution )
	{
		System.out.println(""+getNoOfTimeSlotsOfSolution(solution)+"\t"+getCalculatedStudentCost(solution,studentCourseList));
		//System.out.println(""+getNoOfTimeSlotsOfSolution(testCaseSolution)+"\t"+getCalculatedStudentCost(testCaseSolution,studentCourseList));

		for(int i=0;i<solution.size();i++)
		{
			String[] courses= solution.get(i);

			//System.out.println(i+":"+Arrays.toString(courses));
			for(int j=0;j< courses.length ;j++)
			{
				System.out.println(courses[j]+"\t"+ ((i/noOfTimeSlotsAday)+1) + "\t"+ ((i%noOfTimeSlotsAday)+1) );
			}


		}

	}
	
	public static void writeSolutionToFile(ArrayList<String[]> solution )
	{
		
		File file= new File(outputExamFile);
		if (!file.exists()) {
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(""+getNoOfTimeSlotsOfSolution(solution)+"\t"+getCalculatedStudentCost(solution,studentCourseList)+"\n");
			
			for(int i=0;i<solution.size();i++)
			{
				String[] courses= solution.get(i);
				for(int j=0;j< courses.length ;j++)
				{
					bw.write(courses[j]+"\t"+ ((i/noOfTimeSlotsAday)+1) + "\t"+ ((i%noOfTimeSlotsAday)+1)+"\n" );
					
				}


			}

			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			
			//bw.write("\n\n"+inputCourseFile+":Duration:"+duration);
			
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
	}


}
