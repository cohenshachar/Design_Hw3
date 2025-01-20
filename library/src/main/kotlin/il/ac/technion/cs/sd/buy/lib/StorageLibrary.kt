package il.ac.technion.cs.sd.books.lib
import com.google.inject.Inject
import il.ac.technion.cs.sd.books.dummy.StorageDummyFiles
import il.ac.technion.cs.sd.books.external.LineStorageFactory
import il.ac.technion.cs.sd.books.external.LineStorage
import il.ac.technion.cs.sd.lib.Storable
import il.ac.technion.cs.sd.lib.StorableReviews

/**
 * Implement your library here. Feel free to change the class name,
 * but note that if you choose to change the class name,
 * you will need to update the import statements in GradesInitializer.kt
 * and in GradesReader.kt.
 */

interface StorageLibrary< T : Storable>{

    fun store(itemsContainer : T)
    fun hasReviewedBook(idReviewer: String,idBook: String): Boolean
    fun getBookReviewScoreBy(idReviewer: String,idBook: String):String?
    fun getAllBooksReviewedBy(idReviewer: String): String?
    fun getAllBooksReviewedByWithScore(idReviewer: String): String?
    fun getBookAvgScore(idBook: String):String?
    fun getReviewerAvgScore(idReviewer: String):String?
    fun getAllReviewersOfBook(idBook: String):String?
    fun getAllReviewersOfBookWithScores(idBook: String):String?
    fun clearStorage()
}

