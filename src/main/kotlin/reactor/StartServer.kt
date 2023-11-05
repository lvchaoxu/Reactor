package reactor

import reactor.server.multi.threadPool.MainReactor

fun main() {
    MainReactor(12345).run()
}