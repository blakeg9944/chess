# My notes
The ConnectionManager: You already started thinking about this! This is the class that stores who is in which game.

The WebSocketHandler: This is the "Front Door." It receives the JSON text, turns it back into your UserGameCommand objects, and decides which method to call.

The Game Logic Integration: This is where you connect your existing ChessGame logic (from Phase 3/4) to the WebSocket so the board actually updates.

1. sendMessage (The Private Text)
   This method is for when the server needs to talk to only one person (like sending an ErrorMessage because they made an illegal move).

The Logic: You don't even need the ConnectionManager for this! You just take the Session that sent the command and send a message straight back to it.

2. broadcastMessage (The Group Chat)
   This is where the code you just wrote shines. When a player moves, everyone in that game needs to see it.

The Logic:

The WebSocketHandler calls broadcastMessage(gameID, message, exceptThisSession).

Inside that method, it asks your ConnectionManager: "Hey, give me the Set<Session> for Game #404."

It then loops through that set and calls sendString() on every session except the person who just moved.
