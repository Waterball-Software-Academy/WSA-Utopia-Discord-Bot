# 小遊戲模組實作指引
1. 決定小遊戲模組的 Player (DTO) 需要甚麼屬性 
2. 實作 PlayerFinderAdapter
3. 設計 PlayerRewardedEvent
4. 實作其他小遊戲並試試看可不可以使用 PlayFinder 拿到 Player 資料
5. 其他小遊戲記得要透過 EventPublisher 來推播遊戲結算結果，並且讓 Player 資料更新
