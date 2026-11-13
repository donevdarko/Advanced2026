//package lab.lab1;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.Arrays;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
//// ===================== Part A: your classes =====================
////
//// Copy your Ticket, HourlyTicket, DailyTicket, LotFullException and ParkingLot classes from Part A here.
//// Do not change Ticket, HourlyTicket, DailyTicket or LotFullException.
//
///*
//TODO: your Part A code here
//*/
//
//class LotFullException extends Exception{
//    public LotFullException() {
//        super();
//    }
//}
//
//abstract class  Ticket {
//    private final String plate;
//    private final int minutes;
//
//    protected Ticket(String plate, int minutes) {
//        if (plate == null || plate.isEmpty() || minutes <= 0) throw new IllegalArgumentException();
//        this.plate = plate;
//        this.minutes = minutes;
//    }
//
//    public String getPlate() {
//        return plate;
//    }
//
//    public int getMinutes() {
//        return minutes;
//    }
//
//    public abstract double price();
//
//    public String toString(){
//        return String.format("");
//    }
//}
//
//class HourlyTicket extends Ticket{
//    public HourlyTicket(String plate, int minutes) {
//        super(plate, minutes);
//    }
//
//    public double price(){
//        return ((getMinutes()+59)/60 * 50);
//    }
//}
//
//class DailyTicket extends Ticket{
//    public DailyTicket(String plate, int minutes) {
//        super(plate, minutes);
//    }
//    public double price(){
//        return ((getMinutes()+1439)/1440 * 300);
//    }
//}
//
//class ParkingLot{
//    private Ticket[] tickets;
//    private int ticketCount;
//    private int capacity;
//
//    public ParkingLot(int capacity){
//        if (!(capacity>0)) throw new IllegalArgumentException();
//        this.capacity = capacity;
//        tickets = new Ticket[capacity];
//        ticketCount = 0;
//    }
//
//    public void addTicket(Ticket ticket) throws LotFullException{
//        if (ticket == null) throw new IllegalArgumentException();
//        if (capacity == ticketCount) throw new LotFullException();
//        tickets[ticketCount++] = ticket;
//    }
//
//    public int getTicketCount() {
//        return ticketCount;
//    }
//
//    public double totalRevenue(){
//        double sum = 0.0;
//        if (ticketCount == 0) return 0.0;
//        for (int i = 0; i<ticketCount; i++){
//            sum += tickets[i].price();
//        }
//        return sum;
//    }
//
//    public double revenueWithRule(PricingRule rule){
//        if (rule == null) throw new IllegalArgumentException();
//
//        double sum = 0.0;
//        for (int i = 0; i<ticketCount; i++){
//            sum += rule.adjust(tickets[i], tickets[i].price());
//        }
//        return sum;
//    }
//
//    public double revenueWhere(Predicate<Ticket> condition){
//        if (condition == null) throw new IllegalArgumentException();
//
//        double sum = 0.0;
//        for (int i = 0; i<ticketCount; i++){
//            if (condition.test(tickets[i])) sum+= tickets[i].price();
//        }
//        return sum;
//    }
//
//    public static Predicate<Ticket> longerThan(int minutes){
//        return ticket -> ticket.getMinutes() > minutes;
//    }
//
//    public static Predicate<Ticket> shorterThan (int minutes){
//        return ticket -> ticket.getMinutes() < minutes;
//    }
//
//    public int countWhere(Predicate<Ticket> condition){
//        if (condition == null) throw new IllegalArgumentException();
//        int count = 0;
//        for (int i = 0; i < ticketCount; i++){
//            if (condition.test(tickets[i])) count++;
//        }
//        return count;
//    }
//
//    public String[] describeAll(Function<Ticket, String> describer){
//        if(describer==null) throw new IllegalArgumentException();
//        String[] result = new String[ticketCount];
//        for (int i = 0; i<ticketCount; i++){
//            result[i] = describer.apply(tickets[i]);
//        }
//        return result;
//    }
//
//    public String[] describeWhere(Predicate<Ticket> condition, Function<Ticket, String> describer){
//        if (condition == null || describer == null) throw new IllegalArgumentException();
//        String[] result = new String[countWhere(condition)];
//        int j = 0;
//        for (int i = 0; i < ticketCount; i++){
//            if (condition.test(tickets[i])){
//                result[j++] = describer.apply(tickets[i]);
//            }
//        }
//        return result;
//    }
//
//    public Ticket findFirst(Predicate<Ticket> condition){
//        if (condition == null) throw new IllegalArgumentException();
//        for (int i = 0; i<ticketCount; i++){
//            if (condition.test(tickets[i])) return tickets[i];
//        }
//        return null;
//    }
//
//    public Ticket findLast(Predicate<Ticket> condition){
//        if (condition == null) throw new IllegalArgumentException();
//        for (int i = ticketCount-1; i>=0; i--){
//            if(condition.test(tickets[i])) return tickets[i];
//        }
//        return null;
//    }
//
//
//}
//
//// ===================== Part B: new code =====================
//
///*
//TODO: PricingRule, PricingRules, and the new ParkingLot methods
//*/
//
//@FunctionalInterface
//interface PricingRule{
//    double adjust(Ticket ticket, double price);
//}
//
//class PricingRules{
//    private PricingRules(){
//
//    }
//
//    public static final PricingRule GRACE_PERIOD = (ticket, price) -> (ticket.getMinutes()<=20) ? 0 : price;
//
//    public static final PricingRule LONG_STAY_DISCOUNT = (ticket, price) -> (ticket.getMinutes() > 600) ? price*0.75 : price;
//
//    public static PricingRule flatOff(double amount){
//        if(amount < 0) throw new IllegalArgumentException();
//        return (ticket, price) -> {
//            double temp = 0;
//            if (price - amount >= 0) temp = price-amount;
//            if (temp < 0) temp = 0;
//            return temp;
//        };
//    }
//
//    public static PricingRule chain(PricingRule first, PricingRule second){
//        if (first == null || second == null) throw new IllegalArgumentException();
//        return (ticket, price) -> {
//            double temp = first.adjust(ticket, price);
//            return second.adjust(ticket, temp);
//        };
//    }
//}
//
//public class ParkingLotTest {
//    private static ParkingLot lot = null;
//
//    private static String plateArg(String token) {
//        if (token.equals("NULL")) return null;
//        if (token.equals("EMPTY")) return "";
//        return token;
//    }
//
//    private static boolean noLot() {
//        if (lot == null) {
//            System.out.println("No lot created");
//            return true;
//        }
//        return false;
//    }
//
//    private static PricingRule ruleByName(String name) {
//        if (name.startsWith("FLAT:")) return PricingRules.flatOff(Double.parseDouble(name.substring(5)));
//        switch (name) {
//            case "GRACE": return PricingRules.GRACE_PERIOD;
//            case "LONG": return PricingRules.LONG_STAY_DISCOUNT;
//            case "PLUS10": return (t, p) -> p + 10;
//            case "TIMES2": return (t, p) -> p * 2;
//            case "IDENTITY": return (t, p) -> p;
//            case "NULL": return null;
//            default: throw new IllegalStateException("unknown rule " + name);
//        }
//    }
//
//    // "A,B,C" applies A first, then B to A's result, then C (built only from the test's own lambdas).
//    private static PricingRule parseRules(String expr) {
//        PricingRule result = (t, p) -> p;
//        for (String name : expr.split(",")) {
//            PricingRule before = result;
//            PricingRule next = ruleByName(name);
//            result = (t, p) -> next.adjust(t, before.adjust(t, p));
//        }
//        return result;
//    }
//
//    private static Predicate<Ticket> atom(String s) {
//        if (s.startsWith("!")) {
//            return atom(s.substring(1)).negate();
//        }
//        if (s.equals("ALL")) return t -> true;
//        if (s.equals("NONE")) return t -> false;
//        if (s.startsWith("SHORTER:")) return ParkingLot.shorterThan(Integer.parseInt(s.substring(8)));
//        if (s.startsWith("LONGER:")) {
//            int n = Integer.parseInt(s.substring(7));
//            return t -> t.getMinutes() > n;
//        }
//        if (s.startsWith("PLATE:")) {
//            String prefix = s.substring(6);
//            return t -> t.getPlate().startsWith(prefix);
//        }
//        throw new IllegalStateException("unknown predicate " + s);
//    }
//
//    // "|" separates alternatives, "&" joins conditions, "!" negates one condition.
//    private static Predicate<Ticket> parsePredicate(String expr) {
//        Predicate<Ticket> or = null;
//        for (String orPart : expr.split("\\|")) {
//            Predicate<Ticket> and = null;
//            for (String a : orPart.split("&")) {
//                Predicate<Ticket> p = atom(a);
//                and = (and == null) ? p : and.and(p);
//            }
//            or = (or == null) ? and : or.or(and);
//        }
//        return or;
//    }
//
//    private static Function<Ticket, String> describerByName(String name) {
//        switch (name) {
//            case "PLATE": return Ticket::getPlate;
//            case "PLATE_MINUTES": return t -> t.getPlate() + ":" + t.getMinutes();
//            case "PRICE": return t -> "" + t.price();
//            case "PLATE_PRICE": return t -> t.getPlate() + ":" + t.price();
//            default: throw new IllegalStateException("unknown function " + name);
//        }
//    }
//
//    private static Ticket ticketOf(String type, String minutes) {
//        return type.equals("HOURLY")
//                ? new HourlyTicket("X", Integer.parseInt(minutes))
//                : new DailyTicket("X", Integer.parseInt(minutes));
//    }
//
//    private static void run(String cmd, String[] p) throws LotFullException {
//        switch (cmd) {
//            case "LOT":
//                lot = new ParkingLot(Integer.parseInt(p[1]));
//                System.out.println("Lot created (capacity " + p[1] + ")");
//                break;
//
//            case "NEW_HOURLY": {
//                Ticket t = new HourlyTicket(plateArg(p[1]), Integer.parseInt(p[2]));
//                System.out.println("HourlyTicket " + t.getPlate() + " " + t.getMinutes() + " " + t.price());
//                break;
//            }
//            case "NEW_DAILY": {
//                Ticket t = new DailyTicket(plateArg(p[1]), Integer.parseInt(p[2]));
//                System.out.println("DailyTicket " + t.getPlate() + " " + t.getMinutes() + " " + t.price());
//                break;
//            }
//
//            case "ADD_HOURLY":
//                if (noLot()) break;
//                lot.addTicket(new HourlyTicket(plateArg(p[1]), Integer.parseInt(p[2])));
//                System.out.println("Added");
//                break;
//            case "ADD_DAILY":
//                if (noLot()) break;
//                lot.addTicket(new DailyTicket(plateArg(p[1]), Integer.parseInt(p[2])));
//                System.out.println("Added");
//                break;
//            case "ADD_NULL":
//                if (noLot()) break;
//                lot.addTicket(null);
//                System.out.println("Added");
//                break;
//
//            case "COUNT":
//                if (noLot()) break;
//                System.out.println("Count: " + lot.getTicketCount());
//                break;
//            case "REVENUE":
//                if (noLot()) break;
//                System.out.println("Revenue: " + lot.totalRevenue());
//                break;
//
//            // RULE_ADJUST <rules> <HOURLY|DAILY> <minutes> <price>
//            case "RULE_ADJUST":
//                System.out.println("Adjusted: " + parseRules(p[1]).adjust(ticketOf(p[2], p[3]), Double.parseDouble(p[4])));
//                break;
//            // CHAIN_ADJUST <first> <second> <HOURLY|DAILY> <minutes> <price>
//            case "CHAIN_ADJUST": {
//                PricingRule c = PricingRules.chain(ruleByName(p[1]), ruleByName(p[2]));
//                System.out.println("Chained: " + c.adjust(ticketOf(p[3], p[4]), Double.parseDouble(p[5])));
//                break;
//            }
//            case "RULE_REVENUE":
//                if (noLot()) break;
//                System.out.println("Rule revenue: " + lot.revenueWithRule(parseRules(p[1])));
//                break;
//            case "RULE_NULL":
//                if (noLot()) break;
//                lot.revenueWithRule(null);
//                System.out.println("No exception");
//                break;
//            case "RULE_CALLS": {
//                if (noLot()) break;
//                int[] calls = {0};
//                lot.revenueWithRule((t, price) -> {
//                    calls[0]++;
//                    return price;
//                });
//                System.out.println("Calls: " + calls[0]);
//                break;
//            }
//
//            case "SHORTER_TEST":
//                System.out.println("Shorter: " + ParkingLot.shorterThan(Integer.parseInt(p[1]))
//                        .test(new HourlyTicket("X", Integer.parseInt(p[2]))));
//                break;
//            case "COUNT_WHERE":
//                if (noLot()) break;
//                System.out.println("CountWhere: " + lot.countWhere(parsePredicate(p[1])));
//                break;
//            case "COUNT_WHERE_NULL":
//                if (noLot()) break;
//                lot.countWhere(null);
//                System.out.println("No exception");
//                break;
//
//            case "FIND_LAST": {
//                if (noLot()) break;
//                Ticket t = lot.findLast(parsePredicate(p[1]));
//                System.out.println("Found: " + (t == null ? "none" : t.getPlate()));
//                break;
//            }
//            case "FIND_LAST_NULL":
//                if (noLot()) break;
//                lot.findLast(null);
//                System.out.println("No exception");
//                break;
//
//            case "DESCRIBE_WHERE": {
//                if (noLot()) break;
//                String[] r = lot.describeWhere(parsePredicate(p[1]), describerByName(p[2]));
//                System.out.println("Describe: " + r.length + " " + Arrays.toString(r));
//                break;
//            }
//            case "DESCRIBE_NULL_COND":
//                if (noLot()) break;
//                lot.describeWhere(null, describerByName("PLATE"));
//                System.out.println("No exception");
//                break;
//            case "DESCRIBE_NULL_FN":
//                if (noLot()) break;
//                lot.describeWhere(parsePredicate("ALL"), null);
//                System.out.println("No exception");
//                break;
//            case "DESCRIBE_CALLS": {
//                if (noLot()) break;
//                int[] calls = {0};
//                lot.describeWhere(parsePredicate(p[1]), t -> {
//                    calls[0]++;
//                    return "x";
//                });
//                System.out.println("Calls: " + calls[0]);
//                break;
//            }
//
//            default:
//                System.out.println("Unknown command");
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        String line;
//        while ((line = in.readLine()) != null) {
//            line = line.trim();
//            if (line.isEmpty()) continue;
//            String[] p = line.split("\\s+");
//            if (p[0].equals("END")) break;
//            try {
//                run(p[0], p);
//            } catch (LotFullException e) {
//                System.out.println("LotFullException");
//            } catch (IllegalArgumentException e) {
//                System.out.println("IllegalArgumentException");
//            }
//        }
//    }
//}
