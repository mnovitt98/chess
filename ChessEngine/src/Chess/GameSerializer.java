package chess;

import chess.enums.MoveType;
import chess.Index;
import chess.pieces.Piece;
import chess.pieces.King;
import chess.Board;

public class GameSerializer {
    public static String[] serializeMove(Board b, MoveType m, Piece p, Index src, Index dest) {
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

        if (p.isLight()) {
            src.setSwitchOrientation();
        }

        Piece r = null;
        switch (m) {
        case MoveType.INVALID:
            return new String[]{"INVALID"};
        case MoveType.CAPTURE:
        case MoveType.ADVANCE:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", src.toString()),
                "COMPLETE"
            };
        case MoveType.LENPASSANT:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", src.toString()),
                String.join("|", "REMOVE", src.left(1).toString()),
                "COMPLETE"
            };
        case MoveType.RENPASSANT:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", src.toString()),
                String.join("|", "REMOVE", src.right(1).toString()),
                "COMPLETE"
            };

        case MoveType.KCASTLE:
            r = b.getPieceAt(((King) p).getKingsideRookDest());
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", src.toString()),
                String.join("|", "REPLACE", r.toString(), ((King) p).getKingsideRookDest().toString()),
                String.join("|", "REMOVE", ((King) p).getKingsideRookPos().toString()),
                "COMPLETE"
            };

        case MoveType.QCASTLE:
            r = b.getPieceAt(((King) p).getQueensideRookDest());
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", src.toString()),
                String.join("|", "REPLACE", r.toString(), ((King) p).getQueensideRookDest().toString()),
                String.join("|", "REMOVE", ((King) p).getQueensideRookPos().toString()),
                "COMPLETE"
            };

         case MoveType.PROMOTION_INITIATE:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                String.join("|", "REMOVE", src.toString()),
                "PROMOTE_INITIATE"
            };

         case MoveType.PROMOTION_SUBSTITUTE:
            return new String[]{
                String.join("|", "REPLACE", p.toString(), dest.toString()),
                "PROMOTE_SUBSTITUTE"
            };
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
