package com.song.trust.utils

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by chensongsong on 2021/11/19.
 */
class ThreadPoolUtils {
    /**
     * 开启runnable
     *
     * @param runnable
     */
    fun execute(runnable: Runnable?) {
        try {
            if (runnable != null) {
                fixedThreadPool!!.execute(runnable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 同步获取通知
     *
     * @param task
     * @param <T>
     * @return
    </T> */
    fun <T> submit(task: Callable<T>?): Future<T>? {
        try {
            if (task != null) {
                return fixedThreadPool!!.submit(task)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 执行计时任务
     */
    fun executeScheduled(runnable: Runnable?, initialDelay: Long, period: Long, unit: TimeUnit?) {
        try {
            if (runnable != null) {
                executorService!!.scheduleAtFixedRate(runnable, initialDelay, period, unit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile
        private var singleton: ThreadPoolUtils? = null

        @Volatile
        private var fixedThreadPool: ExecutorService? = null

        @Volatile
        private var executorService: ScheduledExecutorService? = null
        private val threadFactory: ThreadFactory = object : ThreadFactory {
            private val mCount = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "APP_ENV_Task #" + mCount.getAndIncrement())
            }
        }

        /**
         * 初始化线程池
         *
         * @return
         */
        val instance: ThreadPoolUtils?
            get() {
                if (singleton == null) {
                    synchronized(ThreadPoolUtils::class.java) {
                        if (singleton == null) {
                            singleton = ThreadPoolUtils()
                            fixedThreadPool = Executors.newFixedThreadPool(3, threadFactory)
                            executorService = Executors.newScheduledThreadPool(3, threadFactory)
                        }
                    }
                }
                return singleton
            }
    }
}