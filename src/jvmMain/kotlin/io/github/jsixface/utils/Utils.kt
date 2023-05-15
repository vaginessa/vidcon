package io.github.jsixface.utils


class Context(val content: MutableMap<String, Any> = mutableMapOf()) : MutableMap<String, Any> by content {
    fun error(e: Exception) {
        content[e.javaClass.simpleName] = e.localizedMessage
    }
}