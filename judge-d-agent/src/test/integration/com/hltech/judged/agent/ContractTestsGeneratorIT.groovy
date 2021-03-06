package com.hltech.judged.agent

import com.fasterxml.jackson.databind.ObjectMapper
import com.hltech.judged.agent.config.BeanFactory
import com.hltech.pact.gen.PactGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest(classes = BeanFactory)
@ActiveProfiles("test")
@ContextConfiguration
class ContractTestsGeneratorIT extends Specification {

    @Autowired
    ObjectMapper objectMapper

    PactGenerator pactGenerator = new PactGenerator()

    def "should generate pact file"() {
        expect:
        pactGenerator.writePactFiles("com.hltech.judged.agent", "judge-d-agent", objectMapper, new File("target/pacts"))
    }
}
