package com.gpf.snapshottestingdemo

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.catalina.authenticator.SingleSignOn
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.reflect.KClass
import no.gjensidige.gpf.snapshot.SnapshotBase as GpfBibliotekerSnapshotBase

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SnapshotTestingDemoApplication::class, SnapshotTestingConfig::class],
    properties = ["spring.main.allow-bean-definition-overriding=true"]
)
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [SingleSignOn::class])
class SnapshotBase(namespace: KClass<*>) : GpfBibliotekerSnapshotBase() {
    val namespace: String = namespace.qualifiedName!!
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var objectMapper: ObjectMapper

    final inline fun <reified T> jsonGetRequest(
        url: String,
        extraHeaders: HttpHeaders = HttpHeaders()
    ): ResponseEntity<T?> {
        return jsonGetRequest<T>(url, objectMapper, restTemplate, extraHeaders).also {
            if (it.statusCode == HttpStatus.FOUND) {
                throw IllegalStateException(
                    "Received 302 HTTP Status. Possible reason is wrong URL in application-github"
                )
            }
        }
    }
}
