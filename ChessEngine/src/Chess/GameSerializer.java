package chess;

import chess.enums.MoveType;
import chess.Piece;
import chess.Index;

public class GameSerializer {
    public static String[] serializeMove(MoveType m, Piece p, Index src, Index dest) {
        /*
           The following events are pertinent to the client: when a piece should
           be placed on a square; when a piece should be removed from a square;
           when the game has terminated and who the winner is; and alerting the
           client that the requested move was illegal.

           These events will be communicated with the corresponding messages:

           REPLACE|{pieceType}|{destIndex}
           REMOVE|srcIndex|
           WINNER|{lightness}|
           INVALID||

           where destIndex is a c index of the board,
           pieceType is one of the following strings:
           "(l|d)Pawn" | "(l|d)Rook" | "(l|d)Knight" | "(l|d)Bishop" | "(l|d)Queen" | "(l|d)King",
           and lightness is one of the following letters:
           (l|d)

           In the future, one can imagine stepping through a recorded game,
           and this would have its own directives, but for now this should
           suffice.
        */
        switch (m) {
        case MoveType.INVALID:
            return new String[]{"INVALID||"};
        case MoveType.CAPTURE:
        case MoveType.ADVANCE:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", src.toString())
            };
        case MoveType.LENPASSANT:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", (new Index(src, p.isLight())).left(1).toString())
            };
        case MoveType.RENPASSANT:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", (new Index(src, p.isLight())).right(1).toString())
            };

         /* TODO */
         case MoveType.KCASTLE:
         case MoveType.QCASTLE:
         case MoveType.PROMOTION:
             break;
        }

        return new String[]{"INVALID||"};
    }

    public static String[] deserializeMove(String data) {
        /*
           The only information the client will even need to communicate to the
           game engine are the following two events: when a piece moves from one
           square to another; and when the user selects a piece to replace a
           pawn during its promotion.

           These events will be communicated with the corresponding two messages:

           MOVE|{srcIndex}|{destIndex}
           PROMOTE|{pieceType}|

           where srcIndex and destIndex are both c indicies of the board,
           and pieceType is one of the following strings:
           "(l|d)Pawn" | "(l|d)Rook" | "(l|d)Knight" | "(l|d)Bishop" | "(l|d)Queen" | "(l|d)King"
           Even though the lightness can be inferred, for consistency across messages the lightness
           property will be included.
        */
        return data.split("\\|"); // split takes regex, need to escape alternation operator
    }
}
