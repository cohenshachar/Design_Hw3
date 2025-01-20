package il.ac.technion.cs.sd.books.modules
import il.ac.technion.cs.sd.lib.StorableReviews
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Commit
import org.simpleframework.xml.core.Persister
import com.google.inject.Inject
import il.ac.technion.cs.sd.lib.Storable


interface Parser<T : Storable> {
    fun parse(dataString: String): T
}

class XmlParser<T : StorableReviews> @Inject constructor(
    private val serializer: Persister,
    private val clazz: Class<T>
) : Parser<T> {
    override fun parse(dataString: String): T {
        return try {
            if (dataString.isBlank()) {
                clazz.getDeclaredConstructor().newInstance() // Return an empty instance of T as a default value
            } else {
                serializer.read(clazz, dataString)
            }
        } catch (e: Exception) {
            println("Error parsing XML: ${e.message}")
            clazz.getDeclaredConstructor().newInstance() // Return an empty instance of T as a default value
        }
    }
}

class JsonParser<T : StorableReviews> @Inject constructor(
    private val serializer: Persister,
    private val clazz: Class<T>
) : Parser<T> {
    override fun parse(dataString: String): T {
        return try {
            if (dataString.isBlank()) {
                clazz.getDeclaredConstructor().newInstance() // Return an empty instance of T as a default value
            } else {
                serializer.read(clazz, dataString)
            }
        } catch (e: Exception) {
            println("Error parsing XML: ${e.message}")
            clazz.getDeclaredConstructor().newInstance() // Return an empty instance of T as a default value
        }
    }
}


@Root(name = "Root")
data class XmlRoot(
    @field:ElementList(name = "Reviewer", inline = true)
    var reviewers: MutableList<Reviewer> = mutableListOf(),
    var books: MutableList<Book> = mutableListOf()
) : StorableReviews {
    @Commit
    fun afterDeserialization() {
        unifyReviews()
    }
    private fun unifyReviews() {
        val reviewerMap = mutableMapOf<String, Reviewer>()
        val booksMap = mutableMapOf<String, Book>()
        reviewers.asReversed().asSequence().forEach { reviewer ->
            val uniqueReviewer = reviewerMap.getOrPut(reviewer.id) { Reviewer(id = reviewer.id) }
            reviewer.reviews.asReversed().asSequence().forEach { review ->
                if (uniqueReviewer.reviews.none { it.id == review.id }) {
                    val uniqueBook = booksMap.getOrPut(review.id) { Book(id = review.id, avgScore = 0.0, reviews = mutableMapOf()) }
                    uniqueBook.reviews.putIfAbsent(reviewer.id, review)
                    uniqueBook.avgScore += review.score
                    uniqueReviewer.reviews.add(review)
                    uniqueReviewer.avgScore += review.score
                }
            }
        }
        reviewers = reviewerMap.values.toMutableList()
        reviewers.asSequence().forEach { it.avgScore /= it.reviews.size }
        books = booksMap.values.toMutableList()
        books.asSequence().forEach { it.avgScore /= it.reviews.size }
    }

    override fun getReviewersAvgScore(): Map<String, String> {
        return reviewers.associate { it.id to it.avgScore.toString() }
    }

    override fun getReviewersReviews(): Map<String, List<String>> {
        return reviewers.associate { reviewer ->
            reviewer.id to reviewer.reviews.asSequence().map { it.toString() }.toList()
        }
    }
    override fun getBooksAvgScore(): Map<String, String> {
        return books.associate { it.id to it.avgScore.toString() }
    }
    override fun getBooksReviewerScore(): Map<String, List<Pair<String, String>>> {
        return books.associate { book ->
            book.id to book.reviews.asSequence().map { (reviewerId, review) ->
                reviewerId to review.score.toString()
            }.toList()
        }
    }
}

@Root(name = "Reviewer")
data class Reviewer(
    @field:Attribute(name = "Id", required = false)
    var id: String = "",

    @field:ElementList(name = "Review", inline = true)
    var reviews: MutableList<Review> = mutableListOf(),

    var avgScore: Double = 0.0
)

@Root(name = "Review")
data class Review(
    @field:Element(name = "Id")
    var id: String = "",

    @field:Element(name = "Score")
    var score: Int = 0
)

data class Book(
    var id: String = "",
    var avgScore: Double = 0.0,
    var reviews: MutableMap<String, Review>
)
