package tw.waterballsa.utopia.knowledgeking.app.repo

import tw.waterballsa.alpha.wsabot.bot.knowledgeking.commands.AnswerList
import tw.waterballsa.alpha.wsabot.bot.knowledgeking.commands.Solution
import java.net.URL
import java.util.Random

interface GameIO {
    fun ReadSolution() : List<Solution>
    fun SaveGame(answerList: AnswerList)
}



class GooleSheet : GameIO
{
    override fun ReadSolution(): List<Solution> {
        //https://docs.google.com/spreadsheets/d/e/2PACX-1vTMy_yTvqpzzmtD4QSXroKvwiaige-RGeeyuER4MT1n9-taYEJIgG8j6MkXycQBxvReyPKYldacHrhW/pub?gid=0&single=true&output=csv
        val url = URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vTMy_yTvqpzzmtD4QSXroKvwiaige-RGeeyuER4MT1n9-taYEJIgG8j6MkXycQBxvReyPKYldacHrhW/pub?gid=0&single=true&output=csv")
        val inputStream = url.openStream()
        val text = inputStream.bufferedReader().use { it.readText() }
        val data = text.split(Regex("\n(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))

        val solutions = mutableListOf<Solution>()
        val rd = Random()

        for(i in 1..5)
        {
            val d = data[rd.nextInt(data.size-1)+1].split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
            for(j in d)
            {
                println(j)
            }
            solutions.add(Solution(d[2], listOf(d[3], d[4], d[5], d[6]), d[7].toInt(), 10))
        }

        return solutions
    }

    override fun SaveGame(answerList: AnswerList) {

    }

}
