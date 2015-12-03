import sys
from csp import *

#______________________________________________________________________________
# n-Queens Problem

def queen_constraint(A, a, B, b):
    """Constraint is satisfied (true) if A, B are really the same variable,
    or if they are not in the same row, down diagonal, or up diagonal."""
    #print "A:"+str(A)
    #print "B:"+str(B)
    #print "a:"+str(a) 
    #print "b:"+str(b) 
    return A == B or (a != b and A + a != B + b and A - a != B - b)

class NQueensCSP(CSP):
    """Make a CSP for the nQueens problem for search with min_conflicts.
    Suitable for large n, it uses only data structures of size O(n).
    Think of placing queens one per column, from left to right.
    That means position (x, y) represents (var, val) in the CSP.
    The main structures are three arrays to count queens that could conflict:
        rows[i]      Number of queens in the ith row (i.e val == i)
        downs[i]     Number of queens in the \ diagonal
                     such that their (x, y) coordinates sum to i
        ups[i]       Number of queens in the / diagonal
                     such that their (x, y) coordinates have x-y+n-1 = i
    We increment/decrement these counts each time a queen is placed/moved from
    a row/diagonal. So moving is O(1), as is nconflicts.  But choosing
    a variable, and a best value for the variable, are each O(n).
    If you want, you can keep track of conflicted vars, then variable
    selection will also be O(1).
    >>> len(backtracking_search(NQueensCSP(8)))
    8
    """
    def __init__(self, n):
        """Initialize data structures for n Queens."""
        CSP.__init__(self, range(n), UniversalDict(range(n)),
                     UniversalDict(range(n)), queen_constraint)
		
        update(self, rows=[0]*n, ups=[0]*(2*n - 1), downs=[0]*(2*n - 1))

    def nconflicts(self, var, val, assignment):

        """The number of conflicts, as recorded with each assignment.
        Count conflicts in row and in up, down diagonals. If there
        is a queen there, it can't conflict with itself, so subtract 3."""
        #print "inConflicts"
        n = len(self.vars)
        
        #print "####"
        #print "self.vars:"+str(self.vars)
        #print "downs:"+str(self.downs)
        #print "ups:"+str(self.ups)
        #print "val:"+str(val)
        #print "var:"+str(var)
        #print "assignment:"+str(assignment)
        #print "nc:rows#"+str(self.rows)
        #print "nc:downs#"+str(self.downs)
        #print "nc:ups#"+str(self.ups)

        
        c = self.rows[val] + self.downs[var+val] + self.ups[var-val+n-1]
        if assignment.get(var, None) == val:
            c -= 3
        return c

    def assign(self, var, val, assignment):
        "Assign var, and keep track of conflicts."
        #print "inAssign:"+str(assignment)+":"+str(var)+":"+str(val)
        oldval = assignment.get(var, None)
        #print("oldval:"+str(oldval))
        if val != oldval:
            if oldval is not None: # Remove old val if there was one
                self.record_conflict(assignment, var, oldval, -1)
            self.record_conflict(assignment, var, val, +1)
            CSP.assign(self, var, val, assignment)

    def unassign(self, var, assignment):
        #print "inUnAssign:"+str(assignment)+":"+str(var)
        "Remove var from assignment (if it is there) and track conflicts."
        if var in assignment:
            self.record_conflict(assignment, var, assignment[var], -1)
        CSP.unassign(self, var, assignment)

    def record_conflict(self, assignment, var, val, delta):
        "Record conflicts caused by addition or deletion of a Queen."
        #print "inRecord_conflict"
        #print "inAssign:"+str(assignment)
        #print "Before:rc:rows#"+str(self.rows)
        #print "Before:rc:downs#"+str(self.downs)
        #print "Before:rc:ups#"+str(self.ups)
        #print "rc:delta#"+str(delta)
        #print "Before:rc:var#"+str(var)
        #print "Before:rc:val#"+str(val)

        n = len(self.vars)
        self.rows[val] += delta
        self.downs[var + val] += delta
        self.ups[var - val + n - 1] += delta
        #print "After:rc:rows#"+str(self.rows)
        #print "After:rc:downs#"+str(self.downs)
        #print "After:rc:ups#"+str(self.ups)

    def display(self, assignment):
        #print "inDisplay"
        "Print the queens and the nconflicts values (for debugging)."
        n = len(self.vars)
        for val in range(n):
            for var in range(n):
                if assignment.get(var,'') == val: ch = 'Q'
                elif (var+val) % 2 == 0: ch = '.'
                else: ch = '-'
                print ch,
            print '    ',
            for var in range(n):
                if assignment.get(var,'') == val: ch = '*'
                else: ch = ' '
                print str(self.nconflicts(var, val, assignment))+ch,
            print
