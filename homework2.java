
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

class Cell {

    char Fruit;
    int cluster;
}

class Tuple {

    int i, j;

    public Tuple() {
        //visited=false;
    }

    public Tuple(int x, int y) {
        this.i = x;
        this.j = y;
    }

}

class FruitRage {

    static int N, P,B;
    static boolean GameOver;
    static float time;
    Cell Cells[][];
    int score;
    int depth;
    
    int StaticScore;
    HashMap<Integer, Integer> Size;
    HashMap<Integer, HashSet<Tuple>> ClusterLabels;

    public FruitRage() {
        Cells = new Cell[N][N];
        for (int i = 0; i < N; i++) {  
            for (int j = 0; j < N; j++) { 
                Cells[i][j] = new Cell();
                Cells[i][j].cluster = -1;
            }
        }

    }
   
    public FruitRage(String input) {   //PQ=new HashMap<>();
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new FileReader(input));
            N = Integer.parseInt(buffer.readLine().trim());
            P = Integer.parseInt(buffer.readLine().trim());
            time = Float.parseFloat(buffer.readLine().trim());
            //System.out.println(N+" "+P+" "+time);
            GameOver=true;
            Cells = new Cell[N][N];
            for (int i = 0; i < N; i++) {   //Cells[i]=new Cell[N];
                String splitRow = buffer.readLine();
                for (int j = 0; j < N; j++) {   //System.out.println(i+" "+j+" "+splitRow);
                    Cells[i][j] = new Cell();
                    Cells[i][j].Fruit = splitRow.charAt(j);
                    if(Cells[i][j].Fruit!='*')
                        GameOver=false;
                    Cells[i][j].cluster = -1;
                }
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public FruitRage(FruitRage Board) {
        this.Cells = new Cell[N][N];
        for (int i = 0; i < N; i++) {

            for (int j = 0; j < N; j++) {
                Cells[i][j] = new Cell();
                this.Cells[i][j].Fruit = Board.Cells[i][j].Fruit;
                this.Cells[i][j].cluster = -1;
            }
        }

        this.ClusterLabels = Board.ClusterLabels;

    }

    public void writeOutputToFile(String move) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter("output.txt", "UTF-8");
            

         writer.write(move);
            for (int i = 0; i < N; i++) {

                for (int j = 0; j < N; j++) {

                    writer.write(this.Cells[i][j].Fruit);

                }
                writer.write("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public void Print() {

        for (int i = 0; i < N; i++) {

            for (int j = 0; j < N; j++) {

                System.out.print(this.Cells[i][j].Fruit);

            }
            System.out.print("\n");
        }

    }

    public void Gravity(int label) {
        HashSet<Tuple> Cluster = ClusterLabels.get(label);
        HashSet<Integer> Columns = new HashSet<>();
        if (Cluster == null) {
            return;
        }
        for (Tuple t : Cluster) {
            Cells[t.i][t.j].Fruit = '*';
            Columns.add(t.j);
        }
        char shift[] = new char[N];
        Arrays.fill(shift, '*');
        for (Integer i : Columns) {
            Arrays.fill(shift, '*');
            int l = N - 1;
            for (int x = N - 1; x >= 0; x--) {
                if (Cells[x][i].Fruit != '*') {
                    shift[l] = Cells[x][i].Fruit;
                    l--;
                }
            }
            for (int x = 0; x < N; x++) { //System.out.print(shift[x]+" ");
                Cells[x][i].Fruit = shift[x];
            }

        }

        this.score = Cluster.size() * Cluster.size();
    }

    public void insertInMoves(int i, HashSet<Tuple> cluster, HashMap<Integer, HashSet<Tuple>> MovesSet) {
        int t = 0;
        //System.out.println("No of nodes in Pq"+Math.sqrt(N)*this.depth);
        int a=N;
        if(N<10)
            a=10;
        if (MovesSet.size() <= a*this.depth) {

            MovesSet.put(i, cluster);
            Size.put(i, cluster.size());

        } else {
            int min = Collections.min(Size.values());
            if (min < cluster.size()) {
                for (int x : MovesSet.keySet()) {
                    if (Size.get(x) == min) {
                        {
                            MovesSet.put(i, cluster);
                            Size.put(i, cluster.size());
                            t = x;
                            break;
                        }
                    }

                }
            }
            Size.remove(t);
            MovesSet.remove(t);

        }

    }

    public ArrayList<Integer> Compute() {

        int labelNumber = 0;
        ClusterLabels = new HashMap<>();
        Size = new HashMap<>();
        GameOver=true;
        HashMap<Integer, HashSet<Tuple>> MovesSet = new HashMap<>();
        ArrayList<Integer> Moves = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {

                if (Cells[i][j].Fruit == '*') {
                    continue;
                }
                GameOver=false;
                if (i > 0 && j > 0 && Cells[i][j].Fruit == Cells[i][j - 1].Fruit && Cells[i - 1][j].Fruit == Cells[i][j].Fruit) {
                    if (Cells[i - 1][j].cluster != -1) {
                        if (Cells[i][j - 1].cluster != -1) {
                            Cells[i][j].cluster = Cells[i - 1][j].cluster;
                            ClusterLabels.get(Cells[i - 1][j].cluster).add(new Tuple(i, j));

                            if (Cells[i - 1][j].cluster != Cells[i][j - 1].cluster) {
                                //System.out.println("Merge Clusters ");
                                int temp = Cells[i][j - 1].cluster;
                                ClusterLabels.get(Cells[i - 1][j].cluster).addAll(ClusterLabels.get(Cells[i][j - 1].cluster));
                                for (Tuple t : ClusterLabels.get(Cells[i][j - 1].cluster)) {
                                    //System.out.println("Tuple " + t.i + " " + t.j);
                                    Tuple b = new Tuple(t.i, t.j);
                                    //if(!b.contains(ClusterLabels.get(Cells[i-1][j].cluster), t))

                                    Cells[t.i][t.j].cluster = Cells[i - 1][j].cluster;
                                    //ClusterLabels.get(Cells[i-1][j].cluster).add(new Tuple(t.i,t.j));
                                }

                                ClusterLabels.remove(temp);

                                {
                                    MovesSet.remove(temp);
                                    Size.remove(temp);
                                }

                            }
                        } else {

                            ClusterLabels.get(Cells[i - 1][j].cluster).add(new Tuple(i, j));
                            ClusterLabels.get(Cells[i - 1][j].cluster).add(new Tuple(i, j - 1));
                            Cells[i][j].cluster = Cells[i - 1][j].cluster;
                            Cells[i][j - 1].cluster = Cells[i - 1][j].cluster;

                        }
                    } else {
                        if (Cells[i - 1][j - 1].cluster == -1) {
                            System.out.println(i + " " + j + " " + Cells[i][j].Fruit + " Both dont have a cluster Creating new label " + labelNumber + 1);
                            ClusterLabels.put(labelNumber, new HashSet<>());
                            ClusterLabels.get(labelNumber).add(new Tuple(i, j));
                            ClusterLabels.get(labelNumber).add(new Tuple(i - 1, j));
                            ClusterLabels.get(labelNumber).add(new Tuple(i, j - 1));
                            Cells[i][j].cluster = labelNumber;
                            Cells[i - 1][j].cluster = labelNumber;
                            Cells[i][j - 1].cluster = labelNumber;
                            labelNumber++;
                        } else {
                            ClusterLabels.get(Cells[i][j - 1].cluster).add(new Tuple(i, j));
                            ClusterLabels.get(Cells[i][j - 1].cluster).add(new Tuple(i - 1, j));

                            Cells[i][j].cluster = Cells[i][j - 1].cluster;

                            Cells[i - 1][j].cluster = Cells[i][j - 1].cluster;
                        }
                    }
                } else if (i > 0 && Cells[i - 1][j].Fruit == Cells[i][j].Fruit) {
                    ClusterLabels.get(Cells[i - 1][j].cluster).add(new Tuple(i, j));
                    Cells[i][j].cluster = Cells[i - 1][j].cluster;
                } else if (j > 0 && Cells[i][j - 1].Fruit == Cells[i][j].Fruit) {
                    ClusterLabels.get(Cells[i][j - 1].cluster).add(new Tuple(i, j));
                    Cells[i][j].cluster = Cells[i][j - 1].cluster;
                } else {
                    ClusterLabels.put(labelNumber, new HashSet<>());
                    ClusterLabels.get(labelNumber).add(new Tuple(i, j));
                    Cells[i][j].cluster = labelNumber;
                    labelNumber++;
                }
                insertInMoves(Cells[i][j].cluster, ClusterLabels.get(Cells[i][j].cluster), MovesSet);

            }
        }
        int i = 0;
        while (!MovesSet.isEmpty()) {
            int max = Collections.max(Size.values());
            int t = 0;
            for (int x : MovesSet.keySet()) {
                if (Size.get(x) == max) {
                    Moves.add(x);

                    t = x;
                    break;
                }
            }

            MovesSet.remove(t);
            Size.remove(t);
        }

        return Moves;
    }
}

class AlphaBeta {

