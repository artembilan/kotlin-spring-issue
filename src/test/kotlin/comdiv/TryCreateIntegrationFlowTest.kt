package comdiv

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.integration.dsl.StandardIntegrationFlow
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.scheduling.PollerMetadata
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.junit.jupiter.SpringExtension


@Configuration
@EnableIntegration
open class Config {

    @Bean(name = arrayOf(PollerMetadata.DEFAULT_POLLER))
    open fun poller() : PollerMetadata {                               // 11
        return Pollers.fixedDelay(1000).get()
    }
    @Bean  open  fun output() : DirectChannel? {
        return MessageChannels.direct("output").get()
    }
    @Bean
    open fun someFlow() = IntegrationFlows
      .from {  -> GenericMessage("hello world") } //ok
      .transform{ it:String -> "++$it++"} //ok
      .route{s:String->"output"} // not working  Found ambiguous parameter type [interface java.util.function.Function]
      //.route({s:String->"output"},"invoke") // working !!!
      //.route(Function<String, String> { "output" }) //not working !!!  Found ambiguous parameter type [interface java.util.function.Function]
      //.route (Function<String, String> { "output" },"apply") // working

      .get()
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [Config::class],webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TryCreateIntegrationFlowTest {
    @Autowired
    lateinit var someFlow : StandardIntegrationFlow

    @Test
    fun canRunFlow(){
        someFlow.run { println("called")}
    }


}