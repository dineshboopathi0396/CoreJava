/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myworkspace;

import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Administrator
 */
public class MyWorkSpace {

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        // TODO code application logic here
        // Enter data using BufferReader 
        Integer a[] = new Integer[]{5, 8, 8, 1, 8};
        List<Integer> sample = Arrays.asList(a);
        long sum = 0;
        for (int j = 0; j < sample.size(); j++) {
            sum = sum + sample.get(j);
        }
        System.out.println("sum: " + sum);

        LongSummaryStatistics stats = sample.stream().mapToLong((x) -> x).summaryStatistics();

        System.out.println("stats : " + stats);

        Map<Integer, Long> val;
        val = sample.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("val : " + val.values().stream()
                .max(Comparator.comparing(x -> x))
                .get());

        long output = sample.stream().collect(
                Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )
        ).values().stream()
                .max(Comparator.comparing(x -> x))
                .get();
        System.out.println(output);
    }

}
