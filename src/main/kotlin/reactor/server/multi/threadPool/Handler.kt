package reactor.server.multi.threadPool

import reactor.server.Processor
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.nio.charset.Charset
import java.util.concurrent.Future

/**
 * o
 */
class Handler(private val reactor: MainReactor, private val key: SelectionKey) {

    fun handleRead() {
        val socketChannel = key.channel() as SocketChannel
        val buffer = ByteBuffer.allocate(1024)
        val bytesRead = socketChannel.read(buffer)

        if (bytesRead > 0) {
            buffer.flip()
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            val message = data.toString(Charset.defaultCharset())
            // 将业务逻辑放进线程池
            val processor = Processor(message)
            val future = reactor.executorService.submit(processor)
            key.attach(future)
            key.interestOps(SelectionKey.OP_WRITE)
        }
        if (bytesRead == -1) {
            socketChannel.close()
        }
    }

    fun handleSend() {
        val future = key.attachment() as Future<String>
        if (future.isDone) {
            val response = future.get()
            val responseBuffer = ByteBuffer.wrap(response.toByteArray(Charset.defaultCharset()))
            val socketChannel = key.channel() as SocketChannel
            socketChannel.write(responseBuffer)
            key.interestOps(SelectionKey.OP_READ)
        }
    }
}