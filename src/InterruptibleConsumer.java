import java.util.concurrent.ArrayBlockingQueue;

/**
 * Maintains a bounded blocking queue of elements that are processed as soon as
 * possible after they are added. An instance of this class can be run in its
 * own thread by calling start(). Subsequently, items may be added with
 * addItem(), and will be processed by the process() method defined in the
 * subclass. To signal an instance of this class to stop accepting items into
 * its queue, interrupt() should be called.</br></br>
 * If interrupt() has been called and there are still elements in the queue, an
 * instance of this class will only finish processing the remaining elements if
 * willProcessAfterInterrupt() returns true (the default behavior). This
 * behavior may be changed using setProcessAfterInterrupt()</br></br>
 * Items may be added to the queue before calling start() on an instance of this
 * class. However, any items added to the queue during this time will only be
 * processed once start() has been called.
 */
public abstract class InterruptibleConsumer<E> extends Thread
{
	protected ArrayBlockingQueue<E> queue;
	protected final int queueSize;
	protected boolean willProcessAfterInterrupt;
	
	/**
	 * Creates a new instance with a queue of the given size.
	 * 
	 * @param queueSize
	 *            the size of the queue
	 */
	public InterruptibleConsumer(int queueSize)
	{
		this.queueSize = queueSize;
		queue = new ArrayBlockingQueue<E>(queueSize);
		willProcessAfterInterrupt = true;
	}
	
	/**
	 * Atomically adds an item to the queue, blocking if the queue is full. If
	 * this thread has been interrupted, the item will not be added, no
	 * exception will be thrown, and the thread will be re-interrupted.
	 * 
	 * @param item
	 *            the item to be added
	 */
	public void addItem(E item)
	{
		if (isInterrupted())
		{
			return;
		}
		try
		{
			queue.put(item);
		}
		catch (InterruptedException e)
		{
			// re-interrupt the current thread
			interrupt();
		}
	}
	
	/**
	 * Processes items in the queue until this thread has been interrupted. When
	 * this thread has been interrupted, items may no longer be added to the
	 * queue, and the rest of the elements left in the queue will be removed and
	 * processed.
	 */
	@Override
	public final void run()
	{
		while (!isInterrupted())
		{
			try
			{
				E item = queue.take();
				process(item);
			}
			catch (InterruptedException e)
			{
				// finish processing items in queue if specified
				if (willProcessAfterInterrupt)
				{
					while (!queue.isEmpty())
					{
						process(queue.remove());
					}
				}
				// re-interrupt the current thread
				interrupt();
			}
		}
	}
	
	/**
	 * Determines whether items remaining in the queue after interrupt() has
	 * been called will be processed or not.
	 * 
	 * @param willProcess
	 *            If true, remaining items will be processed after interrupt()
	 *            has been called. If false, remaining items will be ignored
	 *            after interrupt() has been called.
	 */
	public void setProcessAfterInterrupt(boolean willProcess)
	{
		willProcessAfterInterrupt = willProcess;
	}
	
	/**
	 * Returns true if items remaining in the queue after interrupt() has been
	 * called will be processed or not.
	 */
	public boolean willProcessAfterInterrupt()
	{
		return willProcessAfterInterrupt;
	}
	
	/**
	 * Called within the run() method. Override to specify how each element
	 * taken off the queue is processed.
	 * 
	 * @param item
	 *            the item removed from the queue to be processed
	 */
	protected abstract void process(E item);
}