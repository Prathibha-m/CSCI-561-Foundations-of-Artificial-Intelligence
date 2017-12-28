
/**
 * @author Prathibha
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;


public class Homework {

    public static void PrintFailure() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("output.txt", "UTF-8");
            writer.write("FAIL");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public static void main(String[] args) {

        String algo_type = null;

        try {
            String InputFile = "./input.txt";
            
            FileReader f = new FileReader(InputFile);
            BufferedReader buffer = new BufferedReader(f);
            algo_type = buffer.readLine().toLowerCase();

            int cornerObstacles = 0;
            Board.n = Integer.parseInt(buffer.readLine());
            Board.p = Integer.parseInt(buffer.readLine());
            
            for (int i = 0; i < Board.n; i++) {

                String splitRow = buffer.readLine();
                for (int j = 0; j < Board.n; j++) {
                    if (splitRow.charAt(j) == '2') {
                        Board.numObstacles += 1;
                        if ((i == 0 && j == Board.n - 1) || (i == Board.n - 1 && j == 0) || (i == 0 && j == 0) || (i == Board.n - 1 && j == Board.n - 1)) {
                            cornerObstacles += 1;
                        }
                        if (Board.RowWiseObstacles.containsKey(i)) {
                            Board.RowWiseObstacles.get(i).add(j);

                        } else {
                            Board.RowWiseObstacles.put(i, new HashSet());
                            Board.RowWiseObstacles.get(i).add(j);
                        }
                        if (Board.ColWiseObstacles.containsKey(j)) {
                            Board.ColWiseObstacles.get(j).add(i);

                        } else {
                            Board.ColWiseObstacles.put(j, new HashSet());
                            Board.ColWiseObstacles.get(j).add(i);

                        }
                    } else if (splitRow.charAt(j) == '0') {
                        Board.ZeroPositions += 1;
                    }

                }

            }
            buffer.close();
f (Board.ZeroPositions < Board.p) {
                Homework.PrintFailure();
            }
            
            else
            switch (algo_type) {
                case "dfs":
                    {
                        DFS Tree = new DFS();
                        Tree.traverseTree();
                        break;
                    }

                case "bfs":
                    {
                        BFS Tree = new BFS();
                        Tree.traverseTree();
                        break;
                    }
                case "sa":
                    SA solution = new SA();
                    solution.SimulatedAnnealing();
                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

class Board {

    public static HashMap<Integer, HashSet<Integer>> RowWiseObstacles = new HashMap<>();
    public static HashMap<Integer, HashSet<Integer>> ColWiseObstacles = new HashMap<>();
    public HashMap<Integer, HashSet<Integer>> RowWiseLizards;
    private HashMap<Integer, HashSet<Integer>> ColWiseLizards;
    public HashMap<Integer, Tuple> LizardPositions;
    public static int n, p;
    public int numLizardsPlaced;
    public static int ZeroPositions;
    public static int numObstacles;

    public Board() {
        this.RowWiseLizards = new HashMap<>();
        this.ColWiseLizards = new HashMap<>();
        this.LizardPositions = new HashMap<>();
        this.numLizardsPlaced = 0;
    }

    public Board(Board B) {
        this.RowWiseLizards = new HashMap<>();
        for (int row : B.RowWiseLizards.keySet()) {
            HashSet<Integer> temp = B.RowWiseLizards.get(row);
            this.RowWiseLizards.put(row, new HashSet<>());
            for (int x : temp) {
                this.RowWiseLizards.get(row).add(x);
            }
        }
        this.ColWiseLizards = new HashMap<>();
        for (int col : B.ColWiseLizards.keySet()) {
            HashSet<Integer> temp = B.ColWiseLizards.get(col);
            this.ColWiseLizards.put(col, new HashSet<>());
            for (int x : temp) {
                this.ColWiseLizards.get(col).add(x);
            }
        }
        this.numLizardsPlaced = B.numLizardsPlaced;
        this.LizardPositions = new HashMap<>();
        for (int i : B.LizardPositions.keySet()) {
            Tuple temp = B.LizardPositions.get(i);
            this.LizardPositions.put(i, temp);
        }

    }

    public void insertLizardPosition(Tuple T) {

        if (Board.isObstacleInPos(T) || this.isLizardInPos(T)) {
            return;
        }

        if (this.RowWiseLizards.containsKey(T.row)) {
            this.RowWiseLizards.get(T.row).add(T.col);
        } else {
            this.RowWiseLizards.put(T.row, new HashSet());
            this.RowWiseLizards.get(T.row).add(T.col);
        }
        if (this.ColWiseLizards.containsKey(T.col)) {
            this.ColWiseLizards.get(T.col).add(T.row);
        } else {
            this.ColWiseLizards.put(T.col, new HashSet());
            this.ColWiseLizards.get(T.col).add(T.row);
        }
    }

    public void insertLizardIDinPosition(int ID, Tuple T) {

        this.insertLizardPosition(T);
        this.LizardPositions.put(ID, T);
        System.out.println("Inserting" + this.LizardPositions.get(ID).row + " " + this.LizardPositions.get(ID).col);

    }

    public void deleteLizardPosition(int ID) {
        System.out.println("Deleting" + this.LizardPositions.get(ID).row + " " + this.LizardPositions.get(ID).col);
        this.deleteLizardPosition(this.LizardPositions.get(ID));
        this.LizardPositions.remove(ID);

    }

    public void deleteLizardPosition(Tuple T) {
        if (this.RowWiseLizards.containsKey(T.row)) {
            this.RowWiseLizards.get(T.row).remove(T.col);
            if (this.RowWiseLizards.get(T.row).isEmpty()) {
                this.RowWiseLizards.remove(T.row);
            }
        }

        if (this.ColWiseLizards.containsKey(T.col)) {
            this.ColWiseLizards.get(T.col).remove(T.row);
            if (this.ColWiseLizards.get(T.col).isEmpty()) {
                this.ColWiseLizards.remove(T.col);
            }
        }
    }
    public void writeSolution() {
        PrintWriter writer = null;

        HashSet<Integer> ObstaclesInRow = new HashSet();
        HashSet<Integer> LizardsInRow = new HashSet();
        try {
         writer = new PrintWriter("output.txt","UTF-8");
         writer.write("OK\n");    
         for (int i = 0; i < n; i++) {
         ObstaclesInRow = RowWiseObstacles.get(i);
         LizardsInRow = this.RowWiseLizards.get(i);
         for (int j = 0; j < n; j++) {
         if (ObstaclesInRow != null && ObstaclesInRow.contains(j)) {
         writer.write('2');
         } else if (LizardsInRow != null && LizardsInRow.contains(j)) {
         writer.write('1');
         } else {
         writer.write('0');
         }

         }
         writer.write("\n");
         }
			
         }
         catch (Exception e) {
         e.printStackTrace();
         } finally {
         writer.close();
         }
    }
    public void PrintBoard() {
        HashSet<Integer> ObstaclesInRow = new HashSet();
        HashSet<Integer> LizardsInRow = new HashSet();

        for (int i = 0; i < n; i++) {
            ObstaclesInRow = RowWiseObstacles.get(i);
            LizardsInRow = this.RowWiseLizards.get(i);
            for (int j = 0; j < n; j++) {
                if (ObstaclesInRow != null && ObstaclesInRow.contains(j)) {
                    System.out.print(2);
                } else if (LizardsInRow != null && LizardsInRow.contains(j)) {
                    System.out.print(1);
                } else {
                    System.out.print(0);
                }

            }
            System.out.print("\n");
        }

    }

    public boolean hasConflicts(Tuple T) {
        System.out.println("Conflicts for " + T.row + " " + T.col);
        if (this.isLizardInPos(T) || Board.isObstacleInPos(T)) {
          //  System.out.println("liz/obstacle in Pos");
            return true;
        } else {
            boolean[] x = new boolean[1];
            //System.out.println(this.RowWiseLizards.get(T.row));
            boolean a,b,c,d;
            a=lizardInRowConflict(T,x);
            b=lizardInColConflict(T,x);
            c=lizardInRightDiagConflict(T,x);
            d=lizardInLeftDiagConflict(T,x);
            System.out.println(a+" "+b+" "+c+" "+d+" ");
            return a||b||c||d;
            //return lizardInRowConflict(T, x) || lizardInColConflict(T, x) || lizardInRightDiagConflict(T, x) || lizardInLeftDiagConflict(T, x);
        }
    }

    public boolean lizardInRowConflict(Tuple t, boolean[] nextLiz) {
        Set<Integer> lizards = RowWiseLizards.get(t.row);
        Set<Integer> obstacles = RowWiseObstacles.get(t.row);

        if (lizards != null) {
            //if (obstacles != null) {
                if (lizards.size() == 1 && lizards.contains(t.col)) {
                    nextLiz[0]=false;
                    return false;
                }

                int[] x = new int[1];
                int[] y = new int[1];
                int col = this.findPrevLizardInRow(new Tuple(t.row, t.col), x);

                int obs = this.findPrevObstacleInRow(new Tuple(t.row, t.col), y);
                System.out.println("Row Conflict" + t.row + " " + col + " obs " + t.row + " " + obs);
                System.out.println("Row Right Conflict" + t.row + " " + x[0] + " obs " + t.row + " " + y[0]);
                if (y[0] == -1&&x[0]==-1) {
                    nextLiz[0]= false;
                } else if (x[0] != -1 &&y[0]== -1) {
                    nextLiz[0] = true;
                }else if(y[0]!=-1&&x[0]==-1){
                    nextLiz[0]=false;
                } 
                else {
                    nextLiz[0] = !(t.col < y[0] && y[0] < x[0]);
                }
                if (obs == -1&&col==-1) {
                    return false;
                } else if (col != -1 && obs == -1) {
                    return true;
                } else if(obs!=-1&&col==-1)
                return false;
                else{ System.out.println("Check row");
                    return !(col < obs && obs < t.col);
                }

            /*} else {
                if (lizards.size() == 1 && lizards.contains(t.col)) {
                    nextLiz[0]=false;
                    return false;
                } else {
                    return true;
                }
            }*/
        } else {
            return false;
        }

    }

    public boolean lizardInColConflict(Tuple t, boolean[] nextLiz) {
        HashSet<Integer> lizards = ColWiseLizards.get(t.col);
        HashSet<Integer> obstacles = ColWiseObstacles.get(t.col);

        if (lizards != null) {
            //if (obstacles != null) {
                if (lizards.size() == 1 && lizards.contains(t.row)) {
                    nextLiz[0]=false;
                    return false;
                }
                int[] x = new int[1];
                int[] y = new int[1];
                int row = this.findPrevLizardInCol(new Tuple(t.row, t.col), x);
                int obs = this.findPrevObstacleInCol(new Tuple(t.row, t.col), y);
                System.out.println("Col Conflict" + row + " " + t.col + " obs " + obs + " " + t.col);
                System.out.println("Col Down Conflict" + x[0] + " " + t.col + " obs " + y[0] + " " + t.col);
                
                
                if (y[0] == -1&&x[0]==-1) {
                    nextLiz[0]= false;
                } else if (x[0] != -1 &&y[0]== -1) {
                    nextLiz[0] = true;
                }else if(y[0]!=-1&&x[0]==-1){
                    nextLiz[0]=false;
                }  else {
                    nextLiz[0] = !(t.row < y[0] && y[0] < x[0]);
                }
                  if (obs == -1&&row==-1) {
                    return false;
                } else if (row != -1 && obs == -1) {
                    return true;
                } else if(obs!=-1&&row==-1)
                return false;
                else { System.out.println("Check col");
                    return !(row < obs && obs < t.row);
                }
             /*{else {
                if (lizards.size() == 1 && lizards.contains(t.row)) {
                    nextLiz[0]=false;
                    return false;
                } else {
                    nextLiz[0]=true;
                    return true;
                }
            }*/
        } else {
            nextLiz[0]=false;
            return false;
        }

    }

    public boolean lizardInLeftDiagConflict(Tuple t, boolean[] nextLiz) {

        Tuple x = new Tuple(-1, -1);
        Tuple y = new Tuple(-1, -1);
        Tuple lizard = this.findPrevLizardInLeftDiag(t, x);
        Tuple obs = this.findPrevObstacleInLeftDiag(t, y);
        System.out.println("Left Diag Conflict" + lizard.row + " " + lizard.col + " obs " + obs.row + " " + obs.col);
        System.out.println("Left Diag Conflict" + x.row + " " + x.col + " obs " + y.row + " " + y.col);
        if (x.row == -1||x.col==-1) {
            nextLiz[0] = false;
        } else if(x.row!=0&&x.col!=0&&(y.row==-1||y.col==-1))
        {
             nextLiz[0] =true;
        } else if((x.row == -1||x.col==-1)&&y.row!=-1&&y.col!=-1){
             nextLiz[0] = false;
        }
        else {
            nextLiz[0] = !(x.row >= y.row && y.row > t.row && x.col >= y.col && y.col > t.col);
        }
        if (lizard.row == -1||lizard.col==-1) {
            return false;
        } else if(lizard.row!=0&&lizard.col!=0&&(obs.row==-1||obs.col==-1))
        {
            return true;
        } else if((lizard.row == -1||lizard.col==-1)&&obs.row!=-1&&obs.col!=-1){
            return false;
        } else {
            return !(lizard.row <= obs.row && obs.row < t.row && lizard.col <= obs.col && obs.col < t.col);
        }

    }

    public boolean lizardInRightDiagConflict(Tuple t, boolean[] nextLiz) {

        Tuple x = new Tuple(-1, -1);
        Tuple y = new Tuple(-1, -1);
        Tuple lizard = this.findPrevLizardInRightDiag(t, x);
        Tuple obs = this.findPrevObstacleInRightDiag(t, y);
        System.out.println("Right Diag Conflict" + lizard.row + " " + lizard.col + " obs " + obs.row + " " + obs.col);
        System.out.println("Right Diag Conflict" + x.row + " " + x.col + " obs " + y.row + " " + y.col);
        if (x.row == -1||x.col==-1) {
            nextLiz[0] = false;
        } else if(x.row!=0&&x.col!=0&&(y.row==-1||y.col==-1))
        {
             nextLiz[0] =true;
        } else if((x.row == -1||x.col==-1)&&y.row!=-1&&y.col!=-1){
             nextLiz[0] = false;
        } else {
            nextLiz[0] = !(x.row > y.row && y.row > t.row && x.col < y.col && y.col < t.col);
        }
        if (lizard.row == -1||lizard.col==-1) {
            return false;
        } else if(lizard.row!=0&&lizard.col!=0&&(obs.row==-1||obs.col==-1))
        {
            return true;
        } else if((lizard.row == -1||lizard.col==-1)&&obs.row!=-1&&obs.col!=-1){
            return false;
        } else {
            return !(lizard.row < obs.row && obs.row < t.row && lizard.col > obs.col && obs.col > t.col);
        }

    }

    public int findPrevObstacleInRow(Tuple t, int[] nextObstacleIndex) {
        
        if (!RowWiseObstacles.containsKey(t.row)) {
             nextObstacleIndex[0]=-1;
            return -1;
        }
        HashSet<Integer> obstacles = new HashSet<>(RowWiseObstacles.get(t.row));
        int maxObstacleIndex = Collections.max(obstacles);
        nextObstacleIndex[0] = -1;
        while (maxObstacleIndex >= t.col) {

            obstacles.remove(maxObstacleIndex);
            if (!obstacles.isEmpty()) {
                if (maxObstacleIndex != t.col) {
                    nextObstacleIndex[0] = maxObstacleIndex;
                }
                maxObstacleIndex = Collections.max(obstacles);
            } else {
                if (maxObstacleIndex != t.col) {
                    nextObstacleIndex[0] = maxObstacleIndex;
                }
                maxObstacleIndex=-1;
                break;
            }
        }
        /*if (maxObstacleIndex < t.col) {

            System.out.println("In Row Max Obstacle Index" + maxObstacleIndex + " " + "NextObstacle Index" + nextObstacleIndex[0]);
            return maxObstacleIndex;
        } else { if(maxObstacleIndex!=t.col&&nextObstacleIndex[0]==-1)
            nextObstacleIndex[0]=maxObstacleIndex;
            else
                nextObstacleIndex[0]=-1;
            System.out.println("End: In Row Max Obstacle Index" + maxObstacleIndex + " " + "NextObstacle Index" + nextObstacleIndex[0]);
            return -1;
        }*/
        return maxObstacleIndex;

    }

    public int findPrevObstacleInCol(Tuple t, int[] nextObstacleIndex) {

        if (!ColWiseObstacles.containsKey(t.col)) {
            nextObstacleIndex[0]=-1;
            return -1;
        }
        HashSet<Integer> obstacles = new HashSet<>(ColWiseObstacles.get(t.col));
        int maxObstacleIndex = Collections.max(obstacles);
        //int nextObstacleIndex;
        nextObstacleIndex[0] = -1;
        while (maxObstacleIndex >= t.row) {

            obstacles.remove(maxObstacleIndex);

            if (!obstacles.isEmpty()) {
                if (maxObstacleIndex != t.row) {
                    nextObstacleIndex[0] = maxObstacleIndex;
                }
                maxObstacleIndex = Collections.max(obstacles);
                System.out.println("In Col: Max Obstacle Index"+maxObstacleIndex+" "+"NextObstacle Index"+ nextObstacleIndex);
            } else {
                if (maxObstacleIndex != t.row) {
                    nextObstacleIndex[0] = maxObstacleIndex;
                }
                maxObstacleIndex=-1;
                break;
                
            }
        }

        return maxObstacleIndex;
    }

    public Tuple findPrevObstacleInRightDiag(Tuple t, Tuple next) {

        int maxFinal = Board.n, max, rowFinal = -1;
        next.row = Board.n;
        next.col = -1;
        for (int row : RowWiseObstacles.keySet()) {
            HashSet<Integer> RowSet = new HashSet<>(RowWiseObstacles.get(row));
            if (row < t.row) {
                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);

                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {

                        if (max > t.col && maxFinal > max) {

                            {
                                if (rowFinal < row) {
                                    maxFinal = max;
                                    rowFinal = row;
                                    System.out.println("In Right Diag Max Obstacle Index " + rowFinal + " " + maxFinal);
                                }

                            }

                        }

                    }
                    RowSet.remove(max);

                }
            } else if (row > t.row) {
                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);

                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {

                        if (max < t.col && next.col < max) {

                            {
                                if (next.row > row) {
                                    next.col = max;
                                    next.row = row;
                                    System.out.println("In Right Diag Next Obstacle Index " + next.row + " " + next.col);
                                }

                            }

                        }

                    }
                    RowSet.remove(max);

                }

            }
        }
        if (maxFinal > t.col) {
            if (next.row == Board.n) {
                next.row = -1;
                next.col = -1;
            }
            return new Tuple(rowFinal, maxFinal);
        } else {
            return new Tuple(-1, -1);
        }
    }

    public Tuple findPrevObstacleInLeftDiag(Tuple t, Tuple next) {

        int maxFinal = -1, max, rowFinal = -1;
        next.row = Board.n;
        next.col = Board.n;
        for (int row : RowWiseObstacles.keySet()) {
            HashSet<Integer> RowSet = new HashSet<>(RowWiseObstacles.get(row));
            if (row < t.row) {

                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);
                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {
                        if (max < t.col && max > maxFinal) {
                            {
                                if (row > rowFinal) {

                                    maxFinal = max;
                                    rowFinal = row;
                                    System.out.println("In Left Diag Max Obstacle Index" + rowFinal + " " + maxFinal);
                                }

                            }
                        }
                    }
                    RowSet.remove(max);

                }
            } else if (row > t.row) {
                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);

                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {

                        if (max > t.col && next.col > max) {
                            

                            {
                                if (next.row > row) {
                                    next.col = max;
                                    next.row = row;
                                    System.out.println("In Left Diag Max NextObstacle Index" + next.row + " " + next.col);
                                }

                            }

                        }

                    }
                    RowSet.remove(max);

                }

            }
        }
        if (maxFinal < t.col) {
            if (next.row == Board.n) {
                next.row = -1;
                next.col = -1;
            }
            return new Tuple(rowFinal, maxFinal);
        }
        return new Tuple(-1, -1);
    }

    public int findPrevLizardInRow(Tuple t, int[] nextLizardIndex) {

        int temp;
        if (!RowWiseLizards.containsKey(t.row)) {
             nextLizardIndex[0]=-1;
            return -1;
        }
        HashSet<Integer> lizards = new HashSet<>(RowWiseLizards.get(t.row));
        int maxLizardIndex = Collections.max(lizards);
        nextLizardIndex[0] = -1;
        while (maxLizardIndex >= t.col) {

            lizards.remove(maxLizardIndex);
            if (!lizards.isEmpty()) {
                if (maxLizardIndex != t.col) {
                    nextLizardIndex[0] = maxLizardIndex;
                }
                maxLizardIndex = Collections.max(lizards);
            } else {
                if (maxLizardIndex != t.col) {
                    nextLizardIndex[0] = maxLizardIndex;
                }
                maxLizardIndex=-1;
                break;
            }
        }
        /*if (maxLizardIndex < t.col) {
            System.out.println("In Row Max Lizard Index" + maxLizardIndex + " " + "NextLizard Index" + nextLizardIndex[0]);
            return maxLizardIndex;
        } else {
            if(maxLizardIndex!=t.col&&nextLizardIndex[0]==-1)
            nextLizardIndex[0]=maxLizardIndex;
            else
                nextLizardIndex[0]=-1;
            return -1;
        }*/
        return maxLizardIndex;
    }

    public int findPrevLizardInCol(Tuple t, int[] nextLizardIndex) {

        if (!ColWiseLizards.containsKey(t.col)) {
             nextLizardIndex[0]=-1;
            return -1;
        }
        HashSet<Integer> lizards = new HashSet<>(ColWiseLizards.get(t.col));
        int maxLizardIndex = Collections.max(lizards);
        nextLizardIndex[0] = -1;
        while (maxLizardIndex >= t.row) {

            lizards.remove(maxLizardIndex);

            if (!lizards.isEmpty()) {
                if (maxLizardIndex != t.row) {
                    nextLizardIndex[0] = maxLizardIndex;
                }
                maxLizardIndex = Collections.max(lizards);
            } else {
                if (maxLizardIndex != t.row) {
                    nextLizardIndex[0] = maxLizardIndex;
                }
                maxLizardIndex=-1;
               
                break;
            }
        }
        /*if (maxLizardIndex < t.row) {
            return maxLizardIndex;
        } else { if(maxLizardIndex!=t.row)
            nextLizardIndex[0]=maxLizardIndex;
            else
                nextLizardIndex[0]=-1;
            return -1;
        }*/
        return maxLizardIndex;
    }

    public Tuple findPrevLizardInLeftDiag(Tuple t, Tuple next) {

        int maxFinal = -1, max, rowFinal = -1;
        next.row = Board.n;
        next.col = Board.n;
        for (int row : RowWiseLizards.keySet()) {
            HashSet<Integer> RowSet = new HashSet<>(RowWiseLizards.get(row));
            if (row < t.row) {

                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);
                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {
                        if (max < t.col && max > maxFinal) {
                            {
                                if (row > rowFinal) {

                                    maxFinal = max;
                                    rowFinal = row;
                                    System.out.println("In Left Diag MaxLizard Index" + next.row + " " + next.col);
                                }

                            }
                        }
                    }
                    RowSet.remove(max);

                }
            } else if (row > t.row) {
                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);

                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {

                        if (max > t.col && next.col > max) {
                            

                            {
                                if (next.row > row) {
                                   // System.out.println("In Left Diag Max NextObstacle Index" + next.row + " " + next.col);
                                    next.col = max;
                                    next.row = row;
                                    System.out.println("In Left Diag NextLizard Index" + next.row + " " + next.col);
                                }

                            }

                        }

                    }
                    RowSet.remove(max);

                }

            }
        }
        if (maxFinal < t.col) {
            if (next.row == Board.n) {
                next.row = -1;
                next.col = -1;
            }
            return new Tuple(rowFinal, maxFinal);
        }
        else {
            if (next.row == Board.n) {
                next.row = -1;
                next.col = -1;
            }
             return new Tuple(-1, -1);
        }
        
    }

    public Tuple findPrevLizardInRightDiag(Tuple t, Tuple next) {

        int maxFinal = Board.n, max, rowFinal = -1;
        next.row = Board.n;
        next.col = -1;
        for (int row : RowWiseLizards.keySet()) {
            HashSet<Integer> RowSet = new HashSet<>(RowWiseLizards.get(row));
            if (row < t.row) {

                //temp1=row;
                //temp2=row;
                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);

                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {

                        if (max > t.col && maxFinal > max) {

                            {
                                if (rowFinal < row) {
                                    maxFinal = max;
                                    rowFinal = row;
                                    System.out.println("In Right Diag Max Lizard Index " + rowFinal + " " + maxFinal);
                                }

                            }

                        }

                    }
                    RowSet.remove(max);

                }
            } else if (row > t.row) {
                while (!RowSet.isEmpty()) {
                    max = Collections.max(RowSet);

                    if (Math.abs(max - t.col) == Math.abs(row - t.row)) {

                        if (max < t.col && next.col < max) {
                            

                            {
                                if (next.row > row) {
                                    next.col = max;
                                    next.row = row;
                                }

                            }

                        }

                    }
                    RowSet.remove(max);

                }

            }
        }
        System.out.println("In Right Diag Max Lizard Index "+rowFinal+" "+maxFinal+" NextObstacle Index"+next.row+" "+next.col);
        if (next.row == Board.n) {
                next.row = -1;
                next.col = -1;
            }
        if (maxFinal > t.col) {
            
            return new Tuple(rowFinal, maxFinal);
        } else {
           
            return new Tuple(-1, -1);
        }
    }

    public int verifyNumLizardsPlaced(Board B) {
        int num = 0;

        for (HashSet i : B.RowWiseLizards.values()) {
            num += i.size();
        }

        return num;
    }

    public boolean isGoal(Board B) {

        return B.numLizardsPlaced == Board.p;
    }

    public boolean isGoalState(Board B) {
        boolean flag = false;
        if (B.LizardPositions.size() != Board.p - 1) {
            return false;
        }
        for (int i : B.LizardPositions.keySet()) {

            boolean[] w = new boolean[1];
            boolean[] x = new boolean[1];
            boolean[] y = new boolean[1];
            boolean[] z = new boolean[1];

            flag = lizardInRowConflict(B.LizardPositions.get(i), w) || lizardInColConflict(B.LizardPositions.get(i), x) || lizardInRightDiagConflict(B.LizardPositions.get(i), y) || lizardInLeftDiagConflict(B.LizardPositions.get(i), z);
            flag = flag || x[0] || y[0] || z[0] || w[0];
            if (flag == true) {
                return false;
            }

        }
        return true;
    }

    public static boolean isObstacleInPos(Tuple T) {
        if (RowWiseObstacles.containsKey(T.row)) {
            return RowWiseObstacles.get(T.row).contains(T.col);
        } else {
            return false;
        }
    }

    public boolean isObstacleInDiag(Tuple t) {

        for (int row : RowWiseObstacles.keySet()) {
            HashSet<Integer> RowSet = RowWiseObstacles.get(row);
            if (RowSet == null) {
                return false;
            }
            for (int col : RowSet) {
                if (Math.abs(col - t.col) == Math.abs(row - t.row)) {
                    return true;
                }

            }
        }
        return false;

    }

    public boolean isLizardInDiag(Tuple t) {

        for (int row : this.RowWiseLizards.keySet()) {
            HashSet<Integer> RowSet = this.RowWiseLizards.get(row);
            if (RowSet == null) {
                return false;
            }
            for (int col : RowSet) {
                if (Math.abs(col - t.col) == Math.abs(row - t.row)) {

                    return true;
                }

            }
        }
        return false;

    }

    public boolean isLizardInPos(Tuple T) {
        if (this.RowWiseLizards.containsKey(T.row)) {
            return this.RowWiseLizards.get(T.row).contains(T.col);
        } else {
            return false;
        }
    }

    public int conflictCost(Board B) {
        int cost = 0;
        int temp;
        boolean a, b, c, d;
        boolean[] w = new boolean[1];
        boolean[] x = new boolean[1];
        boolean[] y = new boolean[1];
        boolean[] z = new boolean[1];

        for (int i : this.LizardPositions.keySet()) {
            Tuple T = this.LizardPositions.get(i);
            a = lizardInRowConflict(T, w);
            b = lizardInColConflict(T, x);
            c = lizardInLeftDiagConflict(T, y);
            d = lizardInRightDiagConflict(T, z);
            System.out.println("Tuple " + T.row + " " + T.col + " Conflicts Row Up " + a + " Col Up " + b + " Left Diag" + c + " Right Diag" + d + " Row down" + w[0] + " Col Up" + x[0] + " Left down" + y[0] + " Right down" + z[0]);
            if (a) {
                cost++;
                
            }
            if (w[0]) {
                    cost++;
                }

            if (b) {
                cost++;
                
            }
            if (x[0]) {
                    cost++;
                }
            if (c) {
                cost++;
                
            }
            if (y[0]) {
                    cost++;
                }

            if (d) {
                cost++;
                
            }
            if (z[0]) {
                    cost++;
                }

        }
        System.out.println("Cost: " + cost);
        return cost;

    }
}

