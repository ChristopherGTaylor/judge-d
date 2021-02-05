package com.hltech.judged.server

import io.restassured.RestAssured
import org.springframework.boot.web.server.LocalServerPort

@FunctionalTest
class ContractsControllerFT extends PostgresDatabaseSpecification {

    @LocalServerPort
    int serverPort

    def 'should create and save service contracts'() {
        when: 'rest validatePacts url is hit'
            RestAssured.given()
                .port(serverPort)
                .contentType("application/json")
                .body("""
                    {
                    "capabilities":
                        {
                        "protocol":{"value":"capabilities","mimeType":"application/json"}},
                        "expectations":{"test-provider":{"protocol":{"value":"expectations","mimeType":"application/json"}}
                        }
                    }
                """)
                .when()
                    .post("/contracts/services/test-provider/versions/'1.0'")
                .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .extract().body().jsonPath().getMap('$')

        then: 'capabilities are saved in db'
             def capabilities = sql.rows("select * from capabilities" as String)
            capabilities.size() == 1
            capabilities[0]['service_name'] == 'test-provider'
            capabilities[0]['service_version'] == "'1.0'"
    }

}
