import java.io.*;
import java.util.*;
//import java.lang.*;
public class textQuery {

  /******************************************************************
 
   Name: Caleb Cox
   Course/Section:       COSC 4315.001
   Instructor:           Dr. Brown
   Program Description:  
   
   This program reads from a file directory of text files and lists all the words. It then keeps
   track of any words said and if they have been read before using an arraylist. the words are processed by
   converting all letters to lowercase and removing end word punctuation, as well as some stemming.
   The program then creates a permutaitons object for each new word, and stores the origional word in the object.
   Upon receiving the query term, the program then process the query term depending if it is a wildcard or normal query
   and checks the database of words to see if the query term is contained. For each entry that is return, the TF-IDF score is
   calculated and then the files are ranked from highest to lowest importance.
 
   This program uses the following approach for each issue
   Case: Converting all to lowercase
   Punctuation: Removing all puncuation other than + or - at then end of a word
   Stop Words: 5 basic stop words are kept from being output
   Stemming: removing "s"(unless the words is less than 2 characters), "ing", and "ed" at the end of words
 ******************************************************************/
    public static void main (String[] args) throws Exception
    {
      IRSystem ir = new IRSystem();
      ir.start();
    }
  }
  /**Class permutations
   * Description: Creates an object that houses the referenced word
   * as well as a list of it's permutations
   * name - Origional Word
   * perms - ArrayList of all permutations
   */
  
  class permutations
  {
    public String name;
    public ArrayList<String> perms;
    public permutations(){
      name = "";
      perms = new ArrayList<String>();
    }

    public void setName(String n){
      name = n;
    }

    public String getName(){
      return name;
    }

    public void permAdd(String n){
      perms.add(n);
    }

    public String getPerm(int x){
      return perms.get(x);
    }

    public int getPermSize(){
      return perms.size();
    }

    public boolean permSearch(String n){
      return perms.contains(n);
    }

    public void makePerm(String n){
      n += "$";
      for(int i = 0; i<n.length()-1;i++)
      {
        perms.add(n);
        n = n.substring(1,n.length()) + n.substring(0,1);
      }
    }
  }
