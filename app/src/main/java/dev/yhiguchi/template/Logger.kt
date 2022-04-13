package dev.yhiguchi.template

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

object MyLogger {
  private val logger = when (BuildType.of(BuildConfig.BUILD_TYPE)) {
    BuildType.DEBUG -> {
      AndroidLogger()
    }
    BuildType.RELEASE -> {
      CrashlyticsLogger()
    }
  }

  fun info(message: String) {
    logger.info(message)
  }
}

enum class BuildType {
  DEBUG,
  RELEASE;

  companion object {
    fun of(type: String) = valueOf(type.uppercase())
  }
}

interface Loggable {
  fun info(message: String)
}

class AndroidLogger : Loggable {
  override fun info(message: String) {
    Log.i("", message)
  }
}

class CrashlyticsLogger : Loggable {
  override fun info(message: String) {
    FirebaseCrashlytics.getInstance().log(message)
  }
}
