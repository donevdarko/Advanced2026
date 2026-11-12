package midterm25;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class NotSupportedExpenseException extends Exception{
    public NotSupportedExpenseException(String expense) {
        super(expense);
    }
}

enum Level {
    IC, //individual contributor
    M, //middle management
    C //C-Level executives
}

class Employee {
    String name;
    String jobTitle;
    Level level;

    public Employee(String name, String jobTitle, Level level) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format(
                "Employee: name=%s, title=%s, level=%s",
                name,
                jobTitle,
                level.toString()
        );
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name) && Objects.equals(jobTitle, employee.jobTitle) && level == employee.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, jobTitle, level);
    }
}

class Item {
    String name;
    String category;

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    double price;

    public Item(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %.2f USD", name, category, price);
    }

}


class Receipt {
    String merchant;

    public List<Item> getItems() {
        return items;
    }

    LocalDateTime date;
    List<Item> items;

    public Receipt(String merchant, LocalDateTime date, List<Item> items) {
        this.merchant = merchant;
        this.date = date;
        this.items = items;
    }

    double totalAmount() {
        return items.stream().mapToDouble(i -> i.price).sum();
    }

    LocalDateTime getDate(){
        return date;
    }

    int getCount(){
        return items.size();
    }

    @Override
    public String toString() {
        String itemsStr = items.stream()
                .map(Item::toString)
                .collect(Collectors.joining("; "));

        return String.format(
                "Receipt: merchant=%s, date=%s, items=%s, total=%.2f USD",
                merchant,
                date,
                itemsStr,
                totalAmount()
        );
    }

}

class DailyExpensesPerCountry {
    static Map<String, Double> ALLOWANCE = new HashMap<>();

    static {
        ALLOWANCE.put("US", 50.0);
        ALLOWANCE.put("MK", 10.0);
        ALLOWANCE.put("PT", 30.0);

        ALLOWANCE.put("DE", 45.0);   // Germany
        ALLOWANCE.put("AT", 40.0);   // Austria
        ALLOWANCE.put("CH", 55.0);   // Switzerland
        ALLOWANCE.put("FR", 50.0);   // France
        ALLOWANCE.put("IT", 40.0);   // Italy
        ALLOWANCE.put("ES", 35.0);   // Spain
        ALLOWANCE.put("UK", 50.0);   // United Kingdom
        ALLOWANCE.put("NL", 45.0);   // Netherlands
        ALLOWANCE.put("BE", 45.0);   // Belgium
        ALLOWANCE.put("SE", 50.0);   // Sweden
        ALLOWANCE.put("NO", 55.0);   // Norway
        ALLOWANCE.put("DK", 50.0);   // Denmark
        ALLOWANCE.put("PL", 25.0);   // Poland
        ALLOWANCE.put("CZ", 25.0);   // Czech Republic
        ALLOWANCE.put("SK", 20.0);   // Slovakia
        ALLOWANCE.put("HU", 20.0);   // Hungary
        ALLOWANCE.put("HR", 25.0);   // Croatia
        ALLOWANCE.put("BG", 20.0);   // Bulgaria
        ALLOWANCE.put("RO", 20.0);   // Romania
        ALLOWANCE.put("GR", 30.0);   // Greece
        ALLOWANCE.put("RS", 15.0);   // Serbia
        ALLOWANCE.put("AL", 15.0);   // Albania
        ALLOWANCE.put("TR", 20.0);   // Türkiye

        ALLOWANCE.put("CA", 45.0);   // Canada
        ALLOWANCE.put("MX", 25.0);   // Mexico
        ALLOWANCE.put("BR", 20.0);   // Brazil
        ALLOWANCE.put("AR", 18.0);   // Argentina
        ALLOWANCE.put("CL", 22.0);   // Chile

        ALLOWANCE.put("AU", 50.0);   // Australia
        ALLOWANCE.put("NZ", 40.0);   // New Zealand

        ALLOWANCE.put("JP", 45.0);   // Japan
        ALLOWANCE.put("CN", 30.0);   // China
        ALLOWANCE.put("KR", 35.0);   // South Korea
        ALLOWANCE.put("SG", 50.0);   // Singapore
        ALLOWANCE.put("IN", 20.0);   // India
        ALLOWANCE.put("AE", 45.0);   // UAE (Dubai)
        ALLOWANCE.put("SA", 30.0);   // Saudi Arabia
    }
}

