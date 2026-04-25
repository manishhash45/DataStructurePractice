import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AtLestOneMaxElement {

    public static void main(String[] args) {

        int[] arr = {5, 5, 3};
        List<Integer> result = List.of(5, 5, 3);

        System.out.println("At least one maximum element in the array: " + solve(result));


        int[] arr1 = {5, 5, 5};
        

    }
    public static ArrayList<Integer> solve(List<Integer> ar) {
        Integer[] arr = ar.toArray(new Integer[0]);

        int start = 0;
        int end = arr.length - 1;

        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;

            start++;
            end--;
        }

        return new ArrayList<>(Arrays.asList(arr));
    }


    public static int atLeastOneMaxElement(int[] arr) {

        int max= Integer.MIN_VALUE;
        for(int num : arr){
            if(num > max){
                max = num;
            }
        }
        int count=0;
        for(int num : arr){
            if(num == max){
                count++;
            }
        }

        return arr.length-count;
    }



}
