InterruptibleConsumer is a no-hassle, extensible consumer class written in Java which takes care of all the synchronization details you don't care about. 

To make use of it:

- Read the documentation for the class
- Subclass it
- Override the process() method
- Create an instance of it
- Call start() to run it in its own thread
- Call addItem() to add items to its queue
- Items will be removed as soon as possible and processed using the process() method you overrode
- Call interrupt() when you want it to stop processing items
- If the consumer still has items in its queue after it has been interrupted, by default it will finish processing the items in its queue, then exit
- If you do not want the consumer to finish processing the rest of the items in its queue after it is interrupted, call setProcessAfterInterrupt(false)