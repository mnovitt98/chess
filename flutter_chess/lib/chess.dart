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
  WebSocketChannel? ws;

  /* not sure if I like using a callback here, would rather it be more synchronized, i.e.,
     the client sends a move to the engine, waits, the engine sends a move back, the client
     updates. however, the callback has the benefit of preempting the transition to multiplayer...*/
  ChessBoardState() {
    ws = getWebSocket(7897, (message) {
      ({int src, int dest, Piece? p}) m = deserializeMove(message);
      Piece? target = m.p ?? _pieces[m.src]; /* this shouldn't ever be null... */
      _pieces[m.src] = null;
      _pieces[m.dest] = target;
      notifyListeners();
    });
  }

  void updateBoard(int srcIndex, int destIndex) async {
    if (srcIndex == destIndex) { /* misclick */
       return;
    }
    await ws?.ready;
    ws?.sink.add(serializeMove(srcIndex, destIndex, _pieces[srcIndex]));
  }

  Image? getPieceImg(index) {
    if (index < 0 || index > 63) {
      return null;
    }
    return pieceImgs[_pieces[index]];
  }
  void resetBoard() {
    _pieces = List.from(initBoardState);
    notifyListeners();
  }
}
