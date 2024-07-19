import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'dart:io';

const BlackSquareColor = Colors.grey;
const WhiteSquareColor = Colors.white;

enum Piece {
  lPawn,
  dPawn,
  lRook,
  dRook,
  lKnight,
  dKnight,
  lBishop,
  dBishop,
  lQueen,
  dQueen,
  lKing,
  dKing,
}

/* we are not concerned with the game logic here, but only how each move type
   affects the board, i.e., what the board needs to draw...
*/

enum MoveType {
  capture,
  advance, /* this will still cover en passant */
  kingsideCastle,
  queensideCastle,

  /* promotion,  handle this later */
}

final Map<Piece, Image?> pieceImgs = {
  Piece.lPawn:   Image.asset("images/Chess_plt60.png"),
  Piece.dPawn:   Image.asset("images/Chess_pdt60.png"),
  Piece.lRook:   Image.asset("images/Chess_rlt60.png"),
  Piece.dRook:   Image.asset("images/Chess_rdt60.png"),
  Piece.lKnight: Image.asset("images/Chess_nlt60.png"),
  Piece.dKnight: Image.asset("images/Chess_ndt60.png"),
  Piece.lBishop: Image.asset("images/Chess_blt60.png"),
  Piece.dBishop: Image.asset("images/Chess_bdt60.png"),
  Piece.lQueen:  Image.asset("images/Chess_qlt60.png"),
  Piece.dQueen:  Image.asset("images/Chess_qdt60.png"),
  Piece.lKing:   Image.asset("images/Chess_klt60.png"),
  Piece.dKing:   Image.asset("images/Chess_kdt60.png"),
};

Color getTileColor(int c_index) {
  /* convert to 2d indices, one indexed */
  int row = (c_index / 8).toInt() + 1;
  int col = (c_index % 8).toInt() + 1;
  return (row % 2) == (col % 2) ? WhiteSquareColor : BlackSquareColor;
}

class ChessBoardState extends ChangeNotifier {
  final List<Piece?> _pieces = [
    Piece.dRook,  Piece.dKnight, Piece.dBishop, Piece.dKing,  Piece.dQueen, Piece.dBishop, Piece.dKnight, Piece.dRook,
    Piece.dPawn,  Piece.dPawn,   Piece.dPawn,   Piece.dPawn,  Piece.dPawn,  Piece.dPawn,   Piece.dPawn,   Piece.dPawn,
    null,         null,          null,          null,         null,         null,          null,          null,
    null,         null,          null,          null,         null,         null,          null,          null,
    null,         null,          null,          null,         null,         null,          null,          null,
    null,         null,          null,          null,         null,         null,          null,          null,
    Piece.lPawn,  Piece.lPawn,   Piece.lPawn,   Piece.lPawn,  Piece.lPawn,  Piece.lPawn,   Piece.lPawn,   Piece.lPawn,
    Piece.lRook,  Piece.lKnight, Piece.lBishop, Piece.lQueen, Piece.lKing,  Piece.lBishop, Piece.lKnight, Piece.lRook,
  ];

  Image? getPieceImg(index) {
    if (index < 0 || index > 63) {
      return null;
    }
    return pieceImgs[_pieces[index]];
  }

  /* this will not be done here in the future. instead it will simply ask the backend whether it may update the
     board in accordance with player input. it will receive a list of instructions that alter the board. if
     the move is invalid, then an empty list will be returned.
  */

  bool isValidMove(int srcIndex, int destIndex) {
    return true;
  }
  void updateBoard(int srcIndex, int destIndex) {
    if (isValidMove(srcIndex, destIndex)) {
      _pieces[destIndex] = _pieces[srcIndex];
      _pieces[srcIndex] = null;
      notifyListeners();
    }
  }

  void
}
