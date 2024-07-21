import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import 'dart:io';
import 'client.dart';

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

final List<Piece?> initBoardState = [
  Piece.dRook,  Piece.dKnight, Piece.dBishop, Piece.dKing,  Piece.dQueen, Piece.dBishop, Piece.dKnight, Piece.dRook,
  Piece.dPawn,  Piece.dPawn,   Piece.dPawn,   Piece.dPawn,  Piece.dPawn,  Piece.dPawn,   Piece.dPawn,   Piece.dPawn,
  null,         null,          null,          null,         null,         null,          null,          null,
  null,         null,          null,          null,         null,         null,          null,          null,
  null,         null,          null,          null,         null,         null,          null,          null,
  null,         null,          null,          null,         null,         null,          null,          null,
  Piece.lPawn,  Piece.lPawn,   Piece.lPawn,   Piece.lPawn,  Piece.lPawn,  Piece.lPawn,   Piece.lPawn,   Piece.lPawn,
  Piece.lRook,  Piece.lKnight, Piece.lBishop, Piece.lQueen, Piece.lKing,  Piece.lBishop, Piece.lKnight, Piece.lRook,
];

Color getTileColor(int c_index) {
  /* convert to 2d indices, one indexed */
  int row = (c_index / 8).toInt() + 1;
  int col = (c_index % 8).toInt() + 1;
  return (row % 2) == (col % 2) ? WhiteSquareColor : BlackSquareColor;
}

class ChessBoardState extends ChangeNotifier {

  List<Piece?> _pieces = List.from(initBoardState);
  /*final WebSocketChannel ws = getWebSocket();*/

  Image? getPieceImg(index) {
    if (index < 0 || index > 63) {
      return null;
    }
    return pieceImgs[_pieces[index]];
  }

  List<({int src, int dest, Piece? p})> getMovesFromGameEngine(int srcIndex, int destIndex) {

    /* this will be the interface between backend and frontend. may pull out into its own
       class eventually. this will need to provide indirection appropriately; we want the
       game engine to be able to run locally, over the network, etc.
    */
    /*sendMove(ws, srcIndex, destIndex);*/

    return [(src: srcIndex, dest: destIndex, p: null)];
  }

  void updateBoard(int srcIndex, int destIndex) {
    /* assumption will be to move piece at src to dest, removing the piece at dest
       if there. Unless Piece is supplied, piece at dest will be the same as piece
       at src. This allows for the arbitrary placement of pieces by making src
       and dest the same index, and providing a piece type
    */
    if (srcIndex == destIndex) { /* misclick */
       return;
    }

    for (final (:src, :dest, :p) in getMovesFromGameEngine(srcIndex, destIndex)){
        Piece? target = p ?? _pieces[src]; /* this shouldn't ever be null... */
        _pieces[src] = null;
        _pieces[dest] = target;
    }
    notifyListeners();
  }

  void resetBoard() {
    _pieces = List.from(initBoardState);
    notifyListeners();
  }
}
