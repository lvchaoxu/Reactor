package reactor.server

import java.util.concurrent.Callable

/**
 * 只负责业务逻辑
 * decode -> process -> encode
 */
class Processor(private val message: String) : Callable<String> {
    override fun call(): String {
        // 模拟耗时的业务处理逻辑
        Thread.sleep(2000)
        val processedMessage = "Processed message: $message"
        println(processedMessage)
        return processedMessage
    }
}