public class ExpensesManagementSystemTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Float maxReceiptAmount = Float.parseFloat(sc.nextLine());

        // Create system with some default max amount
        ExpenseManagementSystem system = new ExpenseManagementSystem(maxReceiptAmount);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("END")) break;
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            String method = parts[0];


            switch (method) {

                case "addReceiptExpense": {
                    // Format:
                    // addReceiptExpense;Name;Job;IC|M|C;description;amount;merchant;datetime;item|cat|price,...
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];

                    String merchant = parts[5];
                    LocalDateTime dt = LocalDateTime.parse(parts[6]);

                    // Items list
                    String itemsRaw = parts[7];
                    List<Item> items = new ArrayList<>();
                    if (!itemsRaw.equalsIgnoreCase("none")) {
                        for (String itemStr : itemsRaw.split(",")) {
                            String[] ip = itemStr.split("\\|");
                            items.add(new Item(ip[0], ip[1], Double.parseDouble(ip[2])));
                        }
                    }

                    Employee e = new Employee(empName, job, lvl);
                    Receipt r = new Receipt(merchant, dt, items);

                    try {
                        system.addReceiptExpense(e, description, r);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "addTravelExpense": {
                    // Format:
                    // addTravelExpense;Name;Job;IC|M|C;description;amount;start;end;country
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];
                    double amount = Double.parseDouble(parts[5]);
                    LocalDateTime start = LocalDateTime.parse(parts[6]);
                    LocalDateTime end = LocalDateTime.parse(parts[7]);
                    String country = parts[8];

                    Employee e = new Employee(empName, job, lvl);

                    try {
                        system.addTravelExpense(e, description, amount, start, end, country);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "printRefunds": {
                    system.printRefunds();
                    break;
                }

                case "totalRefundsPerEmployee": {
                    Map<Employee, Double> map = system.totalRefundsPerEmployee();
                    map.forEach((emp, total) ->
                            System.out.printf("%s -> %.2f%n", emp.name, total));
                    break;
                }

                default:
                    System.out.println("Unknown method: " + method);
            }
        }
    }
}

class ExpenseManagementSystem{
    protected float maxReceiptAmount;
    protected List<Expense> expenses;

    public ExpenseManagementSystem(float maxReceiptAmount){
        this.maxReceiptAmount = maxReceiptAmount;
        this.expenses = new ArrayList<>();
    }

    public void addTravelExpense(Employee employee, String reason, double amount, LocalDateTime start, LocalDateTime end, String country) throws NotSupportedExpenseException{
        expenses.add(new TravelExpense(employee, reason, amount, start, end, country, "T"));
    }

    public void addReceiptExpense(Employee employee, String reason, Receipt receipt) throws NotSupportedExpenseException{
        LocalDateTime moment = receipt.date;
        for(Expense exp : expenses){
            if (!exp.getEmployee().equals(employee)) continue;
            if (exp.getType().equals("R")) continue;
            LocalDateTime start = ((TravelExpense) exp).getStart();
            LocalDateTime end = ((TravelExpense) exp).getEnd();
            if (moment.isAfter(start) & moment.isBefore(end)) throw new NotSupportedExpenseException("You cannot add receipt expense in the same period during an approved travel expense.");
        }

        if (receipt.totalAmount() > maxReceiptAmount) throw new NotSupportedExpenseException(String.format("Receipt with amount %.2f exceeds the max allowed amount for receipt expense %.2f", receipt.totalAmount(), maxReceiptAmount));
        expenses.add(new ReceiptExpense(employee, reason, receipt, "R"));
    }