    static int depth;
    static long startTime;
    static final long NANOSEC_PER_SEC = 1000l * 1000 * 1000;
    FruitRage fr;
    static Tuple BestIndex = new Tuple();
    static final char[] columnName = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    long counter = 0;

    public AlphaBeta(String input) {
        fr = new FruitRage(input);

        //Call alphaBeta and writeToFile
    }
     void SetBranchingFactor(){
        
        if(FruitRage.time<1.)
        { FruitRage.B=(int) Math.ceil(Math.pow(FruitRage.N, 0.67));
        if(FruitRage.N<=5)
        AlphaBeta.depth=3;
        else
            AlphaBeta.depth=2;
        return;
        }
        else if(FruitRage.N>=10 &&FruitRage.time<2)
        {
            FruitRage.B=(int) Math.ceil(Math.pow(FruitRage.N, 0.67));
            AlphaBeta.depth=4;
        }
          else  if(FruitRage.time>20)
            {   if(FruitRage.N<=5)
            {
                AlphaBeta.depth=6;
                FruitRage.B=FruitRage.N*2;
            }
            else {
                AlphaBeta.depth= 4;
                FruitRage.B=(int) Math.ceil(Math.pow(FruitRage.N, 0.67));
            }
            
            }
            else {
              
                FruitRage.B=FruitRage.N*2;
                if(FruitRage.N<=5)
                AlphaBeta.depth=5;
                else
                    AlphaBeta.depth=4;
            }
    }
    public FruitRage alphaBetaAlg(FruitRage fr) {

        int d = 0;
        //Tuple bestIndex=new Tuple();

        //int highestScore = Integer.MIN_VALUE;
        FruitRage alpha = new FruitRage();
        alpha.score = Integer.MIN_VALUE;
        FruitRage beta = new FruitRage();
        beta.score = Integer.MAX_VALUE;
        ArrayList<Integer> Moves;
        FruitRage best = new FruitRage(fr);
        
        best.score = Integer.MIN_VALUE;
        fr.depth=1;
        Moves = fr.Compute();
        int t_i = 0,t_j=-1,moveLabel = 0;
        //Tuple t_max = new Tuple();
        //System.out.println(Moves.values());
        if (fr.ClusterLabels.isEmpty()) {
            return fr;
        }
        //int score = Integer.MIN_VALUE;
        
        for (int x = 0; x < Moves.size(); x++) {
          
            FruitRage newBoard = new FruitRage(fr);
            newBoard.depth=fr.depth+1;
            newBoard.Gravity(Moves.get(x));
            //System.out.println("New First Move "+x);
            //newBoard.Print();
            //newBoard.score=(FruitRage.N*FruitRage.N)-newBoard.score;
            //System.out.println(" New board score " + newBoard.score);
            FruitRage val = minAB(newBoard, d + 1, alpha, beta);
            //fr.StaticScore=newBoard.score-tx.score;
            
            //System.out.println("Back to New First Move "+x);
            
            //newBoard.Print();
            //System.out.println(" Val score "+val.score);
            //newBoard.score -= val.score;
            //System.out.println(" New bOard score " + newBoard.score);
            if (val.score >= best.score) {
               // System.out.println("Val is greater than best. val:"+val.score+" best:"+best.score);
                best = val;
                best.Cells=newBoard.Cells;
                moveLabel=Moves.get(x);
                //System.out.println("After assignment. Val is greater than best. val:"+val.score+" best:"+best.score);

            }
            //newBoard.Print();

        }
        HashSet<Tuple> c=fr.ClusterLabels.get(moveLabel);
        //System.out.println(c.toString());
        
        for(Tuple x:c)
        {   
            
             {t_i=x.i;
            t_j=x.j; }
            
        }
        //best.Print();
        //System.out.println(best.score);
        //System.out.println(t_i+" "+t_j+" "+columnName[t_j]+String.valueOf(t_i+1));
        best.writeOutputToFile(columnName[t_j]+String.valueOf(t_i+1) + "\n");
        return best;
    }

