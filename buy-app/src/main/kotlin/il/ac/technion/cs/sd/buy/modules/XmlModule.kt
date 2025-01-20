package il.ac.technion.cs.sd.books.modules

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Provides
import com.google.inject.TypeLiteral
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.sd.lib.StorableReviews
import org.simpleframework.xml.core.Persister

class XmlModule : KotlinModule() {
    override fun configure() {
        bind(object : TypeLiteral<Parser<StorableReviews>>() {}).toProvider(StorableReviewsParserProvider::class.java)
        bind(object : TypeLiteral<Parser<XmlRoot>>() {}).to(object : TypeLiteral<XmlParser<XmlRoot>>() {})
    }

    @Provides
    fun providePersister(): Persister {
        return Persister()
    }

    @Provides
    fun provideXmlParser(persister: Persister): XmlParser<XmlRoot> {
        return XmlParser(persister, XmlRoot::class.java)
    }
}

class StorableReviewsParserProvider @Inject constructor(
    private val xmlParser: XmlParser<XmlRoot>
) : Provider<Parser<StorableReviews>> {
    override fun get(): Parser<StorableReviews> {
        return object : Parser<StorableReviews> {
            override fun parse(xml: String): StorableReviews {
                return xmlParser.parse(xml)
            }
        }
    }
}
