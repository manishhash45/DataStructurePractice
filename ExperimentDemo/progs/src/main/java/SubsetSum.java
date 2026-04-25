public class SubsetSum {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3};
//        Subsets: {}, {1}, {2}, {3}, {1,2}, {1,3}, {2,3}, {1,2,3}
//        Subset Sums: 0, 1, 2, 3, 3, 4, 5, 6
        boolean[] visited = new boolean[arr.length];
        System.out.println("All subsets with sum ");
        //generateSubsets11(arr, 0 ,0,visited);
    }

    // Wrong approach to generate all subsets
    private static void generateSubsets1(int[] arr, int index, int sum , boolean[] visited) {
        // Base case: reached end of array
        if (index == arr.length) {
            System.out.print(" "+sum);
            return;
        }

        for(int i = 0; i < arr.length; i++) {
            if(!visited[i]) {
                visited[i]=true;
                sum = sum + arr[i];
                generateSubsets1(arr, i + 1, sum,visited);
                sum = sum - arr[i];
                visited[i]=false;
            }
        }

    }




    private static void generateSubsets(int[] arr, int index, int sum) {
        // Base case: reached end of array
        if (index == arr.length) {
            System.out.print(" "+sum);
            return;
        }

        // Include current element in subset
        generateSubsets(arr, index + 1, sum + arr[index]);

        // Exclude current element from subset
        generateSubsets(arr, index + 1, sum);
    }
}
