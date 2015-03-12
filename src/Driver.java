import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by minchen on 15/3/7.
 */
public class Driver {
    public static void main(String[] args) throws IOException {
        int args_i_index = -1;
        int args_d_index = -1;
        int args_q_index = -1;
        int args_s_index = -1;

        int i=0;
        for (String s: args) {
            switch (s) {
                case "-i":
                    args_i_index = i;
                    break;
                case "-d":
                    args_d_index = i;
                    break;
                case "-q":
                    args_q_index = i;
                    break;
                case "-s":
                    args_s_index = i;
                    break;
                default:
                    break;
            }
            i++;
        }

        String pathInFiles = "./";
        if (args_d_index != -1) {
            if (args.length > args_d_index + 1 && !args[args_d_index+1].equals("-i") && !args[args_d_index+1].equals("-s") && !args[args_d_index+1].equals("-q")) {
                pathInFiles = args[args_d_index+1];
            }
            else {
                System.out.println("-d argument input error");
                return ;
            }
        }
        else {
            System.out.println("-d argument input error");
            return ;
        }

        String pathOutIndex = "./index.txt";
        if (args_i_index != -1) {
            if (args.length <= args_i_index + 1 || args[args_i_index+1].equals("-d") || args[args_i_index+1].equals("-q") || args[args_i_index+1].equals("-s")) {
                pathOutIndex = "./index.txt";
            }
            else if (args.length > args_i_index + 1) {
                pathOutIndex = args[args_i_index+1];
            }
        }

        String pathOutQuery = "./search.txt";
        if (args_s_index != -1) {
            if (args.length <= args_s_index + 1 || args[args_s_index+1].equals("-d") || args[args_s_index+1].equals("-q") || args[args_s_index+1].equals("-i")) {
                pathOutQuery = "./search.txt";
            }
            else if (args.length > args_s_index + 1) {
                pathOutQuery = args[args_s_index+1];
            }
        }

        String pathInQuery = "./search.txt";
        if (args_q_index != -1) {
            if (args.length <= args_q_index + 1 || args[args_q_index+1].equals("-d") || args[args_q_index+1].equals("-s") || args[args_q_index+1].equals("-i")) {
                System.out.println("-q argument input error");
                return ;
            }
            else if (args.length > args_q_index + 1) {
                pathInQuery = args[args_q_index+1];
            }
        }

        MultithreadedInvertedIndex ii = new MultithreadedInvertedIndex(pathInFiles);

        MultithreadedSearchQuery sq = null;
        if (args_q_index != -1) {
            sq = new MultithreadedSearchQuery(ii.getWholeMap(), pathInQuery);
        }

        if (args_i_index != -1) ii.outPutIndex(pathOutIndex);
        if (args_s_index != -1) sq.outPutQuerys(pathOutQuery);

        ii.shutdown();
        sq.shutdown();
    }
}
