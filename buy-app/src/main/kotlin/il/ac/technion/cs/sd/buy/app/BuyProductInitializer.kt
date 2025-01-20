package il.ac.technion.cs.sd.buy.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.books.lib.StorageLibrary
import il.ac.technion.cs.sd.books.modules.Parser
import il.ac.technion.cs.sd.lib.Storable

interface BuyProductInitializer {
    /** Saves the XML data persistently, so that it could be queried using BuyProductReader */
    suspend fun setupXml(xmlData: String)

    /** Saves the JSON data persistently, so that it could be queried using BuyProductReader */
    suspend fun setupJson(jsonData: String)
}

class BuyProductInitializerImpl<T : Storable> @Inject constructor(
    private val xmlParser: XmlParser<T>,
    private val jsonParser: JsonParser<T>,
    private val storageLib: StorageLibrary<T>
) : BuyProductInitializer {

    /** Saves the XML data persistently, so that it could be queried using BookScoreReader */
    override suspend fun setupXml(xmlData: String) {
        storeData(xmlParser, xmlData)
    }

    /** Saves the JSON data persistently, so that it could be queried using BookScoreReader */
    override suspend fun setupJson(jsonData: String) {
        storeData(jsonParser, jsonData)
    }

    private suspend fun <P : Parser<T>> storeData(parser: P, data: String) {
        val parsedData: T = parser.parse(data)
        storageLib.store(parsedData)
    }
}

