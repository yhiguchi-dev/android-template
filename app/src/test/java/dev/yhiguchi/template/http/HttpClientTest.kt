package dev.yhiguchi.template.http

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.Serializable

class HttpClientTest : StringSpec({
  val wireMockServer = WireMockServer(9000)
  listener(WireMockListener(wireMockServer, ListenerMode.PER_SPEC))

  val url = "http://localhost:9000/customers/123"

  afterTest { wireMockServer.resetAll() }

  "get method with success" {
    wireMockServer.stubGetWithOk()

    val response = HttpClient.get<TestResponse>(url)
    response.shouldBeInstanceOf<Response.Success<TestResponse>>()
    response.code shouldBe 200
    response.responseBody.customer shouldBe "123"
  }

  "get method with bad request" {
    wireMockServer.stubGetWithBadRequest()

    val response = HttpClient.get<TestResponse>(url)
    response.shouldBeInstanceOf<Response.ClientError>()
    response.code shouldBe 400
    response.message shouldBe ""
  }

  "post method with success" {
    wireMockServer.stubPostWithOk()

    val response = HttpClient.post<TestRequest, TestResponse>(url, TestRequest("test"))
    response.shouldBeInstanceOf<Response.Success<TestResponse>>()
    response.code shouldBe 200
    response.responseBody.customer shouldBe "1234"
  }

  "post method with no content" {
    wireMockServer.stubPostWithNoContent()

    val response = HttpClient.postNoBody(url, TestRequest("test"))
    response.shouldBeInstanceOf<Response.Success<Unit>>()
    response.code shouldBe 200
  }

  "post method with bad request" {
    wireMockServer.stubPostWithBadRequest()

    val response = HttpClient.postNoBody(url, TestRequest("test"))
    response.shouldBeInstanceOf<Response.ClientError>()
    response.code shouldBe 400
    response.message shouldBe """{
  "error": "invalid"
}"""
  }
})

@Serializable
data class TestRequest(val value: String)

@Serializable
data class TestResponse(val customer: String)

fun WireMockServer.stubGetWithOk() {
  this.stubFor(
    WireMock.get(WireMock.urlEqualTo("/customers/123"))
      .willReturn(
        WireMock.ok(
          """
          {
            "customer": "123"
          }
          """.trimIndent()
        )
      )
  )
}

fun WireMockServer.stubGetWithBadRequest() {
  this.stubFor(
    WireMock.get(WireMock.urlEqualTo("/customers/123"))
      .willReturn(
        WireMock.badRequest()
      )
  )
}

fun WireMockServer.stubPostWithOk() {
  this.stubFor(
    WireMock.post(WireMock.urlEqualTo("/customers/123"))
      .willReturn(
        WireMock.ok(
          """
          {
            "customer": "1234"
          }
          """.trimIndent()
        )
      )
  )
}

fun WireMockServer.stubPostWithNoContent() {
  this.stubFor(
    WireMock.post(WireMock.urlEqualTo("/customers/123"))
      .willReturn(
        WireMock.ok()
      )
  )
}

fun WireMockServer.stubPostWithBadRequest() {
  this.stubFor(
    WireMock.post(WireMock.urlEqualTo("/customers/123"))
      .willReturn(
        WireMock.badRequest().withBody(
          """
          {
            "error": "invalid"
          }
          """.trimIndent()
        )
      )
  )
}
