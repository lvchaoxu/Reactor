package reactor.server.multi.threadPool

import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * SubReactor 负责监控读写事件以及具体地读写操作
 */
class SubReactor(val mainReactor: MainReactor) : Runnable {
    val selector: Selector = Selector.open()
    private val registerQueue: Queue<SelectableChannel> = ConcurrentLinkedQueue()

    override fun run() {
        while (!Thread.interrupted()) {
            selector.select()
            processRegisterQueue()
            val keys = selector.selectedKeys()
            val iterator = keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                iterator.remove()
                dispatch(key)
            }
        }
    }

    private fun processRegisterQueue() {
        while (!registerQueue.isEmpty()) {
            val channel = registerQueue.poll()
            channel.register(selector, SelectionKey.OP_READ)
        }
    }

    private fun dispatch(key: SelectionKey) {
        if (key.isReadable) {
            val handler = Handler(this.mainReactor, key)
            handler.handleRead()
        } else if (key.isWritable) {
            val handler = Handler(this.mainReactor, key)
            handler.handleSend()
        }
    }

    fun registerChannel(channel: SelectableChannel) {
        registerQueue.offer(channel)
        // selector在wakeup之后注册才有效
        selector.wakeup()
    }
}