import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int[] arr = {3, 2, 1};
        System.out.println("All subsequences of array [3, 2, 1]:");
        generateSubsequences(arr);
    }

    public static void generateSubsequences(int[] arr) {
        List<Integer> current = new ArrayList<>();
        backtrack(arr, 0, current);
    }

    private static void backtrack(int[] arr, int index, List<Integer> current) {
        // Print current subsequence
        System.out.println(current);

        // Generate remaining subsequences
        for (int i = index; i < arr.length; i++) {
            current.add(arr[i]);
            backtrack(arr, i + 1, current);
            current.remove(current.size() - 1);
        }
    }
}
