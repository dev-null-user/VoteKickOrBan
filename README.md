# VoteKickOrBan
## Now the players themselves decide whether to ban or kick the offender

--------------------------------------------

#### Permissions:
vote
<br/>vote.stop
<br/>vote.help
<br/>vote.kick.fine
<br/>vote.kick
<br/>vote.ban

-----------------------------------------------------

#### Commands:
/vote - to vote
<br/>/vote help - show the list a commands
<br/>/vote kickFine <player> <fine> <message> - create a vote, kick the player and give him a penalty
<br/>/vote kick <player> <message> - create a vote, kick the player
<br/>/vote ban <player> <message> - create a vote, ban the player
<br/>/vote stop - close the vote
  
-----------------------------------------------------

#### Config.yml
```ymal
messages:
  helps:
    '0': '#a>>>>>> List commands <<<<<<'
    '1': /vote - to vote
    '2': /vote kickFine <player> <fine> <message> - create vote, kick a player with a penalty
    '4': /vote kick <player> <message> - create vote, kick the player
    '5': /vote ban <player> <message> - create vote, ban the player
    '6': /vote stop - stop a vote
  errors:
    err1: Type the command /vote help
    err2: You have already cast your vote!
    err3: Missing Voting!
    err4: Missing player!
    err5: You cannot kick or ban yourself!
    err6: You do not own current voting!
    err7: Not enough arguments, enter the command /vote help !
    err8: Player {playerKick} disconnected from server!
    err9: You cannot vote through the console!
    err10: The argument {argument} must be a number!
    err11: The argument {argument} must be a word!
    err12: Voting is not over yet!
  info:
    stopVote: '#lVoting has been suspended!'
    complete: '#lVoting has been completed!'
    sendVoteYes: '#lYou sent your vote (Confirmation)!'
    sendVoteNo: '#lYou sent your vote (Rejection)!'
    startKickOrBanVote: '#lVoting was created by player #b{playerSendKick} #f!%n{msg}%nReason: {reportMsg}'
    backVote: '#lPlayer {playerKick} remained on the server by voting!'
    countVotes: '#lVoted for the exile: {countYes}%nVoted for the pardon: {countNo}%nTotal votes: {countAll}'
    backVoteEqual: '#lThe total votes were the same!'
    kickVote: '#lPlayer {playerKick} was kicked from the server by voting!'
    kickFineVote: '#lPlayer {playerKick} was expelled from the server and a fine {fineMoney}$ was imposed on voting!'
    banReportMsgVote: '#lYou were banned by voting! %nReason: {reportMsg}'
    banVote: '#lPlayer {playerKick} has been banned!'
    kickReportMsgVote: 'You were kicked by the vote! %nReason: {reportMsg}'
    kickFineReportMsgVote: 'You were kicked by the vote and fined {fineMoney}$!%nReason: {reportMsg}'
    kickChatInfo: '#l#aPlayer {playerSendKick} wants to kick player {playerKick}'
    kickChatInfoFine: '#l#aPlayer {playerSendKick} wants to kick player {playerKick} and wants to be fined {fineMoney}$'
    banChatInfo: '#l#aPlayer {playerSendKick} wants to ban player {playerKick}'
formAlert:
    title: Choose a voice!
    content: 'Action: {infoSender}% n% n {infoFine}%n%nReason: {reportMsg}'
    contentExtraFine: Will be fined {fineMoney}$
    btnYes: Yes
    btnNo: No
    bossBar: '#lPlayer #e {playerSendKick} #fwants to kick player #c{playerKick}#f'
    bossBarBan: '#lPlayer #e {playerSendKick} #f wants to ban player #c {playerKick}#f'
    bossBarVotes: '#l#e| Total: {countAll} | Kick out: {countYes} | Leave {countNo} | Left: {countComplete} |'
options:
  maxTimeComplete: 100 # max second {countComplete}
sounds:
  kick: random.hurt
  noKick: random.levelup
  equalVotes: random.totem
  timerMim: random.toast
```

--------------------------------------------

#### Suffix list:
note - suffixes are valid where specified in the config
<br/>`{playerKick}` - player to be kicked out
<br/>`{playerSendKick}` - player who wants to kick player
<br/>`{reportMsg}` - reason
<br/>`{countAll}` - count total votes
<br/>`{countYes}` - count voted for (Yes)
<br/>`{countNo}` - count voted for (No)
<br/>`{msg}` - combined message
<br/>`{countComplete}` - after how many votes will end
<br/>`{fineMoney}` - fine imposed

--------------------------------------------

#### Other information:
- voting can only be started after the completion of the previous vote
- when voting starts, all players create a BossBar, including those players who connected to the server during voting
- you can vote by answering (Yes) or (No), and at the end of the vote the number of voters (Yes) and (No) is read, due to this, the action (kick or ban) is triggered or vice versa the player will remain on the server
- stop the vote can only be the one who launched it
- when a player disconnects from the server, voting automatically stops (I’m still thinking about it, I want to remove it later)
- this plugin uses Russian by default