class Tuple {

    public int row, col;

    Tuple(int i, int j) {
        row = i;
        col = j;
    }
}

class Node {

    int depth;
    Board state;
    Tuple T;
    ArrayList<Node> children;

    public Node() {

        this.depth = 0;
        this.state = new Board();
        this.T = new Tuple(-1, -1);
    }

    public Node(Node p) {

        this.state = new Board(p.state);

        this.T = new Tuple(p.T.row, p.T.col);
    }
}

class BFS {

    public static ArrayList<Node> queue = new ArrayList<>();
    Node Tree;

    public BFS() {

        Tree = new Node();

    }

    public void traverseTree() {
        
        queue.add(Tree);
        boolean GoalFound = false;
        while (queue.isEmpty() == false) {

            Node N = queue.remove(0);

            if (N.T.col != -1) {
                N.state.insertLizardPosition(N.T);
                N.state.numLizardsPlaced += 1;
                System.out.println("Board");
                N.state.PrintBoard();
            }
            if (N.state.isGoal(N.state)) {
                GoalFound = true;
                N.state.writeSolution();
                break;
            }
                
                int col = Board.n - 1;
                if(Board.numObstacles==0&&N.T.col==-1)
                    col=(Board.n+1)/2-1;
                int flag = 0;
                int r = N.T.row;
                while (col >= 0 && r < (Board.n - 1)) {
                    Node child = new Node(N);
                    child.T.row = r + 1;
                    child.T.col = col;
                    //System.out.println("States:Parent and Child\n"+N.state+" "+child.state);
                    if (Board.isObstacleInPos(child.T) || child.state.isLizardInPos(child.T)) {
                        col -= 1;
                        if (col == 0 && flag == 0 && Board.numObstacles != 0) {
                            col = Board.n - 1;
                            r += 1;
                        }
                        continue;
                    }
                    if (child.state.hasConflicts(child.T) == false) {

                        flag = 1;
                        queue.add(child);
                    }
                    if (col == 0 && flag == 0 && Board.numObstacles != 0) {
                        col = Board.n - 1;
                        r += 1;

                    }
                    col -= 1;
                }
            
            if (Board.RowWiseObstacles.containsKey(N.T.row)) {
                if (N.state.RowWiseLizards.get(N.T.row) != null) {
                    if (Board.RowWiseObstacles.get(N.T.row).size() < N.state.RowWiseLizards.get(N.T.row).size()) {
                        continue;
                    }
                }

                {

                     col = Board.n - 1;
                    while (col > N.T.col) {
                        Node child = new Node(N);

                        child.T.row = N.T.row;
                        child.T.col = col;

                        if (Board.isObstacleInPos(child.T) || child.state.isLizardInPos(child.T)) {
                            col -= 1;
                            continue;
                        }
                        // 
                        if (child.state.hasConflicts(child.T) == false) {

                            queue.add(child);
                        //    System.out.println("Tuple" + child.T.row + " " + child.T.col);
                        //    System.out.println("Pushed");
                        }
                        col -= 1;
                    }
                }
            }
            if (N.T.row == -1 && Board.numObstacles != 0) {
                for (int i = Board.n - 1; i > 0; i--) {
                    for (int j = Board.n - 1; j >= 0; j--) {
                        if (Board.isObstacleInPos(new Tuple(i, j))) {
                            continue;
                        }

                        Node firstChild = new Node(N);
                        firstChild.T.row = i;
                        firstChild.T.col = j;
                        queue.add(firstChild);
                     //   System.out.println("Pushed " + i + " " + j);
                    }
                }
            }

        }

        if (queue.isEmpty() && GoalFound == false) {
            Homework.PrintFailure();

        }

    }
}