class StorageLibraryImpl<T:StorableReviews> @Inject constructor(
    private val lineStorageFactory: LineStorageFactory
) : StorageLibrary<T> {

        override fun store(itemsContainer: T) {
            // openning the file to insert into ..?
            // reviewer first


            val gradeStorage_reviewers_and_books = lineStorageFactory.open("reviewers_and_books")
            val gradeStorage_reviewer_first = lineStorageFactory.open("grades_that_reviewers_gave_books")
            val booksReviewerScore: Map<String, List<Pair<String, String>>> = itemsContainer.getBooksReviewerScore()
            val transformedAndSorted = booksReviewerScore
                .flatMap { (reviewerId, bookScores) ->
                    bookScores.map { (bookId, grade) ->
                        "$reviewerId,$bookId" to grade
                    }
                }
                .toMap() // Convert the list of pairs to a map
                .toSortedMap() // Sort the map by the key

            transformedAndSorted.forEach { (key, value) ->

                gradeStorage_reviewer_first.appendLine("$key")
                gradeStorage_reviewers_and_books.appendLine("$key,$value")
            }


            val reviewersAvgScore: Map<String, String> = itemsContainer.getReviewersAvgScore()

            val reviewerAverages: Map<String, Double> = reviewersAvgScore
                .mapValues { entry -> entry.value.toDoubleOrNull() ?: 11.0 } // Convert to Double
                .toSortedMap()


            //inserting averages
            val avgs_file_reviewers_ids = lineStorageFactory.open("averages_of_reviewers_ids")
            val avgs_file_reviewers = lineStorageFactory.open("averages_of_reviewers")
            reviewerAverages.forEach { (reviewerId, average) ->
                avgs_file_reviewers_ids.appendLine("$reviewerId")
                avgs_file_reviewers.appendLine("$average")
            }

            // averages for books// fun8
            val bookGradesAverages: Map<String, Double> = itemsContainer.getBooksAvgScore()
                .mapValues { entry -> entry.value.toDoubleOrNull() ?: 11.0 } // Convert to Double
                .toSortedMap()


            val avgs_file_books_ids = lineStorageFactory.open("averages_of_books_ids")
            val avgs_file_books = lineStorageFactory.open("averages_of_books")
            bookGradesAverages.forEach { (bookId, average) ->
                avgs_file_books_ids.appendLine("$bookId")
                avgs_file_books.appendLine("$average")
            }


            // books and their grades


            // Mapping for each book: reviewers and their grades
            val reviewers_of_a_book: MutableMap<String, MutableList<Pair<String, Int>>> = mutableMapOf()
            val books_of_reviewers: MutableMap<String, MutableList<Pair<String, Int>>> = mutableMapOf()
            itemsContainer.getReviewersReviews().forEach { (reviewerId, reviews) ->
                reviews.forEach { review ->
                    val (bookId, grade) = review.removeSurrounding("review(", ")") // Removes the surrounding "review("
                        .split(",")
                        .let { it[0].split("=")[1] to it[1].split("=")[1] }
                    reviewers_of_a_book.computeIfAbsent(bookId) { mutableListOf() }
                        .add(Pair(reviewerId, grade.toInt()))
                    books_of_reviewers.computeIfAbsent(reviewerId) { mutableListOf() }
                        .add(Pair(bookId, grade.toInt()))

                }

                val reviewers_of_a_book_with_grade =
                    lineStorageFactory.open("reviewers_of_a_book_and_grades_in_one_line")
                val reviewers_of_a_book_with_grade_ids =
                    lineStorageFactory.open("reviewers_of_a_book_and_grades_in_one_line_with_grades_ids")
                val reviewers_of_a_book_with_grade_ids2 =
                    lineStorageFactory.open("reviewers_of_a_book_in_one_line_without_grades_ids")//fun 7
                val reviewers_of_a_book_ =
                    lineStorageFactory.open("reviewers_of_a_book_in_one_line_without_grades")//fun6

                reviewers_of_a_book.forEach { (bookId, reviewers) ->
                    val formattedReviewers_with_grades =
                        reviewers.joinToString(",") { (reviewer, grade) -> "$reviewer:$grade" }
                    val formattedReviewers_without_grades =
                        reviewers.joinToString(",") { (reviewer, grade) -> "$reviewer" }
                    reviewers_of_a_book_with_grade_ids.appendLine("$bookId")
                    reviewers_of_a_book_with_grade_ids2.appendLine(("$bookId"))
                    reviewers_of_a_book_with_grade.appendLine("$formattedReviewers_with_grades")
                    reviewers_of_a_book_.appendLine("$formattedReviewers_without_grades")

                }
                // Writing the data to a file

                val books_of_reviewer_with_grade = lineStorageFactory.open("books_of_a_reviewer_and_grades_in_one_line")
                val books_of_reviewer_with_grade_ids =
                    lineStorageFactory.open("books_of_a_reviewer_and_grades_in_one_line_with_grades_ids")
                val books_of_reviewer_with_grade_ids2 =
                    lineStorageFactory.open("books_of_a_reviewer_and_grades_in_one_line_without_grades_ids")//fun 4
                val books_of_reviewer_ = lineStorageFactory.open("books_of_a_reviewer_in_one_line_without_grades")//fun2

                books_of_reviewers.forEach { (reviewerId, books) ->
                    val formattedbooks_with_grades = books.joinToString(",") { (book, grade) -> "$book:$grade" }
                    val formattedbooks_without_grades = books.joinToString(",") { (book, grade) -> "$book" }
                    books_of_reviewer_with_grade_ids.appendLine("$reviewerId")
                    books_of_reviewer_with_grade_ids2.appendLine(("$reviewerId"))
                    books_of_reviewer_with_grade.appendLine("$formattedbooks_with_grades")
                    books_of_reviewer_.appendLine("$formattedbooks_without_grades")

                }
                // 13 files -> ((13 * 12)/2 )*100 = 7800 -> 8 sec for creating files but this is in setup

                // in answering the queries will be given 1300 * 5 = 6500 -> 7 sec for openning ...

            }
        }
// TODO:: IF IDS CAN CONTAIN "," :: i checked piazza and they answered alphanumeric chars
        fun getLine(id:String,file: LineStorage): Int?{
            val dataOfId= binarySearchIterativeFromExternal(id,file,true)
            return dataOfId?.first
        }

        /**
         * this function does binarysearch in linestorage
         * @param target - id of someone that we search in file
         * @return a pair <int, string> , first = num of line found the id in it and second=data of id that has been found
         */


        private fun binarySearchIterativeFromExternal(target: String, file: LineStorage, check_only_first_part:Boolean): Pair<Int, String>? {
            var left = 0
            var right = file.numberOfLines() - 1

            while (left <= right) {
                val mid = left + (right - left) / 2
                val dataString = file.read(mid)
                val parts = dataString.split(",") // Split the string at the comma
                /*if (parts.size < splits) {
                    //TODO::EXCPETION ..?
                    // Handle error: dataString does not contain a comma
                    continue
                }*/

                //TODO:: WHAT  HAPPENS IN THE CASE OF TWO BOOKS FOR ONE REVIEWER ..?

                val dataConverted = if (check_only_first_part ) {
                    parts[0]
                } else {
                    parts[0] + ',' + parts[1]
                }

                when {
                    dataConverted == target -> return Pair(mid, dataString) // Target found
                    dataConverted < target -> left = mid + 1 // Move to the right half
                    else -> right = mid - 1 // Move to the left half
                }
            }
            return null // Target not found
        }


        override fun getBookReviewScoreBy(idReviewer: String,idBook: String):String?{
            val gradeStorage_reviewer_first = lineStorageFactory.open("grades_that_reviewers_gave_books")
            val dataFound=binarySearchIterativeFromExternal(idReviewer+','+idBook,gradeStorage_reviewer_first,false)
            return dataFound?.second
        }

        override fun hasReviewedBook(idReviewer: String,idBook: String):Boolean {
            val gradeStorage_reviewer_first = lineStorageFactory.open("reviewers_and_books")
            val dataFound = binarySearchIterativeFromExternal(idReviewer + ',' + idBook, gradeStorage_reviewer_first,false)
            return if (dataFound == null) {
                false
            } else {
                // Add your logic here if dataFound is not null
                true
            }
        }
        override fun getAllBooksReviewedBy(idReviewer: String): String? {
            val books_of_reviewer_with_grade_ids2 = lineStorageFactory.open("books_of_a_reviewer_and_grades_in_one_line_without_grades_ids") // fun 4
            val books_of_reviewer_ = lineStorageFactory.open("books_of_a_reviewer_in_one_line_without_grades") // fun 2
            val line = getLine(idReviewer, books_of_reviewer_with_grade_ids2)
            return line?.let { books_of_reviewer_.read(it) }
        }

        override fun getAllBooksReviewedByWithScore(idReviewer: String): String? {
            val books_of_reviewer_with_grade = lineStorageFactory.open("books_of_a_reviewer_and_grades_in_one_line")
            val books_of_reviewer_with_grade_ids = lineStorageFactory.open("books_of_a_reviewer_and_grades_in_one_line_with_grades_ids")
            val line = getLine(idReviewer, books_of_reviewer_with_grade_ids)

            return line?.let { books_of_reviewer_with_grade.read(it) }

        }

        override fun getAllReviewersOfBook(idBook: String):String?{

            val reviewers_of_a_book_with_grade_ids2 = lineStorageFactory.open("reviewers_of_a_book_in_one_line_without_grades_ids")//fun 7
            val reviewers_of_a_book_= lineStorageFactory.open("reviewers_of_a_book_in_one_line_without_grades")//fun6
            val line = getLine(idBook, reviewers_of_a_book_with_grade_ids2)
            return line?.let { reviewers_of_a_book_.read(it) }


        }

        override fun getAllReviewersOfBookWithScores(idBook: String):String?{
            val reviewers_of_a_book_with_grade = lineStorageFactory.open("reviewers_of_a_book_and_grades_in_one_line")
            val reviewers_of_a_book_with_grade_ids = lineStorageFactory.open("reviewers_of_a_book_and_grades_in_one_line_with_grades_ids")
            val line = getLine(idBook, reviewers_of_a_book_with_grade_ids)
            return line?.let { reviewers_of_a_book_with_grade.read(it) }


        }
        override fun getReviewerAvgScore(idReviewer: String):String?{
            val file2openids= lineStorageFactory.open("averages_of_reviewers_ids")
            val line = getLine(idReviewer, file2openids)
            val file2open = lineStorageFactory.open("averages_of_reviewers")
            return line?.let { file2open.read(it) }


        }

        override fun getBookAvgScore(idBook: String):String?{
            val file2openids= lineStorageFactory.open("averages_of_books_ids")
            val line = getLine(idBook, file2openids)
            val file2open = lineStorageFactory.open("averages_of_books")
            return line?.let { file2open.read(it) }
           // val dataFound=binarySearchIterativeFromExternal(idBook,file2open,true)
            //return (dataFound?.second)?.split(",")?.getOrNull(1)

        }

        override fun clearStorage() {

            StorageDummyFiles.clearAll()

        }
    }


