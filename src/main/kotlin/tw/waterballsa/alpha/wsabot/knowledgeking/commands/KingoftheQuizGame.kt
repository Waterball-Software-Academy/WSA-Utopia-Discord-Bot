package com.example.demo



class KingoftheQuizGame(io : GameIO, ui: GameUI, kernel : KingoftheQuiz) {
    private val io = io
    private val ui = ui
    private val kernel = kernel
    private val solutions = io.ReadSolution()
    init {
    }

    fun Play()
    {
        kernel.Prepare(solutions[0], solutions[0].limittime)
        ui.NextQuestion()
        //if timeout
        val score = kernel.Score()
        ui.UpdateScore(score)
    }


}
