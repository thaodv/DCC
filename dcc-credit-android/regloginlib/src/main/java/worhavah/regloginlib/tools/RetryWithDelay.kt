package worhavah.regloginlib.tools

import io.reactivex.Flowable
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

/**
 * Created by lulingzhi on 2017/11/15.
 */
class RetryWithDelay(
        private val delayProvider: DelayProvider
) : Function<Flowable<Throwable>, Publisher<Long>> {

    companion object {
        fun createSimple(maxRetries: Int, delay: Long) =
                RetryWithDelay(object : DelayProvider {
                    override val maxRetries: Int = maxRetries

                    override fun retryDelayMillisOf(retryCount: Int): Long = delay
                })

        fun createGrowth(maxRetries: Int, baseDelay: Long, growth: Double = 2.0) =
                RetryWithDelay(object : DelayProvider {
                    override val maxRetries: Int = maxRetries

                    override fun retryDelayMillisOf(retryCount: Int): Long =
                            (baseDelay * Math.pow(growth, (retryCount - 1).toDouble())).toLong()
                })

        fun createBy(vararg delays: Long) =
                RetryWithDelay(object : DelayProvider {
                    override val maxRetries: Int = delays.size

                    override fun retryDelayMillisOf(retryCount: Int): Long = delays[retryCount - 1]
                })
    }

    private var retryCount: Int = 0
    private val maxRetries = delayProvider.maxRetries

    override fun apply(attempts: Flowable<Throwable>): Publisher<Long> {
        return attempts
                .flatMap {
                    val count = ++retryCount
                    if (count < maxRetries) {
                        // When this Observable calls onNext, the original
                        // Observable will be retried (i.e. re-subscribed).
                        Flowable.timer(delayProvider.retryDelayMillisOf(count),
                                TimeUnit.MILLISECONDS)
                    } else Flowable.error(it)

                    // Max retries hit. Just pass the error along.
                }
    }

    interface DelayProvider {
        val maxRetries: Int
        fun retryDelayMillisOf(retryCount: Int): Long
    }
}