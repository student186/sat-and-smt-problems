package cmpt;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import java.util.Scanner; 


public class Main {
    public static void main(String[] args) {
        // create a new context
        Context ctx = new Context();
        // create a solver
        Solver solver = ctx.mkSolver();

        System.out.print("Choose a solver by entering 1 for Graph Coloring or 2 for Sudoku: ");
        Scanner input = new Scanner(System.in);

        int choice = input.nextInt();
        input.nextLine(); // consume the newline character

         if (choice == 1) {
            GraphColoring gcProblem = new GraphColoring();
            gcProblem.gcSolver(ctx, solver);
             System.out.println("See the results in output_graph.txt");
        }
        else if (choice == 2) {
            Sudoku sudokuProblem = new Sudoku();
            sudokuProblem.sudokuSolver(ctx, solver);
            System.out.println("See the results in output_sudoku.txt");
        }
        else {
            System.out.println("Invalid input");
        }
        input.close();
    }
}
