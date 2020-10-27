package com.hltech.judged.server.infrastructure.persistence.contracts

import com.hltech.judged.server.domain.ServiceId
import com.hltech.judged.server.domain.contracts.Capability
import com.hltech.judged.server.domain.contracts.Contract
import com.hltech.judged.server.domain.contracts.Expectation
import com.hltech.judged.server.domain.contracts.ServiceContracts
import com.hltech.judged.server.domain.contracts.ServiceContractsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.NoResultException

import static java.util.function.Function.identity
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static wiremock.org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["management.port=0"])
@ActiveProfiles("test-integration")
@Transactional
class ServiceIdContractsRepositoryImplIT extends Specification {

    @Autowired
    private ServiceContractsRepository repository


    def 'should find all persisted service names'() {
        given:
            def s1 = repository.persist(new ServiceContracts(new ServiceId(randomAlphabetic(10), "1.0"), [], []))
            def s2 = repository.persist(new ServiceContracts(new ServiceId(randomAlphabetic(10), "1.0"), [], []))
        when:
            def serviceNames = repository.getServiceNames()
        then:
            serviceNames.contains(s1.name)
            serviceNames.contains(s2.name)
    }

    def 'should find all persisted versions of a service'() {
        given:
            def serviceName = randomAlphabetic(10)
            def s1 = repository.persist(new ServiceContracts(new ServiceId(serviceName, randomAlphabetic(5)), [], []))
            def s2 = repository.persist(new ServiceContracts(new ServiceId(serviceName, randomAlphabetic(5)), [], []))
        when:
            def serviceContracts = repository.findAllByServiceName(serviceName)
        then:
            serviceContracts.size() == 2
            serviceContracts.contains(s1)
            serviceContracts.contains(s2)
    }

    def 'should find persisted service by name'() {
        given:
            def serviceName = randomAlphabetic(10)
            def s1 = repository.persist(new ServiceContracts(new ServiceId(serviceName, randomAlphabetic(5)), [], []))
        when:
            String service = repository.getService(serviceName)
        then:
            service == s1.name
    }

    def 'should throw exception when service not found by name'() {
        when:
            repository.getService(randomAlphabetic(7))
        then:
            thrown NoResultException
    }


}
