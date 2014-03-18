/**
 * A simple example of how InterruptibleConsumer can be extended with custom
 * processing functionality. Prints the items it processes to stdout.
 */
public class PrinterConsumer<E> extends InterruptibleConsumer<E>
{
	public PrinterConsumer(int queueSize)
	{
		super(queueSize);
	}
	
	@Override
	protected void process(E item)
	{
		System.out.println("Processed: " + item);
	}
}