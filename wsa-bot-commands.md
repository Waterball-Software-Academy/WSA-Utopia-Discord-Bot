# Commands Document

## 1a2b
| Commands | Arguments | Description                 |
|:--------:| --------- | --------------------------- |
|   1a2b   |           | Start a new guess num game. |

## gaas
|        Commands        | Arguments                                                                                          | Description                                          |
|:----------------------:| -------------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| gaas stats-avg-and-max | event-date-year(INTEGER): Year<br>event-date-month(INTEGER): Month<br>event-date-day(INTEGER): Day | Get Avg And Max Participants Number at Specific Date |
|      gaas observe      | gaas-member(USER): GaaS Member                                                                     | Add a specific member to the watchlist               |
|    gaas unobserved     | gaas-member(USER): GaaS Member                                                                     | Remove a specific member from the watchlist          |

## audio
|    Commands    | Arguments                                                                                                                                            | Description            |
|:--------------:| ---------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------- |
| audio breakout | room-size(INTEGER): Number of members per room.<br>countdown(INTEGER): Countdown time in seconds.<br>room-name(STRING): The name prefix of per room. | Create breakout rooms. |

## ping
| Commands | Arguments | Description |
|:--------:| --------- | ----------- |
|   ping   |           | sends pong  |

## mute
|    Commands    | Arguments                                                             | Description    |
|:--------------:| --------------------------------------------------------------------- | -------------- |
| mute audiences | audience(USER): Allow who to voice<br>role(ROLE): Allow role to voice | Mute Audiences |
|  mute revoked  |                                                                       | Unmute         |

## audience
|     Commands     | Arguments                                            | Description                                    |
|:----------------:| ---------------------------------------------------- | ---------------------------------------------- |
| audience counter | time-length(INTEGER): Time is calculated in minutes. | count the resent voice channel audience amount |

## random
|    Commands    | Arguments                                                                                                  | Description |
|:--------------:| ---------------------------------------------------------------------------------------------------------- | ----------- |
| random lottery | number(INTEGER): Number of choose members per room.<br>role(ROLE): Only select specific role in this round | Lottery     |

## utopia
| Commands | Arguments | Description    |
|:--------:| --------- | -------------- |
|  utopia  |           | utopia command |

## roulette
| Commands | Arguments | Description     |
|:--------:| --------- | --------------- |
| roulette |           | Start the game. |

## message
|        Commands        | Arguments                                                                                                               | Description                               |
|:----------------------:| ----------------------------------------------------------------------------------------------------------------------- | ----------------------------------------- |
|  message cherry-pick   | start-time(STRING): the start time of the cherry-pick range.<br>end-time(STRING): the end time of the cherry-pick range | to cherry pick message                    |
| message cherry-pick-to | channel-name(STRING): destination of channel name                                                                       | to cherry pick message to another channel |

## weekly-messages-volume
|        Commands        | Arguments                                                            | Description                                    |
|:----------------------:| -------------------------------------------------------------------- | ---------------------------------------------- |
| weekly-messages-volume | channel-name(STRING): The channel to show the weekly messages volume | Show the weekly messages volume of the channel |