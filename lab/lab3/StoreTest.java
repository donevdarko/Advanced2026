package lab.lab3;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

class InvalidProductException extends Exception {
    public InvalidProductException(String line) {
        super(String.format("Invalid product: <%s>", line));
    }
}

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String category) {
        super(String.format("Category %s not found", category));
    }
}

abstract class Product implements Comparable<Product> {
    private int id;
    private String name;
    private String category;
    private double basePrice;

    public Product(int id, String name, String category, double basePrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public abstract double getFinalPrice();

    public abstract char getTypeCode();

    public static Product createProduct(String line) throws InvalidProductException {
        String[] splited = line.split(";", -1);
        if (splited.length != 6) throw new InvalidProductException(line);

        if (splited[0].length() != 1) throw new InvalidProductException(line);
        char typeChar = splited[0].charAt(0);
        if (typeChar != 'D' && typeChar != 'P') throw new InvalidProductException(line);

        int id;
        double basePrice;
        int sizeMb;
        double weight;

        try {
            id = Integer.parseInt(splited[1]);
            basePrice = Double.parseDouble(splited[4]);
            if (basePrice < 0) throw new InvalidProductException(line);


            if (typeChar == 'D'){
                sizeMb = Integer.parseInt(splited[5]);
                if (sizeMb <=0) throw new InvalidProductException(line);
                return new DigitalProduct(id, splited[2], splited[3], basePrice, sizeMb);
            }
            else {
                weight = Double.parseDouble(splited[5]);
                if (weight <=0) throw new InvalidProductException(line);
                return new PhysicalProduct(id, splited[2], splited[3], basePrice, weight);
            }
        } catch (NumberFormatException e){
            throw new InvalidProductException(line);
        }
    }

//    @Override
//    public int compareTo(Product other) {
//        // TODO  ne znam
//        return Double.compare(getFinalPrice(), other.getFinalPrice());
//    }

    @Override
    public int compareTo(Product other) {
        int c = Double.compare(getFinalPrice(), other.getFinalPrice());
        if (c != 0) return c;
        c = name.compareTo(other.name);
        if (c != 0) return c;
        return Integer.compare(id, other.id);
    }

    @Override
    public String toString() {
        return String.format("%c %-5d %-15s %-12s %10.2f", getTypeCode(), id, name, category, getFinalPrice());
    }
}

class PhysicalProduct extends Product {
    private double weight;

    public PhysicalProduct(int id, String name, String category, double basePrice, double weight) {
        super(id, name, category, basePrice);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public double getShippingCost() {
        return (weight<=1) ? 60 : 60+30*(weight-1);
    }

    @Override
    public double getFinalPrice() {
        return getShippingCost() + getBasePrice();
    }

    @Override
    public char getTypeCode() {
        return 'P';
    }

    @Override
    public String toString(){
        return super.toString() + String.format(" | %.2f kg", weight);
    }
}

class DigitalProduct extends Product {
    private int sizeMb;

    public DigitalProduct(int id, String name, String category, double basePrice, int sizeMb) {
        super(id, name, category, basePrice);
        this.sizeMb = sizeMb;
    }

    public int getSizeMb() {
        return sizeMb;
    }

    @Override
    public double getFinalPrice() {
        return getBasePrice()*0.9;
    }

    @Override
    public char getTypeCode() {
        return 'D';
    }

    @Override
    public String toString(){
        return super.toString() + String.format(" | %d MB", sizeMb);
    }
}

class Store {
    private List<Product> products = new ArrayList<>();
    private int invalidLines = 0;

    public int readProducts(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        int count = 0;
        while ((line = br.readLine()) != null){
            if(line.trim().isEmpty()) continue;

            try{
                products.add(Product.createProduct(line));
                count ++;
            } catch (InvalidProductException e){
                invalidLines++;
            }
        }
        return count;
    }

    public int getInvalidLines() {
        return invalidLines;
    }

    public void printSorted(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        products.stream()
                .sorted(Comparator.naturalOrder())
                .forEach(pw::println);
        pw.flush();
    }

    public void printByCategory(OutputStream os, String category) throws CategoryNotFoundException {

        Comparator<Product> byCategoryOrder =
                Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Product::getFinalPrice, Comparator.reverseOrder())
                        .thenComparing(Product::getId);

        PrintWriter pw = new PrintWriter(os);
        List<Product> matches = products.stream()
                .filter(product -> product.getCategory().equals(category))
                .sorted(byCategoryOrder)
                .collect(Collectors.toList());

        if (matches.isEmpty()) throw new CategoryNotFoundException(category);

        matches.forEach(pw::println);
        pw.flush();
    }

    public void printStatistics(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        int total = products.size();
        int pp = 0;
        int dp = 0;
        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0;
        double sumPrice = 0;
        for (Product prod : products){
            if (prod.getTypeCode() == 'P') pp++;
            if (prod.getTypeCode() == 'D') dp++;
            sumPrice += prod.getFinalPrice();
        }
        List<Product> prods = products.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        sumPrice /= total;

        if (total == 0) {
            pw.println("No products");
            pw.flush();
            return;
        }


        pw.println(String.format("Total: %d (physical: %d, digital: %d)", total, pp, dp));
        pw.println(String.format("Cheapest: %s", prods.get(0).toString()));
        pw.println(String.format("Most expensive: %s", prods.get(total-1).toString()));
        pw.println(String.format("Average price: %.2f", sumPrice));
        pw.flush();
    }
}

public class StoreTest {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder productLines = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.trim().equals("COMMANDS")) {
            productLines.append(line).append("\n");
        }

        Store store = new Store();
        int read = store.readProducts(
                new ByteArrayInputStream(productLines.toString().getBytes(StandardCharsets.UTF_8)));
        System.out.println("Read products: " + read);
        System.out.println("Invalid lines: " + store.getInvalidLines());

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            System.out.println("=== " + line + " ===");
            switch (parts[0]) {
                case "SORTED":
                    store.printSorted(System.out);
                    break;
                case "CATEGORY":
                    try {
                        store.printByCategory(System.out, parts[1]);
                    } catch (CategoryNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "STATS":
                    store.printStatistics(System.out);
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }
}
