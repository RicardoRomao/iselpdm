package asyncDispatch;

/**
 * Code taken from 
 */
/**
 * Singleton class that performs asynchronous execution requests.
 */
public final class AsyncDispatcher implements Runnable {

    /**
     * Interface that specifies the contract to be supported by asynchronous
     * execution request items.
     */
    public static interface IDelegate {

        /**
         * Method that contains the code to be asynchronously executed.
         *
         * @param params The asynchronously executed method parameters.
         * @return The asynchronously executed method result.
         */
        Object invoke(Object[] params);
    }

    /**
     * Interface that specifies the contract to be supported by asynchronous
     * execution completion notification callbacks.
     */
    public static interface ICallback {

        /**
         * Method that contains the asynchronous execution completion notification
         * code.
         *
         * @param result The asynchronously executed method result
         */
        void invoke(Object result);
    }
    /**
     * Singleton instance.
     */
    private static final AsyncDispatcher instance = new AsyncDispatcher();
    /**
     * Internal thread used to perform asynchronous execution requests.
     */
    private final Thread dispatcher;
    /**
     * The pending asynchronous execution request. If null, no request is pending.
     */
    private IDelegate currentExecutionItem;
    /**
     * The pending asynchronous execution request parameters.
     */
    private Object[] currentExecutionItemParams;
    /**
     * The pending asynchronous execution request callback for completion notification.
     */
    private ICallback currentExecutionItemCallback;

    /**
     * Initiates the <code>AsyncDispatcher</code> instance.
     */
    private AsyncDispatcher() {
        currentExecutionItem = null;
        dispatcher = new Thread(this);
        dispatcher.start();
    }

    /**
     * Helper method that schedules the given request for execution.
     *
     * @param request The requested execution item.
     * @param requestParams The requested execution item's parameters.
     * @param requestCompletionCallback The requested execution item's completion notification callback.
     */
    private void scheduleRequest(IDelegate request, Object[] requestParams, ICallback requestCompletionCallback) {
        currentExecutionItem = request;
        currentExecutionItemParams = requestParams;
        currentExecutionItemCallback = requestCompletionCallback;
        // Signal internal thread
        notifyAll();
    }

    /**
     * Method that contains the code to be executed by the internal dispatcher thread.
     *
     * Note. Usually, and for code robustness, this method should not be public but, this
     * way, there is no need to define yet another class (reducing footprint and working set).
     */
    public void run() {
        try {
            while (true) {
                IDelegate executionItem = null;
                Object[] executionItemParams = null;
                ICallback executionItemCallback = null;
                synchronized (this) {
                    while (currentExecutionItem == null) {
                        wait();
                    }
                    executionItem = currentExecutionItem;
                    executionItemParams = currentExecutionItemParams;
                    executionItemCallback = currentExecutionItemCallback;
                    // Signal waiting threads
                    currentExecutionItem = null;
                    notifyAll();
                }

                // Dispatch request
                Object result = executionItem.invoke(executionItemParams);
                // Signal completion (if required)
                if (executionItemCallback != null) {
                    executionItemCallback.invoke(result);
                }
            }
        } catch (InterruptedException ie) {
            // Thread termination signaled (used in dispose)
        }
    }

    /**
     * Accessor method for getting the singleton instance.
     *
     * @return The singleton instance.
     */
    public static AsyncDispatcher getInstance() {
        return instance;
    }

    /**
     * Method that issues an asynchronous execution request with the given parameters. Completion notification
     * is signaled by calling the given callback. The callback execution is performed by the internal thread.
     *
     * @param executionItem The requested execution item.
     * @param params The requested execution item's parameters.
     * @param callback The requested execution item's completion notification callback. If <code>null</code>
     * then no completion notification is performed. The callback will be executed by the internal thread.
     * @param wait Flag indicating whether the caller wishes to wait for the dispatcher thread to be available.
     * Note that the thread may be servicing a previously made request.
     * @return <code>true</code> if the request was successfully scheduled for servicing, <code>false</code> otherwise.
     * The return value will always be <code>true</code> if the caller specifies that it is willing to wait for
     * request scheduling.
     * @throws InterruptedException If the calling thread is interrupted.
     */
    public boolean dispatch(IDelegate executionItem, Object[] params, ICallback callback, boolean wait)
            throws InterruptedException {
        synchronized (this) {
            // No request is being serviced. Schedule current one.
            if (currentExecutionItem == null) {
                scheduleRequest(executionItem, params, callback);
                return true;
            }

            // A request is being serviced.

            // Don't care to wait.
            if (!wait) {
                return false;
            }

            // Wait for pending request service completion.
            while (currentExecutionItem != null) {
                wait();
            }

            // No request is being serviced. Schedule current one.
            scheduleRequest(executionItem, params, callback);
            return true;
        }
    }

    /**
     * Disposes the <code>AsyncDispatcher</code> instance. Once called, the	instance
     * no longer can be used.
     */
    public void dispose() {
        // Terminate internal thread
        dispatcher.interrupt();
    }
}
