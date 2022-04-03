package dev.yhiguchi.template.mylibrary

import android.content.Context
import java.net.HttpURLConnection
import java.net.URL
import org.chromium.net.CronetEngine

object HttpClientCreator {

  fun create(context: Context): HttpClient {
    val engine = CronetEngine.Builder(context)
      .enableHttp2(true)
      .enableQuic(true)
      .build() ?: error("error")
    URL.setURLStreamHandlerFactory(engine.createURLStreamHandlerFactory())
    val httpConnector = object : HttpConnector {
      override fun connect(url: String): HttpURLConnection =
        (engine.openConnection(URL(url)) as HttpURLConnection)
    }
    return HttpClient(httpConnector)
  }
}
