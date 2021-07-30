package tech.wakame.efficient_survival.util

/**
 *  make pair from key: K to not-null value: V that'll be let to transform
 */
inline fun <K, V : Any> Collection<K>.combineNotNull(crossinline transform: (K) -> V?): Map<K, V> {
    return this.mapNotNull {
        val v = transform(it)
        return@mapNotNull if (v is V) {
            Pair(it, v)
        } else {
            null
        }
    }.toMap()
}
