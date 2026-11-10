package midterm25;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Repository<T extends Valuable & Comparable<T>> implements Comparable<Repository<?>> {

    private final List<T> elements;
    public Repository() { this.elements = new ArrayList<>(); }

    public void addElement(T element){
        elements.add(element);
    }

    public Repository<T> lessThan(double value){
        Repository<T> glupo = new Repository<T>();
        for (T elem : elements){
            if (elem.getValue() < value){
                glupo.addElement(elem);
            }
        }
        return glupo;
    }

    public double totalValue(){
        double sum = 0;
        for (T elem : elements){
            sum += elem.getValue();
        }
        return sum;
    }

    @Override
    public int compareTo(Repository<?> arg0) {
        return Double.compare(this.totalValue(), arg0.totalValue());
    }

    @Override 
    public String toString(){
        String body = elements.stream()
                .sorted(Comparator.reverseOrder())
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        StringBuilder sb = new StringBuilder();
        sb.append("Repository elements:\n");
        if (!body.isEmpty()) sb.append(body).append("\n");
        sb.append(String.format("Total value: %.2f \n", totalValue()));
        return sb.toString();
    }
}

interface Valuable {
    double getValue();
}

class Stock implements Valuable, Comparable<Stock> {
    private String symbol;
    private int units;
    private double pricePerUnit;

    public Stock(String symbol, int units, double pricePerUnit) {
        this.symbol = symbol;
        this.units = units;
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public double getValue() {
        return units * pricePerUnit;
    }

    @Override
    public int compareTo(Stock other) {
        int cmp = Double.compare(this.getValue(), other.getValue());
        if (cmp != 0) return cmp;

        // If equal value, compare by number of units
        return Integer.compare(this.units, other.units);
    }

    @Override
    public String toString() {
        return String.format("Stock[%s, units=%d, value=%.2f]", symbol, units, getValue());
    }
}

class Metal implements Valuable, Comparable<Metal> {
    private String type;
    private double weightGrams;
    private double pricePerGram;

    public Metal(String type, double weightGrams, double pricePerGram) {
        this.type = type;
        this.weightGrams = weightGrams;
        this.pricePerGram = pricePerGram;
    }

    @Override
    public double getValue() {
        return weightGrams * pricePerGram;
    }

    @Override
    public int compareTo(Metal other) {
        return Double.compare(this.weightGrams, other.weightGrams);
    }

    @Override
    public String toString() {
        return String.format("Metal[%s, weight=%.1f g, value=%.2f]", type, weightGrams, getValue());
    }
}

class Collectible implements Valuable, Comparable<Collectible> {
    private String name;
    private int rarity; // 1 (common) to 10 (legendary)
    private double basePrice;

    public Collectible(String name, int rarity, double basePrice) {
        this.name = name;
        this.rarity = rarity;
        this.basePrice = basePrice;
    }

    @Override
    public double getValue() {
        return rarity * basePrice;
    }

    @Override
    public int compareTo(Collectible other) {
        // Higher rarity = greater item
        int cmp = Integer.compare(other.rarity, this.rarity); // DESCENDING
        if (cmp != 0) return cmp;

        return Double.compare(this.basePrice, other.basePrice);
    }

    @Override
    public String toString() {
        return String.format("Collectible[%s, rarity=%d, value=%.2f]",
                name, rarity, getValue());
    }
}



interface RepositoryProcessor{
    public void process(List<Repository<?>> repos);
}

public class GenericsTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // -------------------------------------------------
        // 1. READ ALL REPOSITORIES
        // -------------------------------------------------

        int N = Integer.parseInt(sc.nextLine());

        List<Repository<?>> repos = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            int type = Integer.parseInt(sc.nextLine());

            int M = Integer.parseInt(sc.nextLine());

