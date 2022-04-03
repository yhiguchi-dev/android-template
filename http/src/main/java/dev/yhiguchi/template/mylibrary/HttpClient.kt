package dev.yhiguchi.template.mylibrary

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

object HttpClient {

  @OptIn(ExperimentalSerializationApi::class)
  suspend inline fun <reified R> get(url: String): HttpResponse<R> = withContext(
    Dispatchers.IO
  ) {
    val httpURLConnection = httpURLConnection(url, HttpMethod.GET)
    resolve(httpURLConnection)
  }

  @OptIn(ExperimentalSerializationApi::class)
  suspend inline fun <reified T, reified R> post(url: String, requestBody: T): HttpResponse<R> =
    withContext(
      Dispatchers.IO
    ) {
      val httpURLConnection = httpURLConnection(url, HttpMethod.POST, requestBody)
      resolve(httpURLConnection)
    }

  fun httpURLConnection(url: String, httpMethod: HttpMethod) =
    (URL(url).openConnection() as HttpURLConnection).also {
      it.connectTimeout = 3000
      it.readTimeout = 3000
      it.requestMethod = httpMethod.name
      it.setRequestProperty("content-type", "application/json")
    }

  @OptIn(ExperimentalSerializationApi::class)
  inline fun <reified T> httpURLConnection(url: String, httpMethod: HttpMethod, requestBody: T) =
    httpURLConnection(url, httpMethod).also {
      it.doOutput = true
      Json.encodeToStream(requestBody, it.outputStream)
    }

  @OptIn(ExperimentalSerializationApi::class)
  inline fun <reified R> resolve(httpURLConnection: HttpURLConnection): HttpResponse<R> =
    when (val code = httpURLConnection.responseCode) {
      in 200..299 -> {
        when (R::class) {
          EmptyResponse::class -> HttpResponse.NoContent(code)
          else -> {
            HttpResponse.Success(code, Json.decodeFromStream(httpURLConnection.inputStream))
          }
        }
      }
      in 400..499 -> {
        val message = httpURLConnection.errorStream.bufferedReader().use {
          it.readText()
        }
        HttpResponse.ClientError(code, message)
      }
      in 500..599 -> {
        val message = httpURLConnection.errorStream.bufferedReader().use {
          it.readText()
        }
        HttpResponse.ServerError(code, message)
      }
      else -> error("unknown http status code : $code")
    }
}

sealed class HttpResponse<out T> {
  @Serializable
  data class Success<T>(val code: Int, val responseBody: T) : HttpResponse<T>()
  data class NoContent(val code: Int) : HttpResponse<Nothing>()
  data class ClientError(val code: Int, val message: String) : HttpResponse<Nothing>()
  data class ServerError(val code: Int, val message: String) : HttpResponse<Nothing>()
}

enum class HttpMethod {
  GET,
  POST
}

object EmptyResponse
