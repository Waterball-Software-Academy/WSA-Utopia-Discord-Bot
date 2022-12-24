package com.example.demo

import java.util.*

class Solution(_question : String, option : List<String>, _correctanswer : Int, _limittime: Int)
{
    val question = _question
    val option = option
    val correctanswer = _correctanswer
    val limittime = _limittime
}

data class AnswerList(val table : Map<Solution, Vector<AnswerSheet>>)

class Competition {
    private val table = mutableMapOf<Solution, Vector<AnswerSheet>>()
    private var current = Solution("", listOf(), 0, 0);

    fun AddPlayer(sheet: AnswerSheet): Boolean {
        for (answersheet in table[current]!!) {
            if (answersheet?.userid == sheet.userid) {
                return false;
            }
        }
        table[current]?.add(sheet);
        return true;
    }

    fun CurrentQuestion(solution: Solution) {
        current = solution
        table[current] = Vector<AnswerSheet>();
    }

    fun GetAnswerList() : AnswerList
    {
        return AnswerList(table)
    }

    override fun toString(): String
    {
        var content = String()
        val l = listOf("A","B","C","D")
        for(i in table.keys)
        {
            content += "題目:${i.question} \n" +
                       "答案:(${i.correctanswer})  ${i.option[i.correctanswer-1]}\n"+
                       "responed player:\n"
            for(j in table[i]!!)
            {
                content += "${j.userid} 選擇 ${l[j.useranswer]} 花 ${j.responetime} s\n "
            }
        }
        return content
    }
}

class AnswerSheet(_userid : String, _useranswer : Int, _responetime : Int)
{
    val userid = _userid
    val useranswer = _useranswer
    val responetime = _responetime
}

class KingoftheQuiz {
    private var limittime : Int = 0;
    private var competition = Competition()
    private var starttime : Long = 0
    fun Prepare(solution : Solution, _limittime : Int)
    {
        starttime = Date().time/1000
        limittime = _limittime;
        competition.CurrentQuestion(solution);
    }

    fun RsponeAnswer(id : String, answernumber : Int) : String
    {
        val rtime = Date().time/1000 - starttime
        if ( rtime > limittime)
        {
            return "已經超過回答時間"
        }
        if(competition.AddPlayer(AnswerSheet(id,answernumber, rtime.toInt())))
        {
            val i = listOf("A","B","C","D")
            return "玩家已選擇${i[answernumber-1]}選項"
        }else
        {
            return "玩家答過了"
        }

    }

    fun GetAnswerList() : AnswerList
    {
        return competition.GetAnswerList();
    }

    fun Restart()
    {
        competition = Competition()
        limittime = 0;
    }

    fun Score() : String
    {
        return "player..."
    }
}