class DFS {

    public static Stack<Node> stack = new Stack<>();
    Node Tree;

    public DFS() {

        Tree = new Node();

    }

    public void traverseTree() {

        stack.push(Tree);
        boolean GoalFound = false;
        while (stack.isEmpty() == false) {

            Node N = stack.pop();

            if (N.T.col != -1) {
                N.state.insertLizardPosition(N.T);
                N.state.numLizardsPlaced += 1;
                N.state.PrintBoard();
            }
            if (N.state.isGoal(N.state)) {
                GoalFound = true;
              //  System.out.println(N.state.numLizardsPlaced);
              //  System.out.println("OK");
               N.state.writeSolution();
                break;
            }
            if (N.T.row == -1 && Board.numObstacles != 0) {
                for (int i = Board.n - 1; i > 0; i--) {
                    for (int j = Board.n - 1; j >= 0; j--) {
                        if (Board.isObstacleInPos(new Tuple(i, j))) {
                            continue;
                        }

                        Node firstChild = new Node(N);
                        firstChild.T.row = i;
                        firstChild.T.col = j;
                        stack.push(firstChild);
                   //     System.out.println("Pushed " + i + " " + j);
                    }
                }
            }
            
                int col = Board.n - 1;
                if(Board.numObstacles==0&&N.T.col==-1)
                    col=(Board.n+1)/2-1;
                int flag = 0;
                int r = N.T.row;
                while (col >= 0 && r < (Board.n - 1)) {
                    Node child = new Node(N);
                    child.T.row = r + 1;
                    child.T.col = col;
                    //System.out.println("States:Parent and Child\n"+N.state+" "+child.state);
                    if (Board.isObstacleInPos(child.T) || child.state.isLizardInPos(child.T)) {
                        col -= 1;
                        if (col == 0 && flag == 0 && Board.numObstacles != 0) {
                            col = Board.n - 1;
                            r += 1;
                        }
                        continue;
                    }
                  //  System.out.println("OTuple" + child.T.row + " " + child.T.col);
                    if (child.state.hasConflicts(child.T) == false) {
                    //    System.out.println("Pushed " + child.T.row + " " + child.T.col);
                        flag = 1;
                        stack.push(child);
                    }
                    //System.out.println("Column and flag " + col + flag);
                    if (col == 0 && flag == 0 && Board.numObstacles != 0) {
                        col = Board.n - 1;
                        r += 1;

                    }
                    col -= 1;
                }
            
            if (Board.RowWiseObstacles.containsKey(N.T.row)) {
                if (N.state.RowWiseLizards.get(N.T.row) != null) {
                    if (Board.RowWiseObstacles.get(N.T.row).size() < N.state.RowWiseLizards.get(N.T.row).size()) {
                        continue;
                    }
                }

                {

                     col = Board.n - 1;
                    while (col > N.T.col) {
                        Node child = new Node(N);

                        child.T.row = N.T.row;
                        child.T.col = col;

                        if (Board.isObstacleInPos(child.T) || child.state.isLizardInPos(child.T)) {
                            col -= 1;
                            continue;
                        }
                        // 
                        if (child.state.hasConflicts(child.T) == false) {

                            stack.push(child);

                        }
                        col -= 1;
                    }
                }
            }

        }

        if (stack.isEmpty() && GoalFound == false) {
            Homework.PrintFailure();

        }

    }
}

