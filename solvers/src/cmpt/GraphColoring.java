package cmpt; 

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class GraphColoring {
    public void gcSolver(Context ctx, Solver solver) {
        // variables for two adjacent vertices
        int adj1 = 0;
        int adj2 = 0;

        BoolExpr clause1 = ctx.mkTrue();
        BoolExpr clause2 = ctx.mkTrue();
        BoolExpr clause3 = ctx.mkTrue();

        try {
            File file = new File("input_graph.txt");
            Scanner scanner = new Scanner(file);

            String line1 = scanner.nextLine();
            Scanner lineScanner1 = new Scanner(line1);       // scanner for the first line which consists of n m

            // create boolean variables
            int n = lineScanner1.nextInt();      // n is the number of vertices
            int m = lineScanner1.nextInt();      // m is the number of colors


            // introduce a boolean variable p_{i,j} for vertex i with color j
            BoolExpr[][] p = new BoolExpr[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    p[i][j] = ctx.mkBoolConst("p_" + (i+1) + "_" + (j+1));      // add 1 to represent vertices and colors starting at 1
                }
            }

            // formula asserting every vertex is colored
            for (int i = 0; i < n; i++) {
                BoolExpr f = ctx.mkFalse();
                for (int j = 0; j < m; j++) {
                    f = ctx.mkOr(p[i][j], f);
                }  
                clause1 = ctx.mkAnd(f, clause1);
            }

            solver.add(new BoolExpr[]{clause1});            // create an array of type BoolExpr that contains a single element clause1

            // formula asserting every vertex has at most one color
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    for (int k = j + 1; k < m; k++) {
                        BoolExpr f = ctx.mkOr(ctx.mkNot(p[i][j]), ctx.mkNot(p[i][k]));
                        clause2 = ctx.mkAnd(f, clause2);
                    }

                }  
                
            }

            solver.add(new BoolExpr[]{clause2});    
            lineScanner1.close();

            while (scanner.hasNextLine()) {
                String line2 = scanner.nextLine();
                Scanner lineScanner2 = new Scanner(line2);       // scanner for the lines after the first line which consist of edges

                // formula asserting that no two connected vertices have the same color
                adj1 = lineScanner2.nextInt();
                adj2 = lineScanner2.nextInt();
                for (int j = 0; j < m; j++) {
                    BoolExpr f = ctx.mkFalse();
                    BoolExpr literal = ctx.mkFalse();
                    for (int i = 0; i < n; i++) {
                        if (i == adj1 - 1 || i == adj2 - 1) {       // -1 due to vertex numbers starting at 1 and array indices starting at 0
                            literal = ctx.mkNot(p[i][j]);
                            f = ctx.mkOr(literal, f);
                        }
                    }  
                    clause3 = ctx.mkAnd(f, clause3);
                    
                }
                solver.add(new BoolExpr[]{clause3});        
                lineScanner2.close();
            }


            if (solver.check() == Status.UNSATISFIABLE || solver.check() == Status.UNKNOWN) {
                try {
                    FileWriter result1 = new FileWriter("output_graph.txt");
                    result1.write("No Solution");
                    result1.close();
                }

                catch (IOException e) {
                    System.out.println("An error occured");
                }

            }

            else {
                Model model = solver.getModel();
                try {
                    FileWriter result2 = new FileWriter("output_graph.txt");
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < m; j++) {
                            if (model.eval(p[i][j], true).isTrue()) {
                                result2.write((i + 1) + " " + (j + 1) + "\n");
                            }
                        }
                    } 
                    result2.close();
                }
                catch (IOException e) {
                    System.out.println("An error occured");
                }

            }

            scanner.close();
        }

        catch (FileNotFoundException e) {
            System.out.println("File not found: input_graph.txt");
        }

    }
}