package reactor.server.single.singleThread

import reactor.server.Processor
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

class Handler(private val reactor: Reactor, private val key: SelectionKey) {

    fun handleRead() {
        val socketChannel = key.channel() as SocketChannel
        val buffer = ByteBuffer.allocate(1024)
        val bytesRead = socketChannel.read(buffer)

        if (bytesRead > 0) {
            buffer.flip()
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            val message = data.toString(Charset.defaultCharset())
            val processor = Processor(message)
            val response = processor.call()
            key.attach(response)
            key.interestOps(SelectionKey.OP_WRITE)
        }
        if (bytesRead == -1) {
            socketChannel.close()
        }
    }

    fun handleSend() {
        val response = key.attachment() as String
        val responseBuffer = ByteBuffer.wrap(response.toByteArray(Charset.defaultCharset()))
        val socketChannel = key.channel() as SocketChannel
        socketChannel.write(responseBuffer)
        key.interestOps(SelectionKey.OP_READ)
    }
}