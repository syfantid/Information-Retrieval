package queries;

import java.util.ArrayList;

/**
 * Implements a query. It consists of its id,numbers of returned answers and the search terms
 */
public class Query {
    int id;
    int top_k;
    ArrayList<String> terms;

    /**
     * Constructs the query
     * @param id the ID of the query as given in the file
     * @param top_k the number of returned answers (top-k answers)
     * @param terms the seach terms of the query
     */
    public Query(int id, int top_k, ArrayList<String> terms) {
        this.id = id;
        this.top_k = top_k;
        this.terms = terms;
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getTerms() {
        return terms;
    }

    public int getTop_k() {
        return top_k;
    }


}

