package abeshutt.staracademy.attribute.again;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Test {

    public static void main(String[] args) {
        // Precompute Catalan numbers up to 10
        long[] catalan = new long[22];
        catalan[0] = 1;
        for (int n = 1; n <= 21; n++) {
            catalan[n] = 0;
            for (int i = 0; i < n; i++) {
                catalan[n] += catalan[i] * catalan[n - 1 - i];
            }
        }

        int previous = 1;

        // For each x = number of operations in [2..10]
        for (int x = 2; x <= 20; x++) {
            // We'll use x+1 copies of the integer 5
            int[] nums = new int[x + 1];
            Arrays.fill(nums, 5);

            Set<Integer> allResults = new HashSet<>();

            // Iterate over all 2^x choices of '+' vs '*'
            int totalCombos = 1 << x;
            for (int mask = 0; mask < totalCombos; mask++) {
                char[] ops = new char[x];
                for (int i = 0; i < x; i++) {
                    // if the i-th bit of mask is 1 ⇒ '*', else '+'
                    ops[i] = ((mask & (1 << i)) != 0) ? '*' : '+';
                }
                // Evaluate all parenthesizations of this particular ops[] over nums[]
                allResults.addAll(evaluateAll(nums, ops, 0, x));
            }

            // Sort the results for nicer output
            int current = allResults.size();

            // Print
            System.out.println("=== " + x + " operations ===");
            System.out.println("Catalan: " + catalan[x]);
            System.out.println("Outputs: " + current);
            System.out.println("Ratio: " + (double)current / previous);
            System.out.println();

            previous = current;
        }
    }

    /**
     * Recursively compute the set of all possible values by
     * parenthesizing nums[start..end] with operators ops[start..end-1].
     *
     * @param nums  the array of numbers (all 5s)
     * @param ops   the array of operators ('+' or '*')
     * @param start the starting index in nums
     * @param end   the ending index in nums (inclusive)
     * @return      all possible evaluation results
     */
    private static Set<Integer> evaluateAll(int[] nums, char[] ops, int start, int end) {
        Set<Integer> results = new HashSet<>();
        // Base case: single number
        if (start == end) {
            results.add(nums[start]);
            return results;
        }
        // Try each possible split point
        for (int split = start; split < end; split++) {
            Set<Integer> leftVals  = evaluateAll(nums, ops, start, split);
            Set<Integer> rightVals = evaluateAll(nums, ops, split + 1, end);
            char op = ops[split];
            for (int a : leftVals) {
                for (int b : rightVals) {
                    if (op == '+')      results.add(a + b);
                    else if (op == '*') results.add(a * b);
                }
            }
        }
        return results;
    }

    public static void main2(String[] args) throws IOException {
        String raw = Files.readString(new File("C:\\Users\\Nexus\\Desktop\\Minecraft\\StarAcademyMod\\common\\src\\main\\java\\abeshutt\\staracademy\\attribute\\again\\main.json").toPath());
        JsonObject object = JsonParser.parseString(raw).getAsJsonObject();
        List<String> strings = new ArrayList<>();

        for(String key : object.keySet()) {
            strings.add(object.get(key).getAsString());
        }

        System.out.println(strings.size());
        System.out.println(new HashSet<>(strings).size());
        System.out.println(new HashSet<>(strings).stream().mapToInt(String::length).sum());

        int sum = strings.stream().mapToInt(String::length).sum();
        int max  = new HashSet<>(strings).stream().mapToInt(String::length).max().getAsInt();

        double best = Double.MAX_VALUE;
        double b = 0;
        double k = 0;

        for(int i = 1; i < max; i++) {
            Set<String> parts = new HashSet<>();

            for(String string : strings) {
                parts.add(string.substring(0, Math.min(string.length(), i)));
            }

            double totalSize = (Math.log(parts.size()) / Math.log(2)) * strings.size() + 8 * i * (Math.log(parts.size()) / Math.log(2));
            double rawSize = 8 * i * strings.size();
            double ratio = totalSize / sum;

            if(ratio < best) {
                best = ratio;
                b = i;
                k = (Math.log(parts.size()) / Math.log(2));
            }

            System.out.println(i + ": " + 8 * i + " / " + Math.log(parts.size()) / Math.log(2) + " | " + parts.size() + " | " + totalSize);

        }

        System.out.println(best);
        System.out.println(b);
        System.out.println(k);
    }

}
