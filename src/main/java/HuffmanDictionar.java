import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HuffmanDictionar {

    private static Map<Character, String> charPrefixHashMap = new HashMap<>();
    static HuffmanNode root;

    public static void main(String[] args) {
        String text = readTextPentruDictionar();
        genereazaDictionar(text);
    }

    private static String readTextPentruDictionar() {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get("text-pentru-dictionar.txt"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    private static void genereazaDictionar(String text) {
        Map<Character, Integer> frequency = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            if (!frequency.containsKey(text.charAt(i))) {
                frequency.put(text.charAt(i), 0);
            }
            frequency.put(text.charAt(i), frequency.get(text.charAt(i)) + 1);
        }

        Map<Character, Integer> sortedFrequency =
                frequency.entrySet().stream()
                        .sorted(Entry.comparingByValue())
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));


        String prettyFreq = getPrettyFreq(frequency);
        System.out.println("Dictionarul de frecventa = " + prettyFreq);
        System.out.println();
        System.out.println("Sortat: " + sortedFrequency + "\n");

        root = buildTree(frequency);
        setPrefixCodes(root, new StringBuilder());

        String prettyPrefix = getPrettyPrefix();
        printInFile(prettyPrefix);
    }

    private static void printInFile(final String prettyPrefix) {
        try (PrintWriter out = new PrintWriter("dictionarul-meu.txt")) {
            out.println(prettyPrefix);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getPrettyFreq(final Map<Character, Integer> frequency) {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<Character, Integer>> iter = frequency.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Character, Integer> entry = iter.next();
            sb.append("caracterul \"");
            if (String.valueOf(entry.getKey()).matches(".")) {
                sb.append(entry.getKey());
            } else {
                sb.append("[rand nou]");
            }
            sb.append("\" cu frecventa ");
            sb.append(entry.getValue());
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();
    }

    private static HuffmanNode buildTree(Map<Character, Integer> freq) {

        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
        Set<Character> keySet = freq.keySet();
        for (Character c : keySet) {

            HuffmanNode huffmanNode = new HuffmanNode();
            huffmanNode.data = c;
            huffmanNode.frequency = freq.get(c);
            huffmanNode.left = null;
            huffmanNode.right = null;
            priorityQueue.offer(huffmanNode);
        }
        assert priorityQueue.size() > 0;

        while (priorityQueue.size() > 1) {

            HuffmanNode x = priorityQueue.peek();
            priorityQueue.poll();

            HuffmanNode y = priorityQueue.peek();
            priorityQueue.poll();

            HuffmanNode sum = new HuffmanNode();

            sum.frequency = x.frequency + y.frequency;
            sum.data = '-';

            sum.left = x;

            sum.right = y;
            root = sum;

            priorityQueue.offer(sum);
        }

        return priorityQueue.poll();
    }


    private static void setPrefixCodes(HuffmanNode node, StringBuilder prefix) {

        if (node != null) {
            if (node.left == null && node.right == null) {
                charPrefixHashMap.put(node.data, prefix.toString());

            } else {
                prefix.append('0');
                setPrefixCodes(node.left, prefix);
                prefix.deleteCharAt(prefix.length() - 1);

                prefix.append('1');
                setPrefixCodes(node.right, prefix);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }

    }

    private static String getPrettyPrefix() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<Character, String>> iter = charPrefixHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Character, String> entry = iter.next();
            sb.append("caracterul \"");
            if (String.valueOf(entry.getKey()).matches(".")) {
                sb.append(entry.getKey());
            } else {
                sb.append("[rand nou]");
            }
            sb.append("\" cu codul ");
            sb.append(entry.getValue());
            if (iter.hasNext()) {
                sb.append(',').append('\n');
            }
        }
        return sb.toString();
    }
}