            switch (type) {
                case 1:
                    repos.add(readStockRepo(sc, M));
                    break;
                case 2:
                    repos.add(readMetalRepo(sc, M));
                    break;
                case 3:
                    repos.add(readCollectibleRepo(sc, M));
                    break;
                default:
                    throw new RuntimeException("Invalid repo type!");
            }
        }

        // -------------------------------------------------
        // 2. MENU — USER CHOOSES ONLY ONE OPERATION
        // -------------------------------------------------

        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {

            // -------------------------------------------------
            // toString()
            // -------------------------------------------------
            case 1:

                int idx = Integer.parseInt(sc.nextLine());
                if (valid(idx, repos)) {
                    System.out.println("=== REPOSITORY CONTENT ===");
                    System.out.println(repos.get(idx));
                } else System.out.println("Invalid index!");
                break;

            // -------------------------------------------------
            // totalValue()
            // -------------------------------------------------
            case 2:
                idx = Integer.parseInt(sc.nextLine());
                if (valid(idx, repos)) {
                    System.out.println("Total value = " + repos.get(idx).totalValue());
                    System.out.println("");
                } else System.out.println("Invalid index!");
                break;

            // -------------------------------------------------
            // compareTo()
            // -------------------------------------------------
            case 3:
                int a = Integer.parseInt(sc.nextLine());
                int b = Integer.parseInt(sc.nextLine());

                if (valid(a, repos) && valid(b, repos)) {
                    int cmp = repos.get(a).compareTo(repos.get(b));
                    System.out.println("=== compareTo RESULT ===");
                    if (cmp == 0) System.out.println("Equal values");
                    else if (cmp < 0) System.out.println("Repo " + a + " has LESS total value");
                    else System.out.println("Repo " + a + " has MORE total value");
                } else System.out.println("Invalid indices!");
                break;

            // -------------------------------------------------
            // lessThan()
            // -------------------------------------------------
            case 4:
                idx = Integer.parseInt(sc.nextLine());
                double limit = Double.parseDouble(sc.nextLine());

                if (valid(idx, repos)) {
                    Repository<?> res = repos.get(idx).lessThan(limit);
                    System.out.println(res);
                } else System.out.println("Invalid index!");
                break;

            case 5:
                RepositoryProcessor processor = list -> list.stream()
                        .sorted(Repository::compareTo)
                        .forEach(System.out::println);
                //TODO define object RepositoryProcessor with lambda expression

                processor.process(repos);
                break;

            default:
                System.out.println("Invalid option!");
                break;
        }

    }

    // -----------------------------------------------------------
    // VALIDATION
    // -----------------------------------------------------------
    private static boolean valid(int idx, List<Repository<?>> repos) {
        return idx >= 0 && idx < repos.size();
    }

    // -----------------------------------------------------------
    // INPUT HELPERS
    // -----------------------------------------------------------
    private static Repository<Stock> readStockRepo(Scanner sc, int M) {
        Repository<Stock> repo = new Repository<>();
        for (int i = 0; i < M; i++) {
            String[] p = sc.nextLine().split("\\s+");
            repo.addElement(new Stock(
                    p[0],
                    Integer.parseInt(p[1]),
                    Double.parseDouble(p[2])
            ));
        }
        return repo;
    }

    private static Repository<Metal> readMetalRepo(Scanner sc, int M) {
        Repository<Metal> repo = new Repository<>();
        for (int i = 0; i < M; i++) {
            String[] p = sc.nextLine().split("\\s+");
            repo.addElement(new Metal(
                    p[0],
                    Double.parseDouble(p[1]),
                    Double.parseDouble(p[2])
            ));
        }
        return repo;
    }

    private static Repository<Collectible> readCollectibleRepo(Scanner sc, int M) {
        Repository<Collectible> repo = new Repository<>();
        for (int i = 0; i < M; i++) {
            String[] p = sc.nextLine().split("\\s+");
            repo.addElement(new Collectible(
                    p[0],
                    Integer.parseInt(p[1]),
                    Double.parseDouble(p[2])
            ));
        }
        return repo;
    }
}


