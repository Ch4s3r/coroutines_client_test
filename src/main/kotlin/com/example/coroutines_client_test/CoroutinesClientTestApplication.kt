package com.example.coroutines_client_test

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

@SpringBootApplication
class CoroutinesClientTestApplication

fun main(args: Array<String>) {
    runApplication<CoroutinesClientTestApplication>(*args)
}

@Component
class Runner : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println(
            measureTimeMillis {
                runBlocking {
                    val webClient = WebClient.create()
                    val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                    (1..1000).map {
                        launch(dispatcher) {
                            val response = webClient.get()
                                .uri("http://localhost:8000/thing/1")
                                .retrieve()
                                .awaitBody<String>()
                            println("$it: $response")
                        }
                    }.joinAll()
                }
            }
        )
    }
}
