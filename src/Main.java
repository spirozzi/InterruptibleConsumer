import java.util.ArrayList;

public class Main
{
	/**
	 * Demonstrates how to subclass and make use of InterruptibleConsumer.
	 */
	public static void main(String[] args) throws InterruptedException
	{
		singleThreadDemo();
		Thread.sleep(100);
		System.out.println("----------");
		multithreadedDemo(30, 2);
		Thread.sleep(100);
		System.out.println("----------");
		multithreadedDemo(50, 5);
		Thread.sleep(100);
		System.out.println("----------");
		multithreadedDemo(80, 10);
		Thread.sleep(100);
		System.out.println("----------");
	}
	
	/**
	 * Adds 1000 items to a single consumer before starting it. After the
	 * elements are added, the consumer is started and promptly interrupted
	 * after 3 millis to test that it interrupts properly.
	 */
	private static void singleThreadDemo() throws InterruptedException
	{
		InterruptibleConsumer<String> ic = new PrinterConsumer<>(1000);
		for (int i = 1; i <= 1000; i++)
		{
			ic.addItem("" + i);
		}
		// start processing, sleep for 3 millis then interrupt the thread
		ic.start();
		Thread.sleep(3);
		ic.interrupt();
		// Attempt to add item after the thread is interrupted
		ic.addItem("This element should not be added or printed to stdout.");
	}
	
	/**
	 * Distributes each item to a single consumer in a list by using hashing to
	 * determine which consumer to send each item to.
	 */
	private static void multithreadedDemo(int numItems, int numConsumers)
	{
		ArrayList<InterruptibleConsumer<String>> consumers = new ArrayList<>();
		// initialize and start consumers
		for (int i = 0; i < numConsumers; i++)
		{
			consumers.add(new PrinterConsumer<String>(3));
			consumers.get(i).start();
		}
		// hash each item to decide which consumer to send it to
		for (int i = 1; i <= numItems; i++)
		{
			String item = "" + i;
			InterruptibleConsumer<String> ic =
					consumers.get(hashAndReduce(item, numConsumers));
			ic.addItem(item);
		}
		// stop consumers
		for (InterruptibleConsumer<String> ic : consumers)
		{
			ic.interrupt();
		}
	}
	
	/**
	 * Hashes an object and reduces the hash to a range between 0 and maxVal
	 * inclusive
	 */
	private static int hashAndReduce(Object item, int maxVal)
	{
		return Math.abs(item.hashCode()) % maxVal;
	}
}