    public FruitRage minAB(FruitRage move, int limit, FruitRage alpha,
            FruitRage beta) {
        FruitRage best = new FruitRage(move);
            
            best.score = Integer.MAX_VALUE;
        if (limit >= AlphaBeta.depth || move.ClusterLabels.isEmpty()) {
            //	move.score 
            //System.out.println("Over- min?" + limit);
            //move.score*=-1;
            //best.score=0;
            return move;
        } else {
            int moveLabel = 0,t_i = 0,t_j = 0;
            

            ArrayList<Integer> Moves = move.Compute();
            if (move.ClusterLabels.isEmpty()) {
                //best.score=0;
                //move.score*=-1;
                return move;
            }
            //int bestScore=Integer.MIN_VALUE;
            //int HighestScore=Integer.MIN_VALUE;
            for (int x = 0; x < Moves.size(); x++) {

                FruitRage newBoard = new FruitRage(move);
                newBoard.depth=move.depth+1;
                newBoard.Gravity(Moves.get(x));
                newBoard.score=move.score-newBoard.score;
                
                //newBoard.Print();
                //System.out.println("In min at depth "+limit);
                //System.out.println("min New board score " + newBoard.score);
                //newBoard.Print();
                FruitRage val = maxAB(newBoard, limit + 1, alpha, beta);
                //fr.StaticScore=newBoard.score-tx.score;
                //System.out.println("Back to min at depth "+limit);
                //newBoard.Print();
                
                //newBoard.Print();
                //System.out.println(" Val score "+val.score);
                //newBoard.score += val.score;
                //System.out.println("min New board score " + newBoard.score);
               
                if (val.score<=best.score) {
                   //System.out.println("min of best and newboard is newboard " + best.score + " " + val.score);
                    best = val;
                    best.Cells=newBoard.Cells;
                    //bestScore=val.score;
                    best.StaticScore=newBoard.score;
                    moveLabel=x;
                }
                if (beta.score >= best.score) {
                    //System.out.println("min of best and beta is best " + best.score + " " + beta.score);
                    beta=best;

                }
                if (beta.score <= alpha.score) {
                    //System.out.println("Prune");
                    best.score=best.StaticScore;
                    return best;
                }

            }
            //System.out.println("Min Print");
            HashSet<Tuple> c=fr.ClusterLabels.get(moveLabel);
        if(c!=null)   
        {for(Tuple x:c)
        {
            t_i=x.i;
            t_j=x.j;
            break;
        }}
       // System.out.println("Move "+t_i+" "+t_j);
         //   best.Print();
            best.score=best.StaticScore;
            return best;
        }
    }

