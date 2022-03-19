package dev.yhiguchi.template.mylibrary

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

    val response = HttpClient.get<Response>(url)
    response.shouldBeInstanceOf<HttpResponse.Success<Response>>()
    response.code shouldBe 200
    response.requestBody.customer shouldBe "123"
  }

  "get method with bad request" {
    wireMockServer.stubGetWithBadRequest()

    val response = HttpClient.get<Response>(url)
    response.shouldBeInstanceOf<HttpResponse.ClientError>()
    response.code shouldBe 400
    response.message shouldBe ""
  }

  "post method with success" {
    wireMockServer.stubPostWithOk()

    val response = HttpClient.post<Request, Response>(url, Request("test"))
    response.shouldBeInstanceOf<HttpResponse.Success<Response>>()
    response.code shouldBe 200
    response.requestBody.customer shouldBe "1234"
  }

  "post method with no content" {
    wireMockServer.stubPostWithNoContent()

    val response = HttpClient.post<Request, EmptyResponse>(url, Request("test"))
    response.shouldBeInstanceOf<HttpResponse.NoContent>()
    response.code shouldBe 200
  }

  "post method with bad request" {
    wireMockServer.stubPostWithBadRequest()

    val response = HttpClient.post<Request, EmptyResponse>(url, Request("test"))
    response.shouldBeInstanceOf<HttpResponse.ClientError>()
    response.code shouldBe 400
    response.message shouldBe """{
  "error": "invalid"
}"""
  }
})

@Serializable
data class Request(val value: String)

@Serializable
data class Response(val customer: String)

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
