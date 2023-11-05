package reactor.server.single.singleThread

import reactor.server.single.Acceptor
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

/**
 * 单 Reactor 单线程方案
 * 适用于业务处理迅速的方案
 */
class Reactor(private val port: Int) : Runnable {

    private val selector: Selector = Selector.open()
    private val serverSocketChannel: ServerSocketChannel = ServerSocketChannel.open()

    init {
        serverSocketChannel.bind(InetSocketAddress(port))
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
    }

    override fun run() {
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
        if (key.isAcceptable) { // 服务端使用一个线程不断等待客户端的连接到达
            val acceptor = Acceptor(key)
            acceptor.run()
        } else if (key.isReadable) {
            val handler = Handler(this, key)
            handler.handleRead()
        } else if (key.isWritable) {
            val handler = Handler(this, key)
            handler.handleSend()
        }
    }

}