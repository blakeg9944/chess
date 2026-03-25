# My notes
The ConnectionManager: You already started thinking about this! This is the class that stores who is in which game.

The WebSocketHandler: This is the "Front Door." It receives the JSON text, turns it back into your UserGameCommand objects, and decides which method to call.

The Game Logic Integration: This is where you connect your existing ChessGame logic (from Phase 3/4) to the WebSocket so the board actually updates.
