package queries;

/**
 * Created by sakris on 2/11/2016.
 */
public class Term {

    String word;
    int queryID;


    public Term(int queryID,String word){
        this.word=word;
        this.queryID=queryID;
    }

    public String getWord() {
        return word;
    }

    public int getQueryID() {
        return queryID;
    }
}
