package reactor

import reactor.client.ReactorClient

fun main(args: Array<String>) {
    val body = "客户端发的测试请求"
    ReactorClient("localhost", 12345).sendMessage(body.toByteArray())
}