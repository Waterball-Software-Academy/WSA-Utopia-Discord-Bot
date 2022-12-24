package com.example.demo

import java.util.Vector

interface GameIO {
    fun ReadSolution() : List<Solution>
    fun SaveGame(answerList: AnswerList)
}

class GooleSheet : GameIO
{
    override fun ReadSolution(): List<Solution> {

        return listOf<Solution>(
            Solution("1 == 2 的結果是?", listOf<String>("1 == 2","string","true","false"),4, 10),
            Solution("456", listOf<String>("5","6","7","8"),3, 10),
            Solution("789", listOf<String>("1","6","3","5"),3, 10),
            Solution("101", listOf<String>("1","2","3","4"),3, 10),
            Solution("888", listOf<String>("1","3","3","4"),3, 10))
    }

    override fun SaveGame(answerList: AnswerList) {

    }

}
