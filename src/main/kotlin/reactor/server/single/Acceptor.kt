package reactor.server.single

import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel

class Acceptor(private val key: SelectionKey) : Runnable {

    override fun run() {
        val serverSocketChannel = key.channel() as ServerSocketChannel
        val socketChannel = serverSocketChannel.accept()
        val selector = key.selector()
        socketChannel.configureBlocking(false)
        socketChannel.register(selector, SelectionKey.OP_READ)
    }
}