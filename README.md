# chess

To those who may be reviewing this code,

The purpose of this project was to demonstrate foundational understanding in
both Java and Dart, specifically Dart driving Flutter. Admittedly it is in an
incomplete state - I will continue to work on it. To showcase what is possible
at present, please see the following link: 
[demo](https://drive.google.com/file/d/1-Ri21oxvugoMbnBgIXFJpK2IoKtoLjHC/view?usp=drive_link).
Note that so far, only basic piece movement is implemented. I still need to add
the greater game logic, e.g., turn tracking, king check and checkmate 
determination, etc.

I have since fixed the following, but will leave here for the reader's perusal:

> From this one can observe that the chess frontend is functional albeit with a
> noticeable visual defect - after a move is completed, the frontend updates ina
> flurry. There is not a clean taking of one piece by another, or one piece moving
> from one square to another. The ghost of a piece's former position "flashes"
> before the board is updated. This is, to my knowledge, because the accepting of
> the draggable (the piece) is NOT blocking and the following is occuring:
> onAccept triggers a board update and sends the move information to the backend
> chess engine. The draggable reverts back to the child widget (piece "returns" to
> original square). The backend receives the move information and sends back
> update information. The frontend receives the update information and finally
> reflects this in the board.

> A possible solution would be to make this sequence blocking, that is, onAccept
> would need to hold off completing until the backend informed it of the move
> validity and repainting operations. Of course, if the chess engine lived on
> the frontend then this would provide a speedy resolution - but I would not
> have the opportunity to demonstrate as much Java understanding.

Indeed the foregoing at first appeared to pose as quite an impasse. A walk the
next morning in some fresh air revealed a much better approach: for any move the
user makes on the front end simply remove the selected piece from the
board. When the backend responds with its judgment of the move, update the pieces
to their new locations in the event of a valid move, or have the front end just
put the remembered selected piece back on its old position. The time gap is not
large enough (supposing the network response is swift) to notice the absent
piece.

A final note on what order to visit the source code. The order of developement
was as follows: flutter UI -> java backend (websocket server) connection to
flutter UI -> backend chess engine -> linking chess engine to websocket server
to flutter frontend.  Consequently, the best structure and the most documentation
would be found in the earliest stages. If I may recommend a file that I believe
best represents my code quality standards I would point you to:
ChessEngine/src/WebSocket/WebSocketServer.java. This took some time to develop,
as I could not find in the Java standard library a ready WebSocket server
solution, and I wanted as few dependencies as possible, so I used the RFC to write one
myself. This was no small detour, even if it does not implemenet the protocol in full.
