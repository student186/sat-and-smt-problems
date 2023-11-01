package cmpt;

import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Model;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class Sudoku {
    public void sudokuSolver(Context ctx, Solver solver) {
        // create the sudoku grid of 9x9 cells containing integer variables d_{i,j}
        IntExpr[][] grid = new IntExpr[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = ctx.mkIntConst("d_" + (i+1) + "_" + (j+1)); // add 1 to represent appropriate positions of cells in the grid
            }
        }

        // read the sudoku grid from input_sudoku.txt and assign/equate digits to integer variables using scanner
        try {
            File file = new File("input_sudoku.txt");
            Scanner scanner = new Scanner(file);
            for (int i = 0; i < 9; i++) {
                String[] row = scanner.nextLine().split(" ");
                for (int j = 0; j < 9; j++) {
                    int digit = Integer.parseInt(row[j]);
                    if (digit != 0) {
                        solver.add(ctx.mkEq(grid[i][j], ctx.mkInt(digit)));
                    }
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: input_sudoku.txt");
            return;
        }

        // add the main constraints for sudoku
        IntExpr row[] = new IntExpr[9];
        IntExpr column[] = new IntExpr[9];
        IntExpr subgrid[] = new IntExpr[9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // each cell d_{i,j} must contain a digit between 1 and 9
                solver.add(ctx.mkAnd(ctx.mkGe(grid[i][j], ctx.mkInt(1)), ctx.mkLe(grid[i][j], ctx.mkInt(9))));
                row[j] = grid[i][j];
                column[j] = grid[j][i];
            }

            // each row and column must contain each of the digits 1 to 9 exactly once
            solver.add(ctx.mkDistinct(row));
            solver.add(ctx.mkDistinct(column));
        }

        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {    
                subgrid[0] = grid[i][j];
                subgrid[1] = grid[i][j + 1];
                subgrid[2] = grid[i][j + 2];
                subgrid[3] = grid[i + 1][j];
                subgrid[4] = grid[i + 1][j + 1];
                subgrid[5] = grid[i + 1][j + 2];
                subgrid[6] = grid[i + 2][j];
                subgrid[7] = grid[i + 2][j + 1];
                subgrid[8] = grid[i + 2][j + 2];

                // each subgrid of 3x3 cells must contain each of the digits 1 to 9 exactly once
                solver.add(ctx.mkDistinct(subgrid));
            }
        }

        if (solver.check() == Status.SATISFIABLE) {
            Model model = solver.getModel();
            try {
                FileWriter result1 = new FileWriter("output_sudoku.txt");
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        int digit = ((IntNum) model.evaluate(grid[i][j], false)).getInt();
                        result1.write(digit + " ");
                    }
                    result1.write(System.lineSeparator());
                }
                result1.close();
            }
            catch (IOException e) {
                System.out.println("An error occured");
            }
        }

        else {
            try {
                FileWriter result2 = new FileWriter("output_sudoku.txt");
                result2.write("No Solution");
                result2.close();
            }
            catch (IOException e) {
                System.out.println("An error occured");
            }
        }

    }

}
