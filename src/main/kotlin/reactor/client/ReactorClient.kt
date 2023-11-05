package reactor.client

import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

class ReactorClient(val host: String, val port: Int) {
    private val channel = SocketChannel.open()

    fun sendMessage(body: ByteArray) {
        channel.socket().soTimeout = 60000
        channel.connect(InetSocketAddress(host, port))

        sendRequest(channel, body)

        val bodyBuffer = ByteBuffer.allocate(1024)
        read(channel, bodyBuffer)
        println("客户端收到响应内容 ${bodyBuffer.array().toString(Charset.defaultCharset())}")
        channel.close()
    }

    fun sendRequest(channel: SocketChannel, body: ByteArray) {
        channel.write(ByteBuffer.wrap(body))
    }


    fun read(channel: SocketChannel, buffer: ByteBuffer) {
        val r = channel.read(buffer)
        if (r == -1) {
            throw IOException("end of stream when reading header")
        }
    }
}