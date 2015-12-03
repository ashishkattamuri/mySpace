package timetabling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Neighbourhood {

	ArrayList<StudentEnrollment> studentCourses;
	int maxNoOfTimeSlots;
	ArrayList<String> courseList;
	ArrayList<Integer> courseCapacity;
	int[] capacityRemainingPerTimeSlot;

	public Neighbourhood(ArrayList<StudentEnrollment> studentCourses, int maxNoOfTimeSlots,ArrayList<String> courseList,ArrayList<Integer> courseCapacity,int[] capacityRemainingPerTimeSlot)
	{
		this.studentCourses = studentCourses;
		this.maxNoOfTimeSlots= maxNoOfTimeSlots;
		this.courseList=courseList;
		this.courseCapacity = courseCapacity;
		this.capacityRemainingPerTimeSlot=capacityRemainingPerTimeSlot;
	}


	public ArrayList<String[]>  singleMove(ArrayList<String[]> s,String optimizationFunction)
	{
		ArrayList<String[]> solution= (ArrayList<String[]>) s.clone();
		//swap single exam randomly
		Random random= new Random();
		String courseCode=  courseList.get(random.nextInt(courseList.size()));
		int courseEnrollement= courseCapacity.get(courseList.indexOf(courseCode)); 
		int currentTimeSlot=0;
		int added=0;
		int removedCount =0;
		//remove from current timeSlot
		boolean removed= false;

		for(int i=0;i<solution.size();i++)
		{

			ArrayList<String> coursesInTimeSlot= new ArrayList<>(Arrays.asList(solution.get(i)));

			if (coursesInTimeSlot.contains(courseCode) )
				//if (coursesInTimeSlot.contains(courseCode) && coursesInTimeSlot.size()>1)
			{
				currentTimeSlot=i;
				//System.out.println("removing courseCode :"+courseCode);
				//System.out.println("b4 delete:"+coursesInTimeSlot);
				coursesInTimeSlot.remove(courseCode);
				if(coursesInTimeSlot.size()>0)
				{
					//System.out.println("after delete:"+coursesInTimeSlot);
					String[] array=new String[coursesInTimeSlot.size()];
					array= coursesInTimeSlot.toArray(array);
					solution.set(i, array);
				}
				else
				{
					solution.remove(currentTimeSlot);
				}
				removed= true;
				removedCount=removedCount+1;
				capacityRemainingPerTimeSlot[i]=capacityRemainingPerTimeSlot[i]+courseEnrollement;
				break;
			}

		}

		//ExamSchedule.printSolution(solution);

		//assign to another feasible timeSlot
		boolean newTimeSlotRequired= true;

		if(removed)
		{

			for(int i=0;i<solution.size();i++)
			{
				if(i==currentTimeSlot)
				{
					continue;
				}

				boolean conflict =ExamSchedule.checkOverlapOfCourses(courseCode,solution.get(i) );




				if((!conflict) && (courseEnrollement< capacityRemainingPerTimeSlot[i]) )
				{
					ArrayList<String> coursesInTimeSlot= new ArrayList<>(Arrays.asList(solution.get(i)));
					coursesInTimeSlot.add(courseCode);
					added=added+1;
					//ExamSchedule.capacityRemainingPerTimeSlot[i]= remainingCapacity-currentCourseCapcity;
					String[] array=new String[coursesInTimeSlot.size()];
					array= coursesInTimeSlot.toArray(array);
					solution.set(i, array);
					newTimeSlotRequired=false;
					capacityRemainingPerTimeSlot[i]=capacityRemainingPerTimeSlot[i]-courseEnrollement;
					break;

				}

			}

			if(newTimeSlotRequired)
			{
				if(solution.size()<maxNoOfTimeSlots)
				{
					String temp[]={courseCode};
					solution.add(temp);
					added=added+1;
					int courseEnrollment= courseCapacity.get(courseList.indexOf(courseCode));
					capacityRemainingPerTimeSlot[solution.size()-1]=capacityRemainingPerTimeSlot[solution.size()-1]-courseEnrollement;
				}
				else 
				{
					return s;
				}
			}


			//System.out.println("************************");
			//ExamSchedule.printSolution(solution);


		}

		if(added==removedCount)
		{
			return solution;
		}
		else{
			return s;
		}


	}

	public ArrayList<String[]>  swap(ArrayList<String[]> s,String optimizationFunction)
	{
		ArrayList<String[]> solution = (ArrayList<String[]>) s.clone();

		Random random= new Random();

		String courseCode1,courseCode2;
		courseCode1=  courseList.get(random.nextInt(courseList.size()));
		courseCode2=  courseList.get(random.nextInt(courseList.size()));


		int course1Enrollement=courseCapacity.get(courseList.indexOf(courseCode1));
		int course2Enrollement=courseCapacity.get(courseList.indexOf(courseCode2));

		while(courseCode1==courseCode2)
		{
			courseCode1=  courseList.get(random.nextInt(courseList.size()));
			courseCode2=  courseList.get(random.nextInt(courseList.size()));

		}

		int timeSlot1=0;
		int timeSlot2=0;

		//System.out.println("");
		//System.out.println("courseCode1:"+courseCode1+"courseCode2:"+courseCode2);

		boolean t1found= false;
		boolean t2found= false;
		for(int i=0;i<solution.size();i++)
		{

			ArrayList<String> coursesInTimeSlot= new ArrayList<>(Arrays.asList(solution.get(i)));
			if ( (coursesInTimeSlot.contains(courseCode1) || coursesInTimeSlot.contains(courseCode2))  )
			{
				if(coursesInTimeSlot.contains(courseCode1))
				{
					t1found= true;
					timeSlot1=i;
					if(t1found && t2found)
					{
						break;
					}
				}
				else if(coursesInTimeSlot.contains(courseCode2))
				{
					t2found= true;
					timeSlot2=i;
					if(t1found && t2found)
					{
						break;
					}
				}


			}
		}
		if(timeSlot1!=timeSlot2)
		{
			boolean conflict1 = ExamSchedule.checkOverlapOfCourses(courseCode1, solution.get(timeSlot2)); 
			boolean conflict2 = ExamSchedule.checkOverlapOfCourses(courseCode2, solution.get(timeSlot1));

			//int remainingCapacityInTimeSlot1 = ExamSchedule.capacityRemainingPerTimeSlot[timeSlot1];
			//int remainingCapacityInTimeSlot2 = ExamSchedule.capacityRemainingPerTimeSlot[timeSlot2];


			if( (!conflict1) && (!conflict2) ) //swap the courses
			{
				if(((capacityRemainingPerTimeSlot[timeSlot1]-course1Enrollement)>=course2Enrollement) && ((capacityRemainingPerTimeSlot[timeSlot2]-course2Enrollement)>=course1Enrollement))
				{
					ArrayList<String> t1 = new ArrayList<String>( Arrays.asList(solution.get(timeSlot1)));
					//System.out.println("timeSlot1:"+solution.get(timeSlot1));
					//System.out.println("Adding:"+courseCode2+",removing:"+courseCode1);
					t1.add(courseCode2);
					t1.remove(courseCode1);
					String[] array=new String[t1.size()];
					array= t1.toArray(array);
					solution.set(timeSlot1, array );
					capacityRemainingPerTimeSlot[timeSlot1]=capacityRemainingPerTimeSlot[timeSlot1]-course1Enrollement+course2Enrollement;
					// ExamSchedule.capacityRemainingPerTimeSlot[timeSlot1]= remainingCapacityInTimeSlot1-currentCourseCapcityInTimeSlot1;


					ArrayList<String> t2 = new ArrayList<String>( Arrays.asList(solution.get(timeSlot2)));

					t2.add(courseCode1);
					t2.remove(courseCode2);

					String[] array2=new String[t2.size()];
					array2= t2.toArray(array2);
					solution.set(timeSlot2, array2 );
					capacityRemainingPerTimeSlot[timeSlot2]=capacityRemainingPerTimeSlot[timeSlot2]-course2Enrollement+course1Enrollement;
					//ExamSchedule.capacityRemainingPerTimeSlot[timeSlot2]= remainingCapacityInTimeSlot2-currentCourseCapcityInTimeSlot2;
				}
			}

		}

		return solution;
	}


	public ArrayList<String[]>  moveTwoRandomExams (ArrayList<String[]> s,String optimizationFunction)
	{
		ArrayList<String[]> solution = (ArrayList<String[]>) s.clone();

		//System.out.println("###################################");
		//System.out.println("before:");
		//ExamSchedule.printSolution(solution);

		Random random= new Random();

		ArrayList<String> coursesToMove= new ArrayList<String>();

		String courseCode1,courseCode2;
		courseCode1=  courseList.get(random.nextInt(courseList.size()));
		courseCode2=  courseList.get(random.nextInt(courseList.size()));

		while(courseCode1==courseCode2)
		{
			courseCode1=  courseList.get(random.nextInt(courseList.size()));
			courseCode2=  courseList.get(random.nextInt(courseList.size()));

		}
		coursesToMove.add(courseCode1);
		coursesToMove.add(courseCode2);


		solution= (ArrayList<String[]>) moveCourseToOtherSlot_Util(coursesToMove, solution).clone();


		//System.out.println("after:");
		//ExamSchedule.printSolution(solution);

		return solution;
	}

	public ArrayList<String[]>  moveThreeRandomExams (ArrayList<String[]> s,String optimizationFunction)
	{
		ArrayList<String[]> solution = (ArrayList<String[]>) s.clone();

		Random random= new Random();

		ArrayList<String> courseCheck = new ArrayList<String>(); 

		String courseCode1,courseCode2,courseCode3;
		courseCode1=  courseList.get(random.nextInt(courseList.size()));
		courseCheck.add(courseCode1);
		courseCode2=  courseList.get(random.nextInt(courseList.size()));
		while(courseCheck.contains(courseCode2))
		{
			courseCode2=  courseList.get(random.nextInt(courseList.size()));
		}
		courseCheck.add(courseCode2);

		courseCode3 =  courseList.get(random.nextInt(courseList.size()));
		while(courseCheck.contains(courseCode3))
		{
			courseCode3=  courseList.get(random.nextInt(courseList.size()));
		}
		courseCheck.add(courseCode3);

		solution= (ArrayList<String[]>) moveCourseToOtherSlot_Util(courseCheck, solution).clone();

		//System.out.println("after:");
		//ExamSchedule.printSolution(solution);

		return solution;


	}

	public ArrayList<String[]>  moveFourRandomExams (ArrayList<String[]> s,String optimizationFunction)
	{
		if(courseList.size()>=4)
		{
			ArrayList<String[]> solution = (ArrayList<String[]>) s.clone();

			Random random= new Random();

			ArrayList<String> courseCheck = new ArrayList<String>(); 

			String courseCode1,courseCode2,courseCode3,courseCode4,courseCode5;

			courseCode1=  courseList.get(random.nextInt(courseList.size()));
			courseCheck.add(courseCode1);

			courseCode2=  courseList.get(random.nextInt(courseList.size()));
			while(courseCheck.contains(courseCode2))
			{
				courseCode2=  courseList.get(random.nextInt(courseList.size()));
			}
			courseCheck.add(courseCode2);

			courseCode3 =  courseList.get(random.nextInt(courseList.size()));
			while(courseCheck.contains(courseCode3))
			{
				courseCode3=  courseList.get(random.nextInt(courseList.size()));
			}
			courseCheck.add(courseCode3);

			courseCode4 =  courseList.get(random.nextInt(courseList.size()));
			while(courseCheck.contains(courseCode4))
			{
				courseCode4=  courseList.get(random.nextInt(courseList.size()));
			}
			courseCheck.add(courseCode4);


			//System.out.println("after:");
			//ExamSchedule.printSolution(solution);

			return solution;
		}
		else
			return s;


	}


	public ArrayList<String[]>  moveFiveRandomExams (ArrayList<String[]> s,String optimizationFunction)
	{
		if(courseList.size()>=5)
		{
			ArrayList<String[]> solution = (ArrayList<String[]>) s.clone();

			Random random= new Random();

			ArrayList<String> courseCheck = new ArrayList<String>(); 

			String courseCode1,courseCode2,courseCode3,courseCode4,courseCode5;

			courseCode1=  courseList.get(random.nextInt(courseList.size()));
			courseCheck.add(courseCode1);

			courseCode2=  courseList.get(random.nextInt(courseList.size()));
			while(courseCheck.contains(courseCode2))
			{
				courseCode2=  courseList.get(random.nextInt(courseList.size()));
			}
			courseCheck.add(courseCode2);

			courseCode3 =  courseList.get(random.nextInt(courseList.size()));
			while(courseCheck.contains(courseCode3))
			{
				courseCode3=  courseList.get(random.nextInt(courseList.size()));
			}
			courseCheck.add(courseCode3);

			courseCode4 =  courseList.get(random.nextInt(courseList.size()));
			while(courseCheck.contains(courseCode4))
			{
				courseCode4=  courseList.get(random.nextInt(courseList.size()));
			}
			courseCheck.add(courseCode4);

			courseCode5 =  courseList.get(random.nextInt(courseList.size()));
			while(courseCheck.contains(courseCode5))
			{
				courseCode5=  courseList.get(random.nextInt(courseList.size()));
			}
			courseCheck.add(courseCode5);

			solution= (ArrayList<String[]>) moveCourseToOtherSlot_Util(courseCheck, solution).clone();

			//System.out.println("after:");
			//ExamSchedule.printSolution(solution);

			return solution;
		}
		else
			return s;


	}

	public ArrayList<String[]>  moveWholeTimeSlots(ArrayList<String[]> s,String optimizationFunction)
	{
		ArrayList<String[]> solution = (ArrayList<String[]>) s.clone();

		Random random= new Random();
		int fromTimeSlot= random.nextInt(solution.size());
		int toTimeSlot = random.nextInt(solution.size());

		while(fromTimeSlot== toTimeSlot)
		{
			fromTimeSlot= random.nextInt(solution.size());
			toTimeSlot = random.nextInt(solution.size());
		}

		String courses[]= solution.get(fromTimeSlot);

		//delete and add
		solution.remove(fromTimeSlot);
		solution.add(toTimeSlot, courses);

		return solution;
	}


	public ArrayList<String[]>  swapTimeSlots(ArrayList<String[]> s,String optimizationFunction)
	{
		ArrayList<String[]> solution = (ArrayList<String[]>) s.clone();

		Random random= new Random();
		int timeslot1= random.nextInt(solution.size());
		int timeslot2 = random.nextInt(solution.size());

		while(timeslot1== timeslot2)
		{
			timeslot1= random.nextInt(solution.size());
			timeslot2 = random.nextInt(solution.size());
		}

		String courses1[]= solution.get(timeslot1);
		String courses2[]= solution.get(timeslot2);

		solution.add(timeslot2, courses1);
		solution.remove(courses1);
		solution.add(timeslot1, courses2);
		solution.remove(courses2);



		return solution;
	}


	public ArrayList<String[]>  returnNeighbour(int k, ArrayList<String[]> solution,String optimizationFunction)
	{
		switch(k)
		{
		case 1: return singleMove(solution,optimizationFunction);

		case 2: return swap(solution,optimizationFunction);

		case 3: return moveTwoRandomExams(solution,optimizationFunction);

		case 4: return moveThreeRandomExams(solution,optimizationFunction);

		case 5: return moveFourRandomExams(solution,optimizationFunction);

		case 6: return moveFiveRandomExams(solution,optimizationFunction);

		case 7: return moveWholeTimeSlots(solution,optimizationFunction);

		case 8: return swapTimeSlots(solution,optimizationFunction);

		default: return solution;

		}


	}




	public ArrayList<String[]> moveCourseToOtherSlot_Util(ArrayList<String> courseCode,ArrayList<String[]> s)
	{
		Boolean validSolution= true;
		ArrayList<String[]> solution = (ArrayList<String[]>)s.clone() ; 
		//int currentTimeSlot=0;
		ArrayList<Integer> currentTimeSlot = new ArrayList<Integer>();
		int removed=0;
		int added=0;
		//System.out.println("#######################");
		//System.out.println("courses to move:"+courseCode.toString());

		for(int j=0;j< courseCode.size(); j++)
		{
			for(int i=0;i<solution.size();i++)
			{



				ArrayList<String> coursesInTimeSlot= new ArrayList<>(Arrays.asList(solution.get(i)));
				//if (coursesInTimeSlot.contains(courseCode.get(j)) && coursesInTimeSlot.size()>1)
				if (coursesInTimeSlot.contains(courseCode.get(j))  )
				{
					currentTimeSlot.add(i);
					//System.out.println("removing:"+courseCode.get(j));
					//System.out.println("b4:"+coursesInTimeSlot);
					coursesInTimeSlot.remove(courseCode.get(j));
					int courseEnrollement= courseCapacity.get(courseList.indexOf(courseCode.get(j))); 
					capacityRemainingPerTimeSlot[i]=capacityRemainingPerTimeSlot[i]- courseEnrollement;
					//System.out.println("after:"+coursesInTimeSlot.size());


					if(coursesInTimeSlot.size()>0)
					{
						String[] array=new String[coursesInTimeSlot.size()];
						array= coursesInTimeSlot.toArray(array);
						solution.set(i, array);
					}
					else
					{
						solution.remove(i);
					}


					removed= removed+1;
					break;
				}

			}			
		}

		//ExamSchedule.printSolution(solution);

		//assign to another feasible timeSlot
		//boolean newTimeSlotRequired= true;
		ArrayList<Boolean> newTimeSlotRequired= new ArrayList<Boolean>();
		for(int i=0;i<courseCode.size();i++)
		{
			newTimeSlotRequired.add(true);
		}

		if(removed== courseCode.size())
		{
			for(int j=0;j<courseCode.size();j++)
			{
				for(int i=0;i<solution.size();i++)
				{
					if(i==currentTimeSlot.get(j))
					{
						continue;
					}

					boolean conflict =ExamSchedule.checkOverlapOfCourses(courseCode.get(j),solution.get(i) );


					//System.out.println("conflict in timeslot:"+i+ "for course:"+courseCode.get(j));

					boolean conflict2= (capacityRemainingPerTimeSlot[i]-courseCapacity.get(courseList.indexOf(courseCode.get(j)))<=0)? true:false;



					if(!conflict && (!conflict2) )
					{
						ArrayList<String> coursesInTimeSlot= new ArrayList<>(Arrays.asList(solution.get(i)));

						//.out.println("adding:"+courseCode.get(j));
						//System.out.println("b4:"+coursesInTimeSlot);
						coursesInTimeSlot.add(courseCode.get(j));
						int courseEnrollent = courseCapacity.get(courseList.indexOf(courseCode.get(j)));
						capacityRemainingPerTimeSlot[i]=capacityRemainingPerTimeSlot[i]+courseEnrollent;
						added=added+1;
						//System.out.println("after"+coursesInTimeSlot);
						String[] array=new String[coursesInTimeSlot.size()];
						array= coursesInTimeSlot.toArray(array);
						solution.set(i, array);
						//newTimeSlotRequired=false;
						newTimeSlotRequired.set(j, false);

						//System.out.println("j:"+j+",newTimeSlotRequired:"+newTimeSlotRequired.get(j));
						break;

					}

				}

				if(newTimeSlotRequired.get(j)==true)
				{
					if(solution.size()<maxNoOfTimeSlots)
					{
						//System.out.println("adding in new timSlot:"+courseCode.get(j));
						String temp[]={courseCode.get(j)};
						solution.add(temp);
						added=added+1;
						int courseEnrollement= courseCapacity.get(courseList.indexOf(courseCode.get(j)));
						capacityRemainingPerTimeSlot[solution.size()-1]=capacityRemainingPerTimeSlot[solution.size()-1]-courseEnrollement;
					}
					else
					{
						validSolution=false;
					}
				}

				//System.out.println("************************");
				//ExamSchedule.printSolution(solution);

			}

			if((validSolution!=true) || (added!=removed) )
			{
				return s;
			}

			return solution;
		}

		return s;

	}


}
