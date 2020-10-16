package io.github.natsusai.utils.concurrent

import java.util.*
import java.util.concurrent.*

/**
 * 异步工具类
 *
 * @author Kurenai
 * @since 2020-09-18 10:10
 */
class AsyncUtils {

    companion object {
        /**
         * 创建一个新线程执行传入的方法并在指定时间内获取返回值
         *
         * @param timeout  超时时间
         * @param timeUnit 时间单位
         * @param task     被调用的方法
         * @param <R>      返回类型
         * @return 返回被调用方法的返回值
         * @throws Exception 执行被调用方法异常
        </R> */
        @Throws(Exception::class)
        fun <R> submit(timeout: Long, timeUnit: TimeUnit, task: Callable<R>): R? {
            val future = FutureTask { doTask(task) }
            Thread(future).start()
            return getResult(future, timeout, timeUnit)
        }

        /**
         * 调用线程池执行传入的方法并在指定时间内获取返回值
         *
         * @param timeout  超时时间
         * @param timeUnit 时间单位
         * @param executor 线程池对象
         * @param task     被调用的方法
         * @param <R>      返回类型
         * @return 返回被调用方法的返回值
         * @throws Exception 执行被调用方法异常
        </R> */
        @Throws(Exception::class)
        fun <R> submit(timeout: Long, timeUnit: TimeUnit, executor: ExecutorService, task: Callable<R>): R? {
            val future = executor.submit<R> { doTask(task) }
            return getResult(future, timeout, timeUnit)
        }

        /**
         * 创建一个新线程执行传入的方法并在指定时间等待执行完成
         *
         * @param timeout  超时时间
         * @param timeUnit 时间单位
         * @param task     被调用的方法
         * @throws Exception 执行被调用方法异常
         */
        @Throws(Exception::class)
        fun execute(timeout: Long, timeUnit: TimeUnit, task: Task) {
            val future = FutureTask { doTask(task) }
            Thread(future).start()
            getResult(future, timeout, timeUnit)
        }

        /**
         * 调用线程池执行传入的方法并在指定时间等待执行完成
         *
         * @param timeout  超时时间
         * @param timeUnit 时间单位
         * @param executor 线程池对象
         * @param task     被调用的方法
         * @throws Exception 执行被调用方法异常
         */
        @Throws(Exception::class)
        fun execute(timeout: Long, timeUnit: TimeUnit, executor: ExecutorService?, task: Task) {
            assert(executor != null)
            val future = executor!!.submit<Any?> { doTask(task) }
            getResult(future, timeout, timeUnit)
        }

        /**
         * 获取结果
         *
         * @param future   Future
         * @param timeout  超时时间
         * @param timeUnit 超时时间单位
         * @return 返回执行结果
         * @throws Exception 执行任务当中出现异常
         */
        @Throws(Exception::class)
        private fun <R> getResult(future: Future<R>, timeout: Long, timeUnit: TimeUnit): R? {
            return try {
                future[timeout, timeUnit]
            } catch (e: TimeoutException) {
                null
            }
        }

        /**
         * 执行任务
         *
         * @param task 任务
         * @return 异常或是执行结果对象
         */
        private fun <R> doTask(task: Callable<R>): R {
            return task.call()
        }

        /**
         * 执行任务
         *
         * @param task 任务
         * @return 异常对象
         */
        private fun doTask(task: Task): Any? {
            task.run()
            return null
        }

        fun interface Task {
            @Throws(Exception::class)
            fun run()
        }
    }
}