/**InvertedIndex Class
 * This class holds the data for the:
 * frequency- Document Frequency
 * posting- Word's postings
 * 
 */
  class InvertedIndex
  {
    public Double frequency;
    public ArrayList<String> posting; //Document ID
    public ArrayList<Double>docFreq; //Term Frequency
    public InvertedIndex(){
      frequency = 0.0;
      posting = new ArrayList<String>();
      docFreq = new ArrayList<Double>();
    }

    public void setFrequency(double i){
      frequency = i;
    }

    public void addFrequency(){
      frequency += 1.0;
    }

    public void addPosting(String i){
      posting.add(i);
    }

    public void removePosting(String i){
      posting.remove(posting.indexOf(i));
    }

    public int getPostIndex(String i){
      return posting.indexOf(i);
    }

    public String getPost(int i){
      return posting.get(i);
    }

    public void addDocFreq(double i){
      docFreq.add(i);
    }

    public Integer getDocFreq(double post){
      return docFreq.indexOf(post);
    }

    public double retrieveDocFreq(int i){
      return docFreq.get(i);
    }

    public void updateDocFreq(int post, double i){
      docFreq.set(post, i);
    }

    public void updateDocFreqNew(double post){
      docFreq.add(post);
    }

    public boolean containsPost(String i){
      return posting.contains(i);
    }
  }


  class IRSystem
  {
    private File[] collection;
    ArrayList<String> db = new ArrayList<String>();
  
    public IRSystem(){
      collection = getFiles();
    }
    
    public File[] getFiles()
    {
      File[] files = null;
      try
      {
        System.out.println();
        System.out.print("Enter name of a directory> ");
        Scanner scan = new Scanner(System.in);
        File dir = new File(scan.nextLine());
        files = dir.listFiles();
        System.out.println();
      }
      
      catch (Exception e)
      {
        System.out.println("Caught error in getFiles: " + e.toString());
      }
      return files;
    }
  
  /* Processing: 
    INPUT: String w = next word in file
    OUTPUT: 
    End of word punctuation(Will repeat till all punctuation at end is gone)
    Stemming: Suffixes: s, ed, and ing
    Case: Convert all to Lowercase
      */
    public String process(String w)
    {
      int k = 0;
      while(k != 1){
      switch(w.substring(w.length()-1,w.length())){ //this switch removes any punctuation at the end of a word
        case "?":
        case ".":
        case "!":
        case ",":
        case ";":
        case "(":
        case ")":
        case ":":
        case "]":
        case "[":
        case "}":
        case "{": w = w.substring(0,w.length()-1);
                  break;
        default: 
        k = 1;
        break;
      }
      }
      if(w.length() > 1){
      k = 0;
      while(k != 1){
        switch(w.substring(0,1)){ //this switch removes any punctuation at the beginning of a word
          case "?":
          case ".":
          case "!":
          case ",":
          case ";":
          case "(":
          case ")":
          case ":":
          case "]":
          case "[":
          case "}":
          case "{": w = w.substring(1,w.length());
                    break;
          default: 
          k = 1;
          break;
        }
        }
      }
        if(w.length() >2 && w.substring(w.length()-1,w.length()).startsWith("s")) //removes the s at the end of a word unless it is shorter than 2 characters(to save words like "is" or "as")
          w = w.substring(0,w.length()-1);
        else if(w.length() > 2 && w.substring(w.length()-2,w.length()).startsWith("ed")) // removes ed at the end of a word
          w = w.substring(0,w.length()-2);
        else if(w.length() > 3 && w.substring(w.length()-3,w.length()).startsWith("ing")) //removes ing at the end of a word
          w = w.substring(0,w.length()-3);
        if(w.length() > 2 && w.substring(w.length()-1, w.length()).startsWith("'"))
          w = w.substring(0,w.length()-1);  
        else if(w.length() > 3 && w.substring(w.length()-2, w.length()-1).startsWith("'"))
          w = w.substring(0,w.length()-2);
        else if(w.length() > 3 && w.substring(w.length()-3, w.length()-2).startsWith("'"))
          w = w.substring(0,w.length()-3);
      
        return w.toLowerCase();
    }
  
    /*I am using an arralylist to keep track of all seen words
      If the words has not been seen, I add it to the databse
      and sort it every 100 new words added          
  
      INPUT: String w = next word in the file
      OUTPUT: Boolean False if not in database, True if else*/
    public boolean seenBefore(String w)
    {
      int see = 0;
      see = db.indexOf(w); // Search for string w in db
      if(see == -1)
      {
        db.add(w);
        return false;
      }
      else
        return true;
    }
  /*Added small amount of sorting for my database so as to keep efficiency up */
  /**The array stopWord houses 5 stop words to check for before sending the word
   * for processing
   */
    public void start()
    {
      int sortIt = 0; //Itterator for sorting the seen database
      ArrayList<String> stopWord = new ArrayList<String>();
      stopWord.add("a");
      stopWord.add("is");
      stopWord.add("of");
      stopWord.add("and");
      stopWord.add("the");
      stopWord.add(".");
      ArrayList<permutations> perm = new ArrayList<permutations>(); //Array of permutation objects
      ArrayList<String> permSort = new ArrayList<String>();//full array of sorted permutations
      HashMap<String, String> permMap = new HashMap<String, String>(); // KEY: Permutation  VALUE: Origional Word
      HashMap<String, InvertedIndex> invertIndex = new HashMap<String, InvertedIndex>(); //KEY: Vocab word  Value: Inverted Index Class object for word
      HashMap<String, Integer> documentTotal = new HashMap<String, Integer>(); //KEY: File name  VALUE: Number of words in file
      try
      {
        Integer totalDocuments = 0;
        for (File f : collection)
        {
          Scanner sc = new Scanner(f);
          totalDocuments++;
          Integer totalWord = 0;
          while (sc.hasNextLine())
          {
            StringTokenizer st = new StringTokenizer(sc.nextLine());
            while (st.hasMoreTokens())
            {
              String inputWord = st.nextToken();
              totalWord++;
              if(stopWord.contains(inputWord) || inputWord.equals("-"))
                continue;
              String outputWord = process(inputWord);
              /**Creates a permutations object and set's the name and makes
               * the permutations.
               * The permutation is then added to an array of permutation objects perm
               */
              if (!seenBefore(outputWord)) 
              {
                permutations r = new permutations();
                r.setName(outputWord);
                r.makePerm(outputWord);
                perm.add(r);
                sortIt++;
                InvertedIndex s = new InvertedIndex();
                s.setFrequency(1.0);
                s.addPosting(f.getName());
                s.addDocFreq(1.0);
                invertIndex.put(outputWord, s);
                if(sortIt == 100) //This sorts the database only after 100 new words have been added
                {
                  sortIt = 0;
                  Collections.sort(db);
                }
              }
              else{
                InvertedIndex l = invertIndex.get(outputWord);
                String s = f.getName();
                if(l.containsPost(s))
                {
                  int docPost = l.getPostIndex(f.getName());
                  l.updateDocFreq(docPost, (l.retrieveDocFreq(docPost)+1.0));
                }
                else
                {
                  l.addFrequency();
                  l.addPosting(f.getName());
                  int docPost = l.getPostIndex(f.getName());
                  l.updateDocFreqNew(docPost); 
                }
              }
            }

          }
          documentTotal.put(f.getName(), totalWord);
        }//---------------------------------------------------------------------------
         /**Retrieves all the permutations from the permutations objects */
        for(int i = 0; i < perm.size();i++)
        {
          permutations r = perm.get(i);

          permSort.addAll(r.perms);
        }
        Collections.sort(permSort);
        String temp = "";
        String orig = "";
        /**For every permutation(Sorted in alphabetical order), the loop
         * looks through the permutations object to find what word is linked
         * with that particular permutation and assigns the permutation and origional
         * word to the permMap index.
         */
        for(int i = 0; i < permSort.size(); i++)
        {
          temp = permSort.get(i);
          for(int k = 0; k < perm.size(); k++)
          {
           permutations r = perm.get(k);
            if(r.permSearch(permSort.get(i)))
            {
                permMap.put(temp, r.getName());
                break;
            }
          }

        }
        /**Updates every index to have correct document frequency and term frequency */
        for(Map.Entry<String,InvertedIndex> set : invertIndex.entrySet())
        {
          InvertedIndex a = set.getValue();
          a.frequency = a.frequency/totalDocuments;
          for(int i = 0; i < a.posting.size();i++)
          {
            String docName = a.posting.get(i);
            int t = documentTotal.get(docName);
            double v = a.docFreq.get(i);
            a.docFreq.set(i, (v/t));
          }
        }
////////////////////////////////////////////////////////   USER MODE HERE!!!!!!!
/////////////////////////////////////////////////////// All calls to the user made here
      boolean p = true;
      while(p)
      {

       System.out.print("Please enter your query term: ");
       Scanner in = new Scanner(System.in);
       String term = in.nextLine();
       term = term.toLowerCase();
       HashMap<Double,String> TFIDF = new HashMap<Double,String>();//KEY: TFIDF  VALUE: File Name
       ArrayList<String> words = new ArrayList<String>(); //Holds all possible words in query
       if(term.contains("*")) //Wildcard Query
       {

         term += "$";
         int b = term.indexOf("*");
         if(b == 0)
         {

           String k = term.substring(1, term.length()); //shorten later to 1 line
           term = k;
         }
         else if(b < term.length()-1)
         {

           String k = term.substring(0,b);
           term = term.substring(b + 1,term.length()) + k;
         }
         int l = 0;
         if(!permSort.contains(term)) l =1;
         for(int i = 0; i < permSort.size(); i++) //speed up later by getting first index of term that matches and incrementing till the term doesn't match
         {

            if(permSort.get(i).contains(term))
                words.add(permMap.get(permSort.get(i))); //gets all words that fit permutation
         }
         LinkedHashSet<String> duplicates = new LinkedHashSet<>(words); //removes duplicates
         words = new ArrayList<>(duplicates);
          if(words.size() < 5)//Print out the possible words
          {

            System.out.print("Possible Queries: ");
            for(int i = 0; i < words.size();i++)
              System.out.print(words.get(i) + " ");
            System.out.println("");
          }
          InvertedIndex a;
          for(Map.Entry<String,InvertedIndex> set : invertIndex.entrySet())
          {
            if(!words.contains(set.getKey())) //Check the whole Arraylist for each key
              continue;
            else
            {
              a = set.getValue();
              for(int i = 0; i < a.posting.size();i++)
              {
                double TFDF = (a.retrieveDocFreq(i) * Math.log(totalDocuments/a.frequency));
                TFIDF.put(TFDF, a.getPost(i));
              }
            }
          }
          ArrayList<String> repNames = new ArrayList<>(); // This store names to check for repeats
          ArrayList<Double> TFISort = new ArrayList<>(TFIDF.keySet()); //Sorted list of TFIDF
          if(TFIDF.size() == 0)
              System.out.println("Your query term was not found");
          else
          {
            Collections.sort(TFISort);
            for(int i = TFISort.size()-1; i >= 0;i--)
            {
              if(!repNames.contains(TFIDF.get(TFISort.get(i))))
                System.out.println(TFIDF.get(TFISort.get(i)));
              repNames.add(TFIDF.get(TFISort.get(i)));
            }
          }
       }
       else //No Wildcard
       {
         InvertedIndex a;
        for(Map.Entry<String,InvertedIndex> set : invertIndex.entrySet())
        {
          if(!term.equals(set.getKey()))
            continue;
          else
          {
            a = set.getValue();
            for(int i = 0; i < a.posting.size();i++)
            {
              double TFDF = a.retrieveDocFreq(i) * Math.log(totalDocuments/a.frequency);
              TFIDF.put(TFDF, a.getPost(i));
            }
          }
        }
        ArrayList<Double> TFISort = new ArrayList<>(TFIDF.keySet());
        if(TFIDF.size() == 0)
            System.out.println("Your query term was not found");
        else
        {
          Collections.sort(TFISort);
          for(int i = TFISort.size()-1; i >= 0;i--)
          {
            System.out.println(TFIDF.get(TFISort.get(i)));
          }
        }
       }
      boolean b = true;
      while(b)
      {
      System.out.println("------------");
      System.out.println("Would you like to enter another query? y/n");
      term = in.nextLine();
      term = term.toLowerCase();
      if(term.equals("n"))
      {
        p = false;
        b = false;
      }
      else if(term.equals("y"))
      {
        p = true;
        b = false;
      }
      else
        System.out.println("Invalid entry: Please enter the letter 'y' or the letter 'n'");
      }
      }
      
    }
      catch(Exception e)
      {
        System.out.println("Error in start:  " + e.toString());
      }
    }
  }