package com.example.coroutines_client_test

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

@SpringBootApplication
class CoroutinesClientTestApplication {
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create(
                        // Default has 500 max connections
                        ConnectionProvider.create("test", 10000)
                    )
                )
            )
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<CoroutinesClientTestApplication>(*args)
}

@Component
class Runner(val webClient: WebClient) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println(
            measureTimeMillis {
                val counter = AtomicInteger(0)
                runBlocking {
                    val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                    (1..100).map {
                        launch(dispatcher) {
                            val response = webClient.get()
                                .uri("http://localhost:8000/hello")
                                .retrieve()
                                .awaitBody<String>()
                            println("${counter.incrementAndGet()}: $response")
                        }
                    }.joinAll()
                }
            }
        )
    }
}