    public void printRefunds(){
//        for(Expense exp : expenses){
//            System.out.println(exp.toString());
//        }

        expenses.stream()
                .sorted(Collections.reverseOrder())
                .forEach(System.out::println);
    }

    public Map<Employee, Double> totalRefundsPerEmployee(){
        Map<Employee, Double> heshmap = new HashMap<>();
        for(Expense exp : expenses){
            if (heshmap.get(exp.getEmployee()) == null) heshmap.putIfAbsent(exp.getEmployee(), exp.getRefund());
            else {
                double partial = heshmap.get(exp.getEmployee());
                partial+=exp.getRefund();
                heshmap.replace(exp.getEmployee(), partial);
            }
        }
        return heshmap;
    }
}

abstract class Expense implements Comparable<Expense> {
    private Employee employee;
    private String reason;
    private double amount;
    private String type;

    public Expense(Employee employee, String reason, double amount, String type){
        this.employee = employee;
        this.reason = reason;
        this.amount = amount;
        this.type = type;
    }

    public Employee getEmployee(){
        return employee;
    }

    public String getType(){
        return type;
    }

    public String getReason() {
        return reason;
    }

    public double getAmount() {
        return amount;
    }

    public abstract double getRefund();
}

class TravelExpense extends Expense{
    private LocalDateTime start;
    private LocalDateTime end;
    private String country;

    public TravelExpense(Employee employee, String reason, double amount, LocalDateTime start, LocalDateTime end, String country, String type){
        super(employee, reason, amount, type);
        this.start = start;
        this.end = end;
        this.country = country;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public double getRefund(){
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        return getAmount() + daysBetween * DailyExpensesPerCountry.ALLOWANCE.get(country);

    }

    @Override
    public String toString(){
        String formated = String.format("TravelExpense: employee={%s}, description=%s, baseAmount=%.2f USD, country=%s, start=%s, end=%s, refund=%.2f USD",
                getEmployee().toString(),
                getReason(),
                getAmount(),
                country,
                start.toString(),
                end.toString(),
                getRefund());
        return formated;
    }

    @Override
    public int compareTo(Expense expense) {
        return Double.compare(getRefund(), expense.getRefund());
    }
}

class ReceiptExpense extends Expense{
    private Receipt receipt;
    public ReceiptExpense(Employee employee, String reason, Receipt receipt, String type) {
        super(employee, reason, receipt.totalAmount(), type);
        this.receipt = receipt;
    }

    public double getRefund(){
        double refund = 0.0;
        Level level = getEmployee().getLevel();
        List<Item> items = receipt.getItems();
        if (level == Level.IC){
            for (Item it : items){
                if (it.getCategory().equals("food")) refund += it.getPrice();
                else if (it.getCategory().equals("non-alcohol beverage")) refund += it.getPrice();
                else continue;
            }
        }
        if (level == Level.M){
            for (Item it : items){
                if (it.getCategory().equals("food")) refund += it.getPrice();
                else if (it.getCategory().equals("non-alcohol beverage")) refund += it.getPrice();
                else if (it.getCategory().equals("transport")) refund += it.getPrice();
                else if (it.getCategory().equals("alcohol beverage")) refund += it.getPrice();
                else continue;
            }
        }
        if (level == Level.C){
            for (Item it : items){
                refund += it.getPrice();
            }
        }

        return refund;
    }

    @Override
    public String toString(){
        String formated = String.format("ReceiptExpense: employee={%s}, description=%s, receiptAmount=%.2f USD, receiptDate=%s, itemsCount=%d, refund=%.2f USD",
                getEmployee().toString(),
                getReason(),
                receipt.totalAmount(),
                receipt.getDate().toString(),
                receipt.getCount(),
                getRefund()
                );
        return formated;
    }

    @Override
    public int compareTo(Expense expense) {
        return Double.compare(getRefund(), expense.getRefund());
    }
}