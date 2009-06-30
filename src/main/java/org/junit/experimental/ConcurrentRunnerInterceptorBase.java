package org.junit.experimental;

import java.util.concurrent.Future;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kristian
 * Date: Jun 30, 2009
 * Time: 6:54:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ConcurrentRunnerInterceptorBase {
    public abstract void done() throws InterruptedException;
}
