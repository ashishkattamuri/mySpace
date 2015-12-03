"""
    CS440 Assignment 4
    Submitted by Ashish Kattamuri . e-id:kattamur

"""


import sys
from csp import *
import time
import math





class Knights(CSP):
    
    def __init__(self,k,n):

        CSP.__init__(self, range(2*k), UniversalDict([(x, y) for x in range(n) for y in range(n)]),UniversalDict(range(2*k)),self.constraints)

        #print "self.vars:"+str(self.vars)
        #print "self.domains:"+str(self.domains)
        #print "self.neighbors:"+str(self.neighbors)
        
        #place the queen in 1st row 1st column and check for other columns


    def constraints(self,A, a, B, b):
        noOfKnights= len(self.vars)
        k=noOfKnights/2
        
        n=8
        n=int(math.sqrt(len(self.domains['0'])))
        #print n

        x=a[0]
        y=a[1]

        
        if(a[0]==b[0] and a[1] ==b[1]):
            return False
        
        if( ( A<k and k<=B ) or ( A>=k and k>B ) ):
            return True

        dx=[2,1,-1,-2,-2,-1,1,2]
        dy=[1,2,2,1,-1,-2,-2,-1]

        moves=[]
        
        for item in range(len(dx)):
            nx=x+dx[item]
            ny=y+dy[item]

            if(ny >= 0 and  ny < n and nx > 0 and nx < n ):
                if(b[0]==nx and b[1]==ny):
                    #print "return False"
                    return False
                

        return True


    def goal_test(self,result):
        return True

        

"""
        if((x-2>=0 and y-1>=0 ) and (x-2)==b[0] and y-1==b[1] ):
            #print "1:print true"
            return False
        if(x-2>=0 and y+1<n and x-2==b[0] and y+1==b[1]):
            #print "2:print true"
            return False
        if(x-1>=0 and y+2<n and x-1==b[0] and y+2==b[1] ):
            #print  "3:print true"
            return False
        if(x+1<n and y+2<n and x+1==b[0] and y+2==b[1]):
            #print "4:print true"
            return False
        if(x-1>=0 and y-2>=0 and x+1==b[0] and y+2==b[1]):
            #print "5:print true"
            return False
        if(x+1<n and y-2>=0 and x+1==b[0] and y-2==b[1] ):
            #print "6:print true"
            return False
        if(x+2<n and y+1<n and x+1==b[0] and y-2==b[1]):
            #print "7:print true"
            return False
        if(x+2<n and y-1>=0 and x+2==b[0] and y-1==b[1] ):
            #print "8:print true"
            return False
"""
        

   





def backtracking_solve(k,n):
    #print "in backtracking"
    instance= Knights(k,n)
    

    t0 = time.time()
    print backtracking_search(instance);
    total = time.time() - t0
    print "Backtracking for k:"+str(k) + " and "+"n:"+str(n)+" : "+str(total)

    instance= Knights(k,n)
    t0 = time.time()
    print backtracking_search(instance,select_unassigned_variable=mrv);
    total = time.time() - t0
    print "Backtracking with heuristic MRV for k:"+str(k) + " and "+"n:"+str(n)+" : "+str(total)

    instance= Knights(k,n)
    t0 = time.time()
    print backtracking_search(instance,order_domain_values=lcv);
    total = time.time() - t0
    print "Backtracking with heuristic LCV for k:"+str(k) + " and "+"n:"+str(n)+" : "+str(total)

    instance= Knights(k,n)
    t0 = time.time()
    print backtracking_search(instance,select_unassigned_variable=mrv, order_domain_values=lcv);
    total = time.time() - t0
    print "Backtracking with heuristic MRV and LCV for k:"+str(k) + " and "+"n:"+str(n)+" : "+str(total)

    instance= Knights(k,n)
    t0 = time.time()
    print backtracking_search(instance, inference=mac);
    total = time.time() - t0
    print "Backtracking with heuristic MAC for k:"+str(k) + " and "+"n:"+str(n)+" : "+str(total)

    instance= Knights(k,n)
    t0 = time.time()
    print backtracking_search(instance, inference=forward_checking);
    total = time.time() - t0
    print "Backtracking with heuristic forward_checking for k:"+str(k) + " and "+"n:"+str(n)+" : "+str(total)

    instance= Knights(k,n)
    t0 = time.time()
    print backtracking_search(instance,select_unassigned_variable=mrv, order_domain_values=lcv, inference=mac);
    total = time.time() - t0
    print "Backtracking with heuristic MRV,LCV,MAC for k::"+str(k) + " and "+"n:"+str(n)+" : "+str(total)

    

def AC3_solve(k,n):
    print "in AC3"
    instance= Knights(k,n)
    t0 = time.time()
    print instance.domains
    AC3(instance)
    print instance.domains
    total = time.time() - t0
    

def combined_solve(k,n):
    print "in combined solve"
    instance= Knights(k,n)
    t0 = time.time()
    AC3(instance)
    print backtracking_search(instance);
    total = time.time() - t0
    
    

       
        
        

        
        
    







