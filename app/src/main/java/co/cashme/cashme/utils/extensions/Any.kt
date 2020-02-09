package co.cashme.cashme.utils.extensions

inline fun <reified T> Any.castTo(): T? = this as? T

inline fun <reified T> Any.unsafeCastTo(): T = this as T