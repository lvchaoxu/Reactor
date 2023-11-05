package reactor.server.multi.threadPool

import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.Executors

/**
 * Main Reactor 只负责建立连接
 */
class MainReactor(private val port: Int) : Runnable {

    private val selector: Selector = Selector.open()
    private val serverSocketChannel: ServerSocketChannel = ServerSocketChannel.open()
    val executorService = Executors.newFixedThreadPool(20)
    private val subReactor: SubReactor = SubReactor(this)

    init {
        serverSocketChannel.bind(InetSocketAddress(port))
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
    }

    override fun run() {
        // 将SubReactor启动在子线程
        Thread(subReactor).start()
        while (!Thread.interrupted()) {
            selector.select()
            val keys = selector.selectedKeys()
            val iterator = keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                iterator.remove()
                dispatch(key)
            }
        }
    }

    private fun dispatch(key: SelectionKey) {
        if (key.isAcceptable) {
            val acceptor = Acceptor(key, subReactor)
            acceptor.run()
        }
    }

}