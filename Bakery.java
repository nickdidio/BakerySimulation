import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Bakery implements Runnable {
    private static final int TOTAL_CUSTOMERS = 200;
    private static final int ALLOWED_CUSTOMERS = 50;
    private static final int FULL_BREAD = 20;
    private Map<BreadType, Integer> availableBread;
    private ExecutorService executor;
    private float sales = 0;

    // TODO
    //Semaphores?
    public Semaphore getSales = new Semaphore(1);
    public Semaphore cashiers = new Semaphore(4);
    public Semaphore ryeShelf = new Semaphore(1);
    public Semaphore sourdoughShelf = new Semaphore(1);
    public Semaphore wonderShelf = new Semaphore(1);

    /**
     * Remove a loaf from the available breads and restock if necessary
     */
    public void takeBread(BreadType bread) {
        int breadLeft = availableBread.get(bread);
        if (breadLeft > 0) {
            availableBread.put(bread, breadLeft - 1);
        } else {
            System.out.println("No " + bread.toString() + " bread left! Restocking...");
            // restock by preventing access to the bread stand for some time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            availableBread.put(bread, FULL_BREAD - 1);
        }
    }

    /**
     * Add to the total sales
     */
    public void addSales(float value) {
        sales += value;
    }

    /**
     * Run all customers in a fixed thread pool
     */
    public void run() {
        availableBread = new ConcurrentHashMap<BreadType, Integer>();
        availableBread.put(BreadType.RYE, FULL_BREAD);
        availableBread.put(BreadType.SOURDOUGH, FULL_BREAD);
        availableBread.put(BreadType.WONDER, FULL_BREAD);
        // TODO
        executor = Executors.newFixedThreadPool(ALLOWED_CUSTOMERS);
        Customer customers[] = new Customer[TOTAL_CUSTOMERS];
        for(int i = 0; i < TOTAL_CUSTOMERS; i++){
            customers[i] = new Customer(this);
            executor.execute(customers[i]);
        }
        executor.shutdown();
        try{
            executor.awaitTermination(10000,TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Total Sales: $"+sales);
    }
}