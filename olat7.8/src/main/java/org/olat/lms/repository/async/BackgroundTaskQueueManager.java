/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */
package org.olat.lms.repository.async;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.olat.system.commons.manager.BasicManager;
import org.olat.system.logging.log4j.LoggerHelper;

/**
 * FIFO-queue for background job. Application can add 'background-tasks', the background-job checks in a fix interval the queue and executes the pending tasks. If you
 * look for a generic tasks executor see TaskExecutorManager
 * 
 * @author Christian Guretzki
 */
public class BackgroundTaskQueueManager extends BasicManager {
    private static final Logger log = LoggerHelper.getLogger();

    private Queue<BackgroundTask> backgroundTaskQueue;

    private int maxRetry;

    private TaskExecutorThread taskExecutor;

    /**
     * [used by spring]
     */
    private BackgroundTaskQueueManager() {
        backgroundTaskQueue = new ConcurrentLinkedQueue<BackgroundTask>();
        taskExecutor = new TaskExecutorThread(backgroundTaskQueue);
        taskExecutor.setDaemon(true);
        taskExecutor.start();
    }

    public Queue<BackgroundTask> getQueue() {
        return backgroundTaskQueue;
    }

    public void addTask(BackgroundTask task) {
        log.debug("Add task=" + task);
        task.setMaxRetry(maxRetry);
        synchronized (backgroundTaskQueue) {
            backgroundTaskQueue.offer(task);
            backgroundTaskQueue.notifyAll();
        }
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

}
