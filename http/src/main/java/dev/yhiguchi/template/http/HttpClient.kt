package dev.yhiguchi.template.http

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

  suspend inline fun <reified R> get(url: String): Response<R> = withContext(
    Dispatchers.IO
  ) {
    val httpURLConnection = HttpURLConnectionCreator.create(url, Method.GET)
    ResponseResolver.resolve(httpURLConnection)
  }

  suspend inline fun <reified T, reified R> post(url: String, requestBody: T): Response<R> =
    withContext(
      Dispatchers.IO
    ) {
      val httpURLConnection = HttpURLConnectionCreator.create(url, Method.POST, requestBody)
      ResponseResolver.resolve(httpURLConnection)
    }

  suspend inline fun <reified T> postNoBody(url: String, requestBody: T): Response<Unit> =
    withContext(
      Dispatchers.IO
    ) {
      val httpURLConnection = HttpURLConnectionCreator.create(url, Method.POST, requestBody)
      ResponseResolver.resolveNoBody(httpURLConnection)
    }
}

sealed class Response<out T> {
  @Serializable
  data class Success<T>(val code: Int, val responseBody: T) : Response<T>()
  data class ClientError(val code: Int, val message: String) : Response<Nothing>()
  data class ServerError(val code: Int, val message: String) : Response<Nothing>()
}

enum class Method {
  GET,
  POST
}

object HttpURLConnectionCreator {
  fun create(url: String, method: Method) =
    (URL(url).openConnection() as HttpURLConnection).also {
      it.connectTimeout = 3000
      it.readTimeout = 3000
      it.requestMethod = method.name
      it.setRequestProperty("content-type", "application/json")
    }

  @OptIn(ExperimentalSerializationApi::class)
  inline fun <reified T> create(url: String, method: Method, requestBody: T) =
    create(url, method).also {
      it.doOutput = true
      Json.encodeToStream(requestBody, it.outputStream)
    }
}

object ResponseResolver {
  @OptIn(ExperimentalSerializationApi::class)
  inline fun <reified R> resolve(httpURLConnection: HttpURLConnection): Response<R> =
    when (val code = httpURLConnection.responseCode) {
      in 200..299 -> Response.Success(code, Json.decodeFromStream(httpURLConnection.inputStream))
      else -> resolveError(httpURLConnection)
    }

  fun resolveNoBody(httpURLConnection: HttpURLConnection): Response<Unit> =
    when (val code = httpURLConnection.responseCode) {
      in 200..299 -> Response.Success(code, Unit)
      else -> resolveError(httpURLConnection)
    }

  fun <R> resolveError(httpURLConnection: HttpURLConnection): Response<R> {
    val message = httpURLConnection.errorStream.bufferedReader().use {
      it.readText()
    }
    return when (val code = httpURLConnection.responseCode) {
      in 400..499 -> Response.ClientError(code, message)
      in 500..599 -> Response.ServerError(code, message)
      else -> error("unknown http status code : $code")
    }
  }
}
