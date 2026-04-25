
import java.util.ArrayList;
import java.util.List;

public class SubSeq {

    public static void main(String[] args) {
        int[] arr = {3, 2, 1};
        System.out.println("All subsequences using Pick and Not Pick strategy:");
        generateSubsequencesPickNotPick(arr);
    }

    public static void generateSubsequencesPickNotPick(int[] arr) {
        List<Integer> current = new ArrayList<>();
        backtrackPickNotPick(arr, 0, current);
    }

    private static void backtrackPickNotPick(int[] arr, int index, List<Integer> current) {
        // Base case: reached end of array
        if (index == arr.length) {
            System.out.println(current);
            return;
        }

        // Pick: Include current element
        current.add(arr[index]);
        backtrackPickNotPick(arr, index + 1, current);

        // Not Pick: Exclude current element
        current.remove(current.size() - 1);
        backtrackPickNotPick(arr, index + 1, current);
    }
}