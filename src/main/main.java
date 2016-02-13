package main;


import inverted_index.ReadFile;
import queries.ReadQueries;

import java.io.File;

public class main {
    public static void main(String[] args) {
        if (args.length<3) {
            System.out.println("Please specify 3 arguments.\n1)Number of threads \n2)Folder path containing the " +
                    "documents \n3)File path to queries' file");
        }
        else {
            File data= new File(args[1]);
            File queries = new File(args[2]);
            int threads = Integer.parseInt(args[0]);
            if (threads<2) {
                System.out.println("Specified threads must be more that 2 or more");
            }
            else if(!data.exists() || !data.isDirectory()) {
                System.out.println("2nd argument must be a path to the directory of the documents");
            }
            else if(!queries.exists() || !queries.isFile()) {
                System.out.println("3rd arguments must be a path to the file of the queries");
            } else {
                String dataPath = args[1];
                String queriesPath = args[2];
                ReadFile.start(threads, dataPath);
                ReadQueries.start(threads, queriesPath);
            }
        }
    }
}
