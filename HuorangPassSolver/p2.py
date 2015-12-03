"""
    CS440 Assignment 2
    Submitted by Ashish Kattamuri . e-id:kattamur

"""


import sys
from search import *


initialBoardState= (('A','B','B','C'),
                       ('A','B','B','C'),
                       ('D','E','E','F'),
                       ('D','G','H','F'),
                       ('I','X','X','J'))



def run_huarong_pass():
    print "in huarang pass"
    instance =  HuarongPass(initialBoardState)
    astar_search(instance).solution()
    
    
        

def  huarong_pass_search(searchTech):
    if(searchTech=='BFS'):
        myInstance =  HuarongPass(initialBoardState)
        #endNode= depth_first_graph_search(myInstance)
        endNode= breadth_first_search(myInstance)
        return endNode.solution()
    if(searchTech=='DFS'):
        myInstance =  HuarongPass(initialBoardState)
        endNode= depth_first_graph_search(myInstance)
        return endNode.solution()
    if(searchTech=='IDS'):
        myInstance =  HuarongPass(initialBoardState)
        endNode= iterative_deepening_search(myInstance)
        return endNode.solution()

    
class HuarongPass (Problem):

    def  __init__(self, initial):
        """The constructor specifies the initial state, and possibly a goal
        state, if there is a unique goal.  Your subclass's constructor can add
        other arguments."""
        self.initial = initial;
        

    def actions(self, state):
        """Return the actions that can be executed in the given
        state. The result would typically be a list, but if there are
        many actions, consider yielding them one at a time in an
        iterator, rather than building them all at once."""
        tupleList=[]


    
        for i in range(len(state)):
            for j in range(len(state[i])):
                if(j-1>=0): #check the left empty element and move left
                    if(state[i][j-1]=='X' and state[i][j]!='X'):
                        if( (state[i][j]!='A' and state[i][j]!='D' and state[i][j]!='F' and state[i][j]!='C' and state[i][j]!='B')):
                           #print "current :"+state[i][j]
                           #print "Move : Left"
                           addtuple=(''+state[i][j],'LEFT')
                           tupleList.append(addtuple)
                           
                    #print "Before checing"
                    if(i+1 < len(state) ):
                          #print "Inside:"+state[i][j]+"("+str(i)+","+str(j)+")"
                          #print state[i][j-1] +"::"+state[i+1][j-1]
                          if(state[i][j-1]=='X' and  state[i+1][j-1]=='X'):
                               if( (state[i][j]=='A' and state[i+1][j]=='A') or
                                   (state[i][j]=='C' and state[i+1][j]=='C') or
                                   (state[i][j]=='D' and state[i+1][j]=='D') or
                                   (state[i][j]=='F' and state[i+1][j]=='F') or
                                   (state[i][j]=='B' and state[i+1][j]=='B')):

                                 #  print "current :"+state[i][j]
                                 # print "Move : Left"
                                   addtuple=(''+state[i][j],'LEFT')
                                   tupleList.append(addtuple)
                           
                                   
 
                #check for right empty element and move right
                if(j+1<len(state[i])):
                    if(state[i][j+1]=='X' and state[i][j]!='X'):
                        if(state[i][j]!='A' and
                           state[i][j]!='C' and
                           state[i][j]!='D' and
                           state[i][j]!='F' and
                           state[i][j]!='B'):
                            #print "current :"+state[i][j]
                            #print "Move : Right"
                            addtuple=(''+state[i][j],'RIGHT')
                            tupleList.append(addtuple)
                            


                    if(i+1 < len(state)):
                        if(state[i][j+1]=='X' and state[i+1][j+1]=='X'):
                            if( (state[i][j]=='A' and state[i+1][j]=='A') or
                                    (state[i][j]=='C' and state[i+1][j]=='C') or
                                   (state[i][j]=='D' and state[i+1][j]=='D') or
                                   (state[i][j]=='F' and state[i+1][j]=='F') or
                                   (state[i][j]=='B' and state[i+1][j]=='B')):

                             #   print "current :"+state[i][j]
                             #  print "Move : Right"
                                addtuple=(''+state[i][j],'RIGHT')
                                tupleList.append(addtuple)

                # Check the empty element in the bottom and move down

                if(i+1< len(state)):
                     if(state[i+1][j]=='X'):
                         
                         if(state[i][j]=='E') :
                             if(j+1<len(state[i])):
                                 if(state[i][j+1]=='E' and state[i+1][j+1]=='X'):
                                     #    print "current :"+state[i][j]
                                     #    print "state[i][j]:"+state[i][j]
                                     addtuple=(''+state[i][j],'DOWN')
                                     tupleList.append(addtuple)
                         
                         elif((( state[i][j]=='D' and state[i-1][j]=='D' ) or
                             ( state[i][j]=='A' and state[i-1][j]=='A' ) or
                             ( state[i][j]=='C' and state[i-1][j]=='C' ) or
                             ( state[i][j]=='F' and state[i-1][j]=='F' ) )
                             ):
                             #print "current :"+state[i][j]
                             #print "Move : Down"
                             addtuple=(''+state[i][j],'DOWN')
                             tupleList.append(addtuple)
                         elif(state[i][j]=='B'):
                             if(j+1<len(state[i])):
                                 if(state[i][j+1]=='B' and state[i+1][j+1]=='X' ):
                                     addtuple=(''+state[i][j],'DOWN')
                                     tupleList.append(addtuple)
                                     
                         elif(state[i][j]=='X'):
                             #do nothing
                             do="nothing"
                         else:
                             #print "current :"+state[i][j]
                             #print "Move : Down"
                             addtuple=(''+state[i][j],'DOWN')
                             tupleList.append(addtuple)


                
                
                #check the empty elements at top and move current element up if applicable
                
                if(i-1>=0 ):
                    if(state[i-1][j]=='X'):
                        if(state[i][j]=='E'):
                            if(j+1<len(state[i]) ):
                                if(state[i][j+1]=='E' and state[i-1][j+1]=='X'):
                                #  print "current :"+state[i][j]
                                #  print "Move : Up"
                                    addtuple=(''+state[i][j],'UP')
                                    tupleList.append(addtuple)
                                
                        
                        elif( (state[i][j]=='D' and state[i-1][j]=='D')
                            or (state[i][j]=='A' and state[i-1][j]=='A')
                            or (state[i][j]=='C' and state[i-1][j]=='C')
                            or (state[i][j]=='F' and state[i-1][j]=='F')
                            ) :
                            
                           # print "current :"+state[i][j]
                           # print "Move : Up"
                            addtuple=(''+state[i][j],'UP')
                            tupleList.append(addtuple)

                        elif( (state[i][j]=='B')):
                            if(j+1<len(state[i])):
                                if(state[i-1][j+1]=='X' and state[i][j+1]=='B'):
                                    addtuple=(''+state[i][j],'UP')
                                    tupleList.append(addtuple)
                                
                        
                        elif (state[i][j]=='X'): do = "nothing"
                        
                        else:
                           # print "current :"+state[i][j]
                           # print "Move : Up"
                            addtuple=(''+state[i][j],'UP')
                            tupleList.append(addtuple)

                
                    
                             
                             
        return tupleList                  
                             

        
    def result(self, state, action):
        """Return the state that results from executing the given
        action in the given state. The action must be one of
        self.actions(state)."""
        currentState=[]
        #print action[0]
        #print action[1]

        direction=action[1]

        # print "in result def"
        for x in range(len(state)):
            tempList=[]
            for y in state[x]:
                tempList.append(y)
            currentState.append(tempList)

        found=False
        
        for x in range(len(currentState)):
            for y in range(len(currentState[x])) :
                #print "#currentState["+str(x)+"]["+str(y)+"]:"+currentState[x][y]
                if(currentState[x][y]==action[0] and  not found):
                    i=x
                    j=y
                    found=True
                    break

        #print "STATE:"+state[i][j]

        
        if(direction=='LEFT'):
            #print "left"
            if(currentState[i][j]=='B'):
                #print "currentState[i][j-1]:"+currentState[i][j-1]
                #print "currentState[i+1][j-1]:"+currentState[i+1][j-1]
                currentState[i][j-1]='B'
                currentState[i+1][j-1]='B'
                currentState[i][j+1]='X'
                currentState[i+1][j+1]='X'
                
            elif(currentState[i][j]=='E'):
                currentState[i][j-1]='E'
                currentState[i][j+1]='X'
                
            elif(currentState[i][j]=='A'
                 or currentState[i][j]=='D'
                 or currentState[i][j]=='C'
                 or currentState[i][j]=='F'):
                currentState[i][j-1]=currentState[i][j]
                currentState[i+1][j-1]=currentState[i+1][j]
                currentState[i][j]='X'
                currentState[i+1][j]='X'
            else:
                currentState[i][j-1]=currentState[i][j]
                currentState[i][j]='X'
                
                
                
                
            
        elif(direction=='RIGHT'):
            #print "right"
            if(currentState[i][j]=='B'):
                currentState[i][j+2]='B'
                currentState[i+1][j+2]='B'
                currentState[i][j]='X'
                currentState[i+1][j]='X'
                
            elif(currentState[i][j]=='E'):
                currentState[i][j]='X'
                currentState[i][j+2]='E'
                
            elif(currentState[i][j]=='A'
                 or currentState[i][j]=='D'
                 or currentState[i][j]=='C'
                 or currentState[i][j]=='F'):
                currentState[i][j+1]=currentState[i][j]
                currentState[i+1][j+1]=currentState[i+1][j]
                currentState[i][j]='X'
                currentState[i+1][j]='X'
            else:
                currentState[i][j+1]=currentState[i][j]
                currentState[i][j]='X'


        elif(direction=='UP'):
            #print "up"
            if(currentState[i][j]=='B'):
                currentState[i-1][j]='B'
                currentState[i-1][j+1]='B'
                currentState[i+1][j]='X'
                currentState[i+1][j+1]='X'
                
            elif(currentState[i][j]=='E'):
                currentState[i-1][j]='E'
                currentState[i-1][j+1]='E'
                currentState[i][j]='X'
                currentState[i][j+1]='X'
                
            elif(currentState[i][j]=='A'
                 or currentState[i][j]=='D'
                 or currentState[i][j]=='C'
                 or currentState[i][j]=='F'):
                currentState[i-1][j]=currentState[i][j]
                currentState[i+1][j]='X'
            else:
                #print "in else::testing"
                currentState[i-1][j]=currentState[i][j]
                currentState[i][j]='X'

            
            
        elif(direction=='DOWN'):
           # print "down"
            if(currentState[i][j]=='B'):
                #print "test:"+str(i)
                #print "test:"+str(j)
                
                currentState[i+2][j]='B'
                currentState[i+2][j+1]='B'
                currentState[i][j]='X'
                currentState[i][j+1]='X'

            elif(currentState[i][j]=='E'):
                currentState[i+1][j]='E'
                currentState[i][j]='X'
                currentState[i+1][j+1]='E'
                currentState[i][j+1]='X'
                
            elif(currentState[i][j]=='A'
                 or currentState[i][j]=='D'
                 or currentState[i][j]=='C'
                 or currentState[i][j]=='F'):
                currentState[i+2][j]=currentState[i][j]
                currentState[i][j]='X'
            else:
                currentState[i+1][j]=currentState[i][j]
                currentState[i][j]='X'
                

        #print str(i)+","+str(j)

        """
        print "@@@@@@@@@"
        for x in state:
            print x
         
        print "@@@@@@@@@@@@@"

        
        print "###############"
        print "Tile:"+action[0]
        print "Direction:"+action[1]
        counter=0
       
        counter=0
        for x in currentState:
            #print x
            for y in x:
                if(y=='X'):
                    counter=counter+1

        
        if(counter>2):
            print "###############GOT WRONG MOVE at ###############################"
            print state
            print currentState
            print state[i][j]
            sys.exit("aa! errors!")
            print "################"
            
         
        
        
        #fo.write("@@@@@@@@@@@@@@")
        #for x in state:
        #    fo.write(x)
        #fo.write("@@@@@@@@@@@@@@")
        
        #fo.write("###############")
        #fo.write("Tile:"+action[0])
        #fo.write("Direction:"+action[1])
        #for x in currentState:
        #    fo.write(x)
            

        """
        tempTuple=((currentState[0][0],currentState[0][1],currentState[0][2],currentState[0][3]),
                   (currentState[1][0],currentState[1][1],currentState[1][2],currentState[1][3]),
                   (currentState[2][0],currentState[2][1],currentState[2][2],currentState[2][3]),
                   (currentState[3][0],currentState[3][1],currentState[3][2],currentState[3][3]),
                   (currentState[4][0],currentState[4][1],currentState[4][2],currentState[4][3]))
            

        
        return tempTuple

    def goal_test(self, state):
        """Return True if the state is a goal. The default method compares the
        state to self.goal, as specified in the constructor. Override this
        method if checking against a single self.goal is not enough."""
        if (state[3][1]=='B' and state[3][1]=='B' and state[4][1]=='B' and state[4][2]=='B'):
            return True
        




#huarong_pass_search('BFS')

#myInstance =  HuarongPass(initialBoardState)
#print myInstance.actions(initialBoardState)
#actions=('C','LEFT')
#print myInstance.result(initialBoardState,actions)

#myInstance =  HuarongPass(initialBoardState)
#print astar_search(myInstance)


#run_huarong_pass()


