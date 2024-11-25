package br.com.transactionauthorizer

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@SpringBootTest
class TransactionAuthorizerApplicationTests {

	@Autowired
	lateinit var applicationContext: ApplicationContext

	@Test
	fun contextLoads() {
		assertNotNull(applicationContext)
	}

}
