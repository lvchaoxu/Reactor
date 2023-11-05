package reactor.server.multi.threadPool

import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel

class Acceptor(private val key: SelectionKey, private val subReactor: SubReactor) : Runnable {

    override fun run() {
        val serverSocketChannel = key.channel() as ServerSocketChannel
        val socketChannel = serverSocketChannel.accept()
        val selector = subReactor.selector
        socketChannel.configureBlocking(false)
        subReactor.registerChannel(socketChannel)
    }
}