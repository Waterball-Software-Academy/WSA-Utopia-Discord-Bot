package tw.waterballsa.utopia.automaticchannelcommenttracking.domain


class UsersMessageCount(
    val date: String = "",
    val userId: String = "",
    val channelId: String = "",
    val messageCount: Int = 0
)


//class DateToUserMessageCount : LinkedHashMap<String, usersMessageCount>() {
//
//    fun incrementKeyCount(date: String, userId: String, channelId: String): usersMessageCount {
//        return merge(date, 1) { oldValue, increment -> oldValue + increment }
//    }
//
//    override fun toString(): String {
//        var totalCount = 0
//        var result = ""
//
//        forEach { (date, count) ->
//            totalCount += count
//            result += "$date, $count, $totalCount\n"
//        }
//
//        return result
//    }
//
//}
