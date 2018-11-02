package pt.lsts.neptus.plugins.noc.transports;

import pt.lsts.neptus.comm.transports.DeliveryListener;
import pt.lsts.neptus.plugins.noc.transports.messages.NocMessage;

import java.util.concurrent.Callable;

public class NocMessageDeliveryListener implements Callable<DeliveryListener.ResultEnum> {
    public DeliveryListener.ResultEnum result = null;
    private long timeoutMillis = 10000;
    private long start;

    /**
     * @param message
     * @param string
     */
    public void deliveryUncertain(NocMessage message, Object msg)
    {}

    public NocMessageDeliveryListener(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.start = System.currentTimeMillis();
    }

    /**
     * Message has been successfully delivered to target
     * @param message The message that was sent for reliable delivery
     */
    public void deliverySuccess(NocMessage message) {
        result =  DeliveryListener.ResultEnum.Success;
    }

    /**
     * Delivery time out after some time. End point may be disconnected or network conditions are poor
     * @param message The message that was sent for reliable delivery
     */
    public void deliveryTimeOut(NocMessage message) {
        result =  DeliveryListener.ResultEnum.TimeOut;
    }

    /**
     * Unable to reach end point. The end point may have disconnected or destination is invalid.
     * @param message The message that was sent for reliable delivery
     */
    public void deliveryUnreacheable(NocMessage message) {
        result =  DeliveryListener.ResultEnum.Unreacheable;
    }

    /**
     * Unexpected error while trying to deliver message
     * @param message The message that was sent for reliable delivery
     * @param error The error that was found or returned by the end point.
     */
    public void deliveryError(NocMessage message, Object error) {
        result =  DeliveryListener.ResultEnum.Error;
    }


    @Override
    public DeliveryListener.ResultEnum call() throws Exception {
        while (true) {
            synchronized (this) {
                if (result != null) {
                    return result;
                }
                if (System.currentTimeMillis() - start > timeoutMillis) {
                    return DeliveryListener.ResultEnum.TimeOut;
                }
            }
            Thread.sleep(100);
        }
    }
}
