
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class homework {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        InferenceEngine E = new InferenceEngine();
        ReadFromFile(E, "input.txt");
        WriteToFile(E,"output.txt");
    }

    static void ReadFromFile(InferenceEngine E, String file) {
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new FileReader(file));
            E.NQ = Integer.parseInt(buffer.readLine().trim());
            E.queries = new String[E.NQ];
            //System.out.println(E.NQ);
            for (int i = 0; i < E.NQ; i++) {   //Cells[i]=new Cell[N];
                E.queries[i] = buffer.readLine();
                //System.out.println(E.queries[i]);

            }
            E.NS = Integer.parseInt(buffer.readLine().trim());
            E.sentences = new ArrayList<String>();
            //System.out.println(E.NS);
            for (int i = 0; i < E.NS; i++) {  
                E.insertIntoKB(buffer.readLine(), i + 1);
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

    static void WriteToFile(InferenceEngine E, String file) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(file, "UTF-8");
             for (int i = 0; i < E.NQ; i++) {
                Boolean x = (E.isQueryTrue(E.queries[i]));
                writer.write(x.toString().toUpperCase()+"\n");
                if (x) {
                    E.insertIntoKB(E.queries[i], E.SentenceKB.size() + 1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

    }

}

class Predicate {

    Boolean Negative;
    String PredName;
    String[] Arguments;

}

class InferenceEngine {

    int NS, NQ;
    String[] queries;
    ArrayList<String> sentences;
    HashMap<Integer, HashSet<Predicate>> SentenceKB;
    HashMap<String, HashSet<Integer>> NegPredMap;
    HashMap<String, HashSet<Integer>> PosPredMap;
    HashMap<String,HashSet<Predicate>> NegClauses;
    HashMap<String,HashSet<Predicate>> PosClauses;
    ArrayList<String> Clauses, Resolvent;
    
    public void FormSentence(LinkedList<Predicate> sentence, HashMap<String,String> Map) {
        String res="";
        if(sentence.isEmpty())
            return;
        for(Predicate x:sentence)
        {
            if(x.Negative)
            { res+="~";
            }
            res+=x.PredName+"(";
                for(String s:x.Arguments)
                {
                    if(Map.containsKey(s))
                        res+=Map.get(s)+",";
                    else res+=s+",";
                }
                res=res.substring(0,res.length()-1)+") | "; 
        }
        res=res.substring(0, res.length()-3);
        //System.out.println("Resolvent "+res);
        Resolvent.add(res);
        
    }

    public InferenceEngine() {
        SentenceKB = new HashMap<>();
        NegPredMap = new HashMap<>();
        PosPredMap = new HashMap<>();

    }

    public HashMap<String, String> MakeCopy(HashMap<String, String> X) {
        HashMap<String, String> Y=new HashMap<>();
        
        if (X.size() == 0) {
            return Y;
        }

        for (String S : X.keySet()) {
            Y.put(S, X.get(S));
        }
        return Y;
    }

    public void MakeSetCopy(LinkedList<Predicate> X, LinkedList<Predicate> Y) {
        Y.clear();
        if (X.size() == 0) {
            return;
        }
        for (Predicate S : X) {
            Y.add(S);
        }
    }

    public String negate(String q) {   
        if (q.charAt(0) == '~') {
            return q.substring(1);
        } else {
            return "~" + q;
        }

    }

    public void StandardizeVariables(String[] args, int sentenceNum) {
        for (int i = 0; i < args.length; i++) {
            char x = args[i].charAt(0);
            if (x >= 'A' && x <= 'Z') {
                continue;
            } else {
                args[i] = args[i].concat(Integer.toString(sentenceNum));
            }
        }
    }

    public void insertIntoKB(String S, int i) {   
        String[] split = S.split("\\|");
        HashSet<Predicate> t = new HashSet<>();
        for (String s : split) {
            s = s.trim();
            //System.out.println(s);
            Predicate e = new Predicate();
            int ind = s.indexOf('(');
            // System.out.print(ind);
            if (s.charAt(0) == '~') {
                e.Negative = true;
                e.PredName = s.substring(1, ind);

            } else {
                e.Negative = false;
                e.PredName = s.substring(0, ind);
            }
            e.Arguments = s.substring(ind + 1, s.length() - 1).split(",");
            StandardizeVariables(e.Arguments, i);
           // e.numArgs = e.Arguments.length;

            t.add(e);
            if (e.Negative) {
                if (NegPredMap.containsKey(e.PredName)) {
                    NegPredMap.get(e.PredName).add(i);
                } else {
                    NegPredMap.put(e.PredName, new HashSet<Integer>());
                    NegPredMap.get(e.PredName).add(i);

                }
            } else {
                if (PosPredMap.containsKey(e.PredName)) {
                    PosPredMap.get(e.PredName).add(i);
                } else {
                    PosPredMap.put(e.PredName, new HashSet<Integer>());
                    PosPredMap.get(e.PredName).add(i);

                }
            }

        }
        
        SentenceKB.put(i, t);
    }

    public Boolean Unify(Predicate a, Predicate b, HashMap<String, String> SubMap, Boolean[] ConstantArg) {
        //System.out.println("Trying to Unify " + a.PredName + " " + a.Arguments[0] + "," + b.PredName + " " + b.Arguments[0]);
        int z = 0;
        int AConsts = 0, BConsts = 0;
        for (int i = 0; i < a.Arguments.length; i++) {
            char firstLetterA = a.Arguments[i].charAt(0);
            char firstLetterB = b.Arguments[i].charAt(0);
            ConstantArg[0] = false;

            if (firstLetterA >= 'A' && firstLetterA <= 'Z') {
                AConsts++;
                if (firstLetterB >= 'A' && firstLetterB <= 'Z') {
                    BConsts++;
                    if (a.Arguments[i].compareTo(b.Arguments[i]) == 0) {
                        z++;
                        continue;
                    } else {
                        return false;
                    }
                } else { //First is constant, second is var
                    if (SubMap.containsKey(b.Arguments[i])) { //If var already has a mapping, check if mapping =constant or to some other variable

                        String x = (SubMap.get(b.Arguments[i]));
                        if (x.charAt(0) >= 'A' && x.charAt(0) <= 'Z') // if a is mapped to constant
                        {
                            if (SubMap.get(b.Arguments[i]).compareTo(a.Arguments[i]) != 0) {
                                return false;
                            }
                        } else {
                            SubMap.put(x, a.Arguments[i]);
                            SubMap.put(b.Arguments[i], a.Arguments[i]);
                        }
                    } else //put mapping var of b,constant in a
                    {
                        SubMap.put(b.Arguments[i], a.Arguments[i]);
                    }
                }
            } else if (firstLetterB >= 'A' && firstLetterB <= 'Z') {
                BConsts++;
                //if B is constant, a is var
                if (SubMap.containsKey(a.Arguments[i])) //if a has a mapping
                {
                    String x = (SubMap.get(a.Arguments[i]));
                    if (x.charAt(0) >= 'A' && x.charAt(0) <= 'Z') // if a is mapped to constant
                    {
                        if (SubMap.get(a.Arguments[i]).compareTo(b.Arguments[i]) != 0) {
                            return false;
                        }
                    } else {
                        SubMap.put(x, b.Arguments[i]);
                        SubMap.put(a.Arguments[i], b.Arguments[i]);
                    }

                } else {
                    SubMap.put(a.Arguments[i], b.Arguments[i]);
                }

            } else {

                if (SubMap.containsKey(a.Arguments[i])) {
                    if (SubMap.containsKey(b.Arguments[i])) {
                        if (SubMap.get(a.Arguments[i]).compareTo(SubMap.get(b.Arguments[i])) != 0) {
                            return false;
                        }
                    } else {
                        SubMap.put(b.Arguments[i], SubMap.get(a.Arguments[i]));
                    }
                } else if (SubMap.containsKey(b.Arguments[i])) {

                    SubMap.put(a.Arguments[i], SubMap.get(b.Arguments[i]));
                }
                else SubMap.put(a.Arguments[i], b.Arguments[i]);
            }

        }
        //System.out.println(SubMap.toString());
        if (AConsts == a.Arguments.length || BConsts == b.Arguments.length) {
            ConstantArg[0] = true;
        }
        return true;
        //return "*";

    }

    public Boolean Resolution(LinkedList<Predicate> queries, HashMap<String, String> Map) {

        if (queries.isEmpty()) {
            return true;
        }
        /*if (LoopCounter > SentenceKB.size() * 5) {
            return false;
        }*/
        
        // if queries is a subset of 
        HashMap<String, String> subs;
        for(Predicate q:queries)
        { subs=MakeCopy(Map);
        Boolean[] C = new Boolean[1];
                    C[0] = false;
            if(q.Negative)
            {
                if(PosClauses.containsKey(q.PredName))
                {
                    for(Predicate u:PosClauses.get(q.PredName))
                    {
                        if(Unify(q, u, subs, C))
                        {
                            queries.remove(q);
                            queries.remove(u);
                            PosClauses.get(u.PredName).remove(u);
                            Map=MakeCopy(subs);
                            break;
                        }
                    }
                }
                    
            }
            else 
            {
                if(NegClauses.containsKey(q.PredName))
                {
                    for(Predicate u:NegClauses.get(q.PredName))
                    {
                        if(Unify(q, u, subs, C))
                        {
                            queries.remove(q);
                            queries.remove(u);
                            NegClauses.get(u.PredName).remove(u);
                            Map=MakeCopy(subs);
                            break;
                        }
                    }
                }
            }
        }
        if (queries.isEmpty()) {
            return true;
        }
        FormSentence(queries,Map);
        if(Clauses.containsAll(Resolvent))
        {   System.out.println("Subset");
        
        
            return false;
        }
       
        Clauses.addAll(Resolvent);
        System.out.println(Resolvent.toString());
        HashSet<Integer> SentenceNumbers;
        LinkedList<Predicate> LocalSet = new LinkedList<>();
        
        Predicate query = queries.get(0);
        
        //System.out.println(query.PredName);
        if (query.Negative) {
            SentenceNumbers = PosPredMap.get(query.PredName);
            //System.out.println(PosPredMap.containsKey(query.PredName));

        } else {

            SentenceNumbers = NegPredMap.get(query.PredName);
            //System.out.println(PosPredMap.containsKey(query.PredName));
        }
        Boolean flag;
        if (SentenceNumbers == null) {
            //System.out.println("Empty"+query.PredName+" "+LoopCounter);
            return false;
        }
        
        //System.out.println(SentenceNumbers.toString());

        for (Integer i : SentenceNumbers) {
            subs=MakeCopy(Map);
            MakeSetCopy(queries, LocalSet);
            HashSet<Predicate> line = SentenceKB.get(i);
           //System.out.println("sen num " + i + " " + line.toString() );
                 //       System.out.println(subs.toString() + " Parent " + Map.toString());

            flag = false;

            for (Predicate e : line) { 
                if (e.PredName.equals(query.PredName) && flag == false&&e.Negative!=query.Negative) {
                    //MakeCopy(subs, SubM);
                    Boolean[] C = new Boolean[1];
                    C[0] = false;
                    if (Unify(query, e, subs, C)) {

                      /*  if (isSubset(subs, Map) && C[0] == false) {
                            System.out.println("No inference made " + C[0]);
                            flag = false;
                            subs=MakeCopy(Map);
                                        System.out.println(subs.toString() + " Parent " + Map.toString());

                            continue;
                        } else*/ {  //System.out.println("Unified " + query.PredName);
                            //System.out.println(subs.toString() + " Parent " + Map.toString());

                            flag = true;
                            LocalSet.remove(query);
                            
                        }

                    }

                } else {
                    if(e.Negative)
                    {
                        if(NegClauses.containsKey(e.PredName))
                            NegClauses.get(e.PredName).add(e);
                        else{
                            NegClauses.put(e.PredName, new HashSet<>());
                            NegClauses.get(e.PredName).add(e);
                        }
                    }
                    else
                    {
                        if(PosClauses.containsKey(e.PredName))
                            PosClauses.get(e.PredName).add(e);
                        else{
                            PosClauses.put(e.PredName, new HashSet<>());
                            PosClauses.get(e.PredName).add(e);
                        }
                    }
                    
                    LocalSet.add(e);
                }

            }
           // System.out.println("Calling Resolution on " + LocalSet.toString());
            if (flag == false) {
               
                continue;
            }
            if (Resolution(LocalSet, subs)) {
                //System.out.println(subs.toString() + " Parent " + Map.toString());
                return true;
            } 
        }
   
        return false;

    }

    public Boolean isQueryTrue(String Q) {
        
        String Q1 = negate(Q);
        Predicate query = new Predicate();
        Clauses=new ArrayList<>();
        Clauses.addAll(sentences);
        Resolvent=new ArrayList<>();
        
        int ind = Q1.indexOf('(');
        if (Q1.charAt(0) == '~') {
            query.PredName = Q1.substring(1, ind);
            query.Negative = true;
        } else {
            query.PredName = Q1.substring(0, ind);
            query.Negative = false;
        }
        PosClauses=new HashMap<>();
        NegClauses=new HashMap<>();
       // System.out.println("New Query");
        query.Arguments = Q1.substring(ind + 1, Q1.length() - 1).split(",");
        HashMap<String, String> Substitution = new HashMap<>();
        LinkedList<Predicate> List = new LinkedList<>();
        List.add(query);
        return Resolution(List, Substitution);
    }

}
