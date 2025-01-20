package il.ac.technion.cs.sd.books.lib
import com.google.inject.TypeLiteral
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.sd.lib.StorableReviews

class StorageLibraryModule : KotlinModule() {
    override fun configure() {
        bind(object : TypeLiteral<StorageLibrary<StorableReviews>>() {}).to(object : TypeLiteral<StorageLibraryImpl<StorableReviews>>() {})
    }
}