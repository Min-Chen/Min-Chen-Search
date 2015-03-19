import java.io.*;
import static java.lang.Thread.sleep;

/**
 * Created by minchen on 15/3/7.
 */
public class Driver {
    public static void main(String[] args) throws IOException, InterruptedException {
        int args_i_index = -1;
        int args_d_index = -1;
        int args_q_index = -1;
        int args_s_index = -1;
        int args_t_index = -1;
        int args_u_index = -1;

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
                case "-t":
                    args_t_index = i;
                    break;
                case "-u":
                    args_u_index = i;
                    break;
                default:
                    break;
            }
            i++;
        }

        String pathInFiles = "./";
        if (args_d_index != -1) {
            if (args.length > args_d_index + 1 && !args[args_d_index+1].equals("-i") && !args[args_d_index+1].equals("-s") && !args[args_d_index+1].equals("-q") || args[args_d_index+1].equals("-t") || args[args_d_index+1].equals("-u")) {
                pathInFiles = args[args_d_index+1];
            }
            else {
                System.out.println("-d argument input error");
                return ;
            }
        }
        else {
            if (args_u_index == -1) {
                System.out.println("-d argument input error");
                return ;
            }
        }

        String pathOutIndex = "./index.txt";
        if (args_i_index != -1) {
            if (args.length <= args_i_index + 1 || args[args_i_index+1].equals("-d") || args[args_i_index+1].equals("-q") || args[args_i_index+1].equals("-s") || args[args_i_index+1].equals("-t") || args[args_i_index+1].equals("-u")) {
                pathOutIndex = "./index.txt";
            }
            else if (args.length > args_i_index + 1) {
                pathOutIndex = args[args_i_index+1];
            }
        }

        String pathOutQuery = "search.txt";
        if (args_s_index != -1) {
            if (args.length <= args_s_index + 1 || args[args_s_index+1].equals("-d") || args[args_s_index+1].equals("-q") || args[args_s_index+1].equals("-i") || args[args_s_index+1].equals("-t") || args[args_s_index+1].equals("-u")) {
                pathOutQuery = "search.txt";
            }
            else if (args.length > args_s_index + 1) {
                pathOutQuery = args[args_s_index+1];
            }
        }

        String pathInQuery = "./search.txt";
        if (args_q_index != -1) {
            if (args.length <= args_q_index + 1 || args[args_q_index+1].equals("-d") || args[args_q_index+1].equals("-s") || args[args_q_index+1].equals("-i") || args[args_q_index+1].equals("-t") || args[args_q_index+1].equals("-u")) {
                System.out.println("-q argument input error");
                return ;
            }
            else if (args.length > args_q_index + 1) {
                pathInQuery = args[args_q_index+1];
            }
        }

        int threadAmount = 5;
        if (args_t_index != -1) {
            if (args.length <= args_t_index + 1 || args[args_t_index+1].equals("-d") || args[args_t_index+1].equals("-s") || args[args_t_index+1].equals("-i") || args[args_t_index+1].equals("-q") || args[args_t_index+1].equals("-u")) {
                System.out.println("-t argument input error");
                return ;
            }
            else if (args.length > args_t_index + 1) {
                String thread = args[args_t_index+1];
                if (thread.replaceAll("[0-9]","").length()>0) threadAmount = 5;
                else threadAmount = Integer.parseInt(args[args_t_index+1]);
            }
        }
        else {
            threadAmount = 1;
        }

        String seed = "";
        if (args_u_index != -1) {
            if (args.length <= args_u_index + 1 || args[args_u_index+1].equals("-d") || args[args_u_index+1].equals("-s") || args[args_u_index+1].equals("-i") || args[args_u_index+1].equals("-t") || args[args_u_index+1].equals("-q")) {
                System.out.println("-u argument input error");
                return ;
            }
            else if (args.length > args_u_index + 1) {
                seed = args[args_u_index+1];
            }
        }

        File file = new File(pathInFiles);
        if (!file.isDirectory()) {
            System.out.println("No Directory");
            return;
        }

        MultithreadedInvertedIndex ii;
        if (seed.equals("")) {
            ii = new MultithreadedInvertedIndex(pathInFiles,threadAmount);
        }
        else {
            ii = new MultithreadedInvertedIndex(threadAmount, seed);
        }

        while (true) {
            sleep(10);
            if (ii.finished()) break;
        }

        MultithreadedSearchQuery sq = null;
        if (args_q_index != -1) {
            sq = new MultithreadedSearchQuery(ii.getWholeMap(), pathInQuery,threadAmount);

            while (true) {
                sleep(10);
                if (sq.finished()) break;
            }
        }

        if (args_i_index != -1) {
            ii.outPutIndex(pathOutIndex);
        }


        if (args_s_index != -1) {
            file = new File(pathOutQuery);
            if (file.isDirectory()) {
                System.out.println("No write Directory");
                return ;
            }
            sq.outPutQuerys(pathOutQuery);
        }

        ii.shutdown();
        if(args_q_index != -1) sq.shutdown();
    }
}