    public FruitRage maxAB(FruitRage move, int limit, FruitRage alpha,
            FruitRage beta) {
         FruitRage best = new FruitRage(move);
            best.score = Integer.MIN_VALUE;
        if (limit >= AlphaBeta.depth || move.ClusterLabels.isEmpty()) {
            //	move.score 
            //System.out.println("Over-max?" + limit);
            //best.score=0;
            //move.score*=-1;
            return move;
        } else {
            int moveLabel = 0,t_i = 0,t_j = 0;
           

            ArrayList<Integer> Moves = move.Compute();
            if (move.ClusterLabels.isEmpty()) {
                //best.score=0;
                //move.score*=-1;
                return move;
            }
            for (int x = 0; x < Moves.size(); x++) {

                FruitRage newBoard = new FruitRage(move);
                newBoard.depth=move.depth+1;
                newBoard.Gravity(Moves.get(x));
                
                //System.out.println("In max at depth "+limit);
                newBoard.score=move.score-newBoard.score;
                FruitRage val = minAB(newBoard, limit + 1, alpha, beta);
                //fr.StaticScore=newBoard.score-tx.score;
                //System.out.println("Back to max at depth "+limit);
                //move.Print();
              //System.out.println(" max New board score " + newBoard.score);
             // newBoard.Print();
              //System.out.println(" Val score "+val.score);
            //newBoard.score += val.score;
            //System.out.println(" max New bOard score " + newBoard.score);
                
                
                //newBoard.Print();
                if (best.score <= val.score) {
                    //System.out.println("max of best and newboard is newboard " + best.score + " " + val.score);
                    best = val;
                    best.StaticScore=newBoard.score;
                    best.Cells=newBoard.Cells;
                    moveLabel=x;
                }
                if (best.score >= alpha.score) {
                    //System.out.println("max of best and alpha is best " + best.score + " " + alpha.score);
                    alpha = best;

                }
                if (beta.score <= alpha.score) {
                    //System.out.println("Prune");
                    best.score=best.StaticScore;
                    
                    return best;
                }

            }
            
            //System.out.println("Max Print");
            HashSet<Tuple> c=fr.ClusterLabels.get(moveLabel);
        if(c!=null)   
        {for(Tuple x:c)
        {
            t_i=x.i;
            t_j=x.j;
            break;
        }}
        //System.out.println("Move "+t_i+" "+t_j);
           // best.Print();
            best.score=best.StaticScore;
            return best;
        }
    }

}

public class homework {

    public static void main(String[] args) {
        String input = "./input.txt";
        AlphaBeta.startTime=System.nanoTime();
        AlphaBeta Game = new AlphaBeta(input);
        Game.SetBranchingFactor();
        //AlphaBeta.depth=5;
        Game.fr = Game.alphaBetaAlg(Game.fr);

    }

}
