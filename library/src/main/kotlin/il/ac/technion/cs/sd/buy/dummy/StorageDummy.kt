package il.ac.technion.cs.sd.books.dummy

object StorageDummyFiles{
    private val storages = mutableMapOf<String, StorageDummy>()

    fun getOrCreateStorage(filename: String): StorageDummy {
        return storages.getOrPut(filename) { StorageDummy(filename) }
    }

    fun clearAll() {
        storages.values.forEach { it.clear() }
    }

    /** Removes storage for a specific file */
    fun clearStorage(filename: String) {
        storages.remove(filename)?.clear()
    }
}
class StorageDummy(private val filename: String) {


    private val storage = mutableListOf<String>()
    fun append(item: String) {
        storage.add(item)
    }
    /**number of lines inserted*/
    fun size(): Int {
        return storage.size
    }

    /** Returns the item at the specified index-line */
    fun get(index: Int): String? {
        return if (index in 0 until storage.size) {
            storage[index]
        } else {
            null
        }
    }
    /** clears the collection - to use between tests */
    fun clear() {
        storage.clear()
    }
}
