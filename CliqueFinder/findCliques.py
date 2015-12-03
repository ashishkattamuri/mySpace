
# create a class representing graph
class Graph(object):

    """ create an adjacency matrix to represent the graph"""
    adjacencyMatrix=[]
    contentOfGraph=""
    nodes=[]    

    def __init__(self,fileName):
        self.fileName= fileName
        self.input_graph= open(fileName,"r")
         

    def printLoadedGraph(self):
        #return self.input_graph.read()
        #self.input_graph.close()
        return self.parseLoadedGraph()
        


    def parseLoadedGraph(self):
        array=[]
        lineNo=1;
        for currentLine in self.input_graph:
          #  print "#"+str(lineNo ) + ": "+currentLine
            if(lineNo==1 or currentLine.find('{')!=-1  or  currentLine.find('}')!=-1 ):
                lineNo=lineNo+1;
                continue

            if(currentLine.find("/*")!=-1 ):
                currentLine=currentLine[:currentLine.index("/*")]

            if(currentLine.find(";")!=-1 ):
                currentLine=currentLine[:currentLine.index(";")].strip()


            #print currentLine
            self.contentOfGraph=self.contentOfGraph+currentLine+"\n"

            # identifying the nodes of the graph and set to list nodes
            identifiers= currentLine.split('--')

            for item in identifiers:
                self.nodes.append(item.strip())
            

            lineNo+=1
            #end of for loop - identifing nodes

        self.nodes=list(set(self.nodes))
        #print self.nodes
        self.nodes= sorted(self.nodes)
        #print self.nodes

        # setting the adjacency matrix- identify the edges
        noOfNodes= len(self.nodes)
        self.adjacencyMatrix=  [ [0 for i in range(noOfNodes)] for j in range(noOfNodes) ]
        #print self.adjacencyMatrix

       # print self.contentOfGraph.strip()

        graphContent= self.contentOfGraph.strip().split("\n")

        for item in graphContent:
            edges=item.split("--")
            if(len(edges)>1):
                v1= self.nodes.index(edges[0].strip())
                v2= self.nodes.index(edges[1].strip())
                #print str(v1)+"::"+str(v2)

                self.adjacencyMatrix[v1][v2]=1
                self.adjacencyMatrix[v2][v1]=1

        #end of for loop to note edges
        #print self.adjacencyMatrix

        subgraphs=[]
        for i in range(0,len(self.nodes)):
            neighbourNodes=[];
            #neighbourNodes.append(i)
            for j in range (0, len(self.nodes)):
                if(self.adjacencyMatrix[i][j]==1):
                    neighbourNodes.append(j)

            fullyConnected=True;
            if(len(neighbourNodes)>1):
                for k in range(0,len(neighbourNodes)):
                    for l in range(k+1 ,len(neighbourNodes) ):
                        if(self.adjacencyMatrix[neighbourNodes[l]][neighbourNodes[k]]!=1):
                            fullyConnected=False
                            break

                if(fullyConnected):
                    neighbourNodes.append(i)
                    #print neighbourNodes
                    
                    subgraphs.append(sorted(neighbourNodes))
                    
                    
                    
                else:
                    for k in range(0,len(neighbourNodes)):
                        tempList=[]
                        for l in range(k+1 ,len(neighbourNodes) ):
                            if(self.adjacencyMatrix[neighbourNodes[l]][neighbourNodes[k]]==1):
                                tempList.append(neighbourNodes[k])
                                tempList.append(neighbourNodes[l])
                                tempList.append(i);
                                tempList=list(set(tempList))
                                #print tempList
                                
                                subgraphs.append(sorted(tempList))
                                 
        #subgraphs.append(neighbourNodes)
        subgraphs=map(list,set(map(tuple, subgraphs)))
        #print subgraphs
        cliqueSizes=[]
        for eachSubGraph in subgraphs:
            cliqueSizes.append(len(eachSubGraph))

        maxCliqueSize=max(cliqueSizes)

        cliquePath=""
        
        for eachSubGraph in subgraphs:
            if(len(eachSubGraph)==maxCliqueSize):
                for item in eachSubGraph:
                    cliquePath=cliquePath+self.nodes[item]+"->"
                #print "maximumCliquePath:"+cliquePath[0:len(cliquePath)-2]
                    
                    

            
            
        #print subgraphs
        return maxCliqueSize


def maximum_clique(self):
    
    #print "I am in maximum clique def!"
    return self.printLoadedGraph()
    

        
        
#g=Graph("graph2.txt")
#print maximum_clique(g)