class SA {

    public static Node Current = new Node();
    public static Node Next;
    public static Random RandomNum = new Random();
    public static double temperature;

    @SuppressWarnings("empty-statement")
    public SA() {
        int temp = 0;
        int ID, row = 0;
        if(Board.numObstacles==0)
        {
            if(Board.n<Board.p)
            {Homework.PrintFailure(); return;}
            else
            {
                for(int i=0;i<Board.n;i++)
                {
                    int tx=RandomNum.nextInt(Board.n);
                    Current.state.insertLizardIDinPosition(i, new Tuple(i,tx));
                }
            }
        }
        else{
        for (ID = 0; ID < Board.p;) {
            int tx = RandomNum.nextInt(Board.n);
            int ty = RandomNum.nextInt(Board.n);
            while (Board.isObstacleInPos(new Tuple(tx, ty)) == true || Current.state.isLizardInPos(new Tuple(tx, ty)) == true) {
                tx = RandomNum.nextInt(Board.n);
                ty = RandomNum.nextInt(Board.n);
            }
            Current.state.insertLizardIDinPosition(ID, new Tuple(tx, ty));
            ID++;
        }
        }
        
    }

    public void SimulatedAnnealing() {

        double delta;
        double probability;
        double rand;
        double iteration;
        final long NANOSEC_PER_SEC = 1000l * 1000 * 1000;

        long startTime = System.nanoTime();
        //temperature=(System.nanoTime() - startTime);
        iteration=2;
        //temperature=0.5;
        while (((System.nanoTime() - startTime)) <4.7 * 60 * NANOSEC_PER_SEC) {
            temperature=3/(Math.log(iteration));
            iteration+=0.5;
            
            int currentCost = Current.state.conflictCost(Current.state);
            if (currentCost == 0) {
            //    System.out.println("OK");
                Current.state.writeSolution();
                return;
            }
            
            System.out.println("Current Board Config");
            Current.state.PrintBoard();
            findNext();

            delta = currentCost-Next.state.conflictCost(Current.state);
            System.out.println(delta);
            probability = Math.exp((delta)/temperature);
            System.out.println(probability);
            rand = Math.random();

            if (delta> 0) {
                System.out.println("Going to Next");
                Current = Next;
            } else if (rand <= probability) {
                System.out.println("Bad Decision but Going to Next");
                Current = Next;
            } else {
                System.out.println("Finding diff Next");
            }
        }
        if (Current.state.isGoalState(Current.state)) {
          //  System.out.println("OK");
            Current.state.PrintBoard();
        } else {
            Homework.PrintFailure();
        }

    }

    public void findNext() {
        int randNum = RandomNum.nextInt(Board.p);
        Next = new Node(Current);
        Tuple T = Next.state.LizardPositions.get(randNum);
        System.out.println("Lizard ID" + randNum);
        int tx = RandomNum.nextInt(Board.n);
        int ty = RandomNum.nextInt(Board.n);
        if(Board.numObstacles==0)
        { while(T==null)
        {
            randNum = RandomNum.nextInt(Board.p);
             T = Next.state.LizardPositions.get(randNum);
        }
        while(ty==T.col)
        {   
            ty = RandomNum.nextInt(Board.n);
        }
            Next.state.deleteLizardPosition(randNum);
            Next.state.insertLizardIDinPosition(randNum, new Tuple(randNum, ty));
        }
        else{
        while ((Board.isObstacleInPos(new Tuple(tx, ty)) == true || Current.state.isLizardInPos(new Tuple(tx, ty)) == true)||(tx==T.row&&ty==T.col)) {
            tx = RandomNum.nextInt(Board.n);
            ty = RandomNum.nextInt(Board.n);
        }
        Next.state.deleteLizardPosition(randNum);
        Next.state.insertLizardIDinPosition(randNum, new Tuple(tx, ty));
        }
    }
}
