import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Customer implements Runnable {
    private Bakery bakery;
    private Random rnd;
    private List<BreadType> shoppingCart;
    private int shopTime;
    private int checkoutTime;

    /**
     * Initialize a customer object and randomize its shopping cart
     */
    public Customer(Bakery bakery) {
        // TODO
        this.bakery = bakery;
        shoppingCart = new ArrayList<BreadType>();
        rnd = new Random();
        fillShoppingCart();
        shopTime = rnd.nextInt(500)+1;     //change this to a random value
        checkoutTime = rnd.nextInt(500)+1;   //change this to a random value
    }

    /**
     * Run tasks for the customer
     */
    public void run() {
        try {
        // TODO
        System.out.println("Customer "+hashCode()+" has started shopping.");

        //shopping
        for (BreadType currentCart : shoppingCart) {
            if(currentCart.equals(BreadType.values()[0])){
                bakery.ryeShelf.acquire();
                bakery.takeBread(currentCart);
                Thread.sleep(shopTime);
                System.out.println("Customer "+hashCode()+" has taken "+currentCart.toString()+" bread from stock.");
                bakery.ryeShelf.release();
            }else if(currentCart.equals(BreadType.values()[1])){
                bakery.sourdoughShelf.acquire();
                bakery.takeBread(currentCart);
                Thread.sleep(shopTime);
                System.out.println("Customer "+hashCode()+" has taken "+currentCart.toString()+" bread from stock.");
                bakery.sourdoughShelf.release();
            }else if(currentCart.equals(BreadType.values()[2])){
                bakery.wonderShelf.acquire();
                bakery.takeBread(currentCart);
                Thread.sleep(shopTime);
                System.out.println("Customer "+hashCode()+" has taken "+currentCart.toString()+" bread from stock.");
                bakery.wonderShelf.release();
            }
        }
        
        //calculate total
        bakery.cashiers.acquire();
        bakery.getSales.acquire();
        bakery.addSales(getItemsValue());
        bakery.getSales.release();
        Thread.sleep(checkoutTime);
        System.out.println("Customer "+hashCode()+" has bought their items.");
        bakery.cashiers.release();
        System.out.println("Customer "+hashCode()+" has finished.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return a string representation of the customer
     */
    public String toString() {
        return "Customer " + hashCode() + ": shoppingCart=" + Arrays.toString(shoppingCart.toArray()) + ", shopTime=" + shopTime + ", checkoutTime=" + checkoutTime;
    }

    /**
     * Add a bread item to the customer's shopping cart
     */
    private boolean addItem(BreadType bread) {
        // do not allow more than 3 items, chooseItems() does not call more than 3 times
        if (shoppingCart.size() >= 3) {
            return false;
        }
        shoppingCart.add(bread);
        return true;
    }

    /**
     * Fill the customer's shopping cart with 1 to 3 random breads
     */
    private void fillShoppingCart() {
        int itemCnt = 1 + rnd.nextInt(3);
        while (itemCnt > 0) {
            addItem(BreadType.values()[rnd.nextInt(BreadType.values().length)]);
            itemCnt--;
        }
    }

    /**
     * Calculate the total value of the items in the customer's shopping cart
     */
    private float getItemsValue() {
        float value = 0;
        for (BreadType bread : shoppingCart) {
            value += bread.getPrice();
        }
        return value;
    }
}