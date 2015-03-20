import java.io.*;
import static java.lang.Thread.sleep;

/**
 * Created by minchen on 15/3/7.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        int args_i_index = -1;
        int args_d_index = -1;
        int args_q_index = -1;
        int args_s_index = -1;
        int args_t_index = -1;
        int args_u_index = -1;
        int args_p_index = -1;

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
                case "-p":
                    args_p_index = i;
                    break;
                default:
                    break;
            }
            i++;
        }

        int port = 0;
        if (args_p_index != -1) {
            if (args.length > args_p_index + 1 && !args[args_p_index+1].equals("-i") && !args[args_p_index+1].equals("-s") && !args[args_p_index+1].equals("-q") && !args[args_p_index+1].equals("-t") && !args[args_p_index+1].equals("-u") && !args[args_p_index+1].equals("-d")) {
                port = Integer.parseInt(args[args_p_index+1]);
            }
            else {
                System.out.println("-p argument input error");
                return ;
            }
        }

        String pathInFiles = "./";
        if (args_d_index != -1) {
            if (args.length > args_d_index + 1 && !args[args_d_index+1].equals("-i") && !args[args_d_index+1].equals("-s") && !args[args_d_index+1].equals("-q") && !args[args_d_index+1].equals("-t") && !args[args_d_index+1].equals("-u") && !args[args_p_index+1].equals("-p")) {
                pathInFiles = args[args_d_index+1];
            }
            else {
                System.out.println("-d argument input error");
                return ;
            }
        }
        else {
            if (args_u_index == -1 && args_p_index == -1) {
                System.out.println("-d argument input error");
                return ;
            }
        }

        String pathOutIndex = "./index.txt";
        if (args_i_index != -1) {
            if (args.length <= args_i_index + 1 || args[args_i_index+1].equals("-d") || args[args_i_index+1].equals("-q") || args[args_i_index+1].equals("-s") || args[args_i_index+1].equals("-t") || args[args_i_index+1].equals("-u") || args[args_p_index+1].equals("-p")) {
                pathOutIndex = "./index.txt";
            }
            else if (args.length > args_i_index + 1) {
                pathOutIndex = args[args_i_index+1];
            }
        }

        String pathOutQuery = "search.txt";
        if (args_s_index != -1) {
            if (args.length <= args_s_index + 1 || args[args_s_index+1].equals("-d") || args[args_s_index+1].equals("-q") || args[args_s_index+1].equals("-i") || args[args_s_index+1].equals("-t") || args[args_s_index+1].equals("-u") || args[args_p_index+1].equals("-p")) {
                pathOutQuery = "search.txt";
            }
            else if (args.length > args_s_index + 1) {
                pathOutQuery = args[args_s_index+1];
            }
        }

        String pathInQuery = "./search.txt";
        if (args_q_index != -1) {
            if (args.length <= args_q_index + 1 || args[args_q_index+1].equals("-d") || args[args_q_index+1].equals("-s") || args[args_q_index+1].equals("-i") || args[args_q_index+1].equals("-t") || args[args_q_index+1].equals("-u") || args[args_p_index+1].equals("-p")) {
                System.out.println("-q argument input error");
                return ;
            }
            else if (args.length > args_q_index + 1) {
                pathInQuery = args[args_q_index+1];
            }
        }

        int threadAmount = 5;
        if (args_t_index != -1) {
            if (args.length <= args_t_index + 1 || args[args_t_index+1].equals("-d") || args[args_t_index+1].equals("-s") || args[args_t_index+1].equals("-i") || args[args_t_index+1].equals("-q") || args[args_t_index+1].equals("-u") || args[args_p_index+1].equals("-p")) {
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
            if (args.length <= args_u_index + 1 || args[args_u_index+1].equals("-d") || args[args_u_index+1].equals("-s") || args[args_u_index+1].equals("-i") || args[args_u_index+1].equals("-t") || args[args_u_index+1].equals("-q") || args[args_p_index+1].equals("-p")) {
                System.out.println("-u argument input error");
                return ;
            }
            else if (args.length > args_u_index + 1) {
                seed = args[args_u_index+1];
            }
        }

        for (String a:args) {
            System.out.println(a);
        }

        if (args_p_index != -1) {
            if (seed.equals("")) {
                System.out.println("need a seed");
                return;
            }
            SearchServer ss = new SearchServer(port,seed,threadAmount);
        }

        File file = new File(pathInFiles);
        if (!file.isDirectory()) {
            System.out.println("No Directory");
            return;
        }

        System.out.println("Start index working");
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
        System.out.println("Finish index working");

        MultithreadedSearchQuery sq = null;
        if (args_q_index != -1) {
            System.out.println("Start searching");
            sq = new MultithreadedSearchQuery(ii.getWholeMap(), pathInQuery,threadAmount);

            while (true) {
                sleep(10);
                if (sq.finished()) break;
            }
            System.out.println("Finish searching");
        }

        if (args_i_index != -1) {
            ii.outPutIndex(pathOutIndex);
        }


        if (args_s_index != -1) {
            file = new File(pathOutQuery);
            if (file.isDirectory()) {
                System.out.println("No Write Directory");
                return ;
            }
            sq.outPutQuerys(pathOutQuery);
        }

        ii.shutdown();
        if(args_q_index != -1) sq.shutdown();
    }
}