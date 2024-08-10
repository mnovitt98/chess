import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import 'dart:io';
import 'dart:async';
import 'client.dart';

const BlackSquareColor = Colors.grey;
const WhiteSquareColor = Colors.white;

enum Piece {
  INVALID,
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
  Piece.dRook,  Piece.dKnight, Piece.dBishop, Piece.dQueen,  Piece.dKing, Piece.dBishop, Piece.dKnight, Piece.dRook,
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
  late WebSocketChannel ws;
  ({int i, Piece? p}) lastSelected = (i: -1, p: null);
  bool needsPromotion = false;

  ChessBoardState() {
    ws = getWebSocket(7897, (message) {
      deserializeMove(message, this);
    });
  }

  void updateBoard(int srcIndex, int destIndex) async {
    /* assuming the front end board is still faithfully representing the
       backend state of things, could also prevent a network back and forth
       by returning when the board does not have a piece at srcIndex */
    if (srcIndex == destIndex) { /* misclick */
       return;
    }

    setLastSelected(srcIndex);
    clearPiece(srcIndex);
    notifyListeners();

    await ws.ready;
    ws.sink.add(serializeMove(srcIndex, destIndex, _pieces[srcIndex]));
  }

  void resetBoard() async {
    await ws.ready;
    ws.sink.add(serializeMove(-1, -1, null));
  }

  void clearBoard() {
       _pieces = List.from(initBoardState);
  }

  void setPiece(int atIndex, Piece? p) {
       _pieces[atIndex] = p;
  }

  void clearPiece(int atIndex) {
       _pieces[atIndex] = null;
  }

  void setLastSelected(int i) {
    lastSelected = (i: i, p: _pieces[i]);
  }

  void resetLastSelected() {
       _pieces[lastSelected.i] = lastSelected.p;
       lastSelected = (i: -1, p: null);
  }

  void handleSelection(String s) async {
    await ws.ready;
    ws.sink.add("PROMOTE|${s}");
  }

  void beginPromotion() {
    this.needsPromotion = true;
  }

  void endPromotion() {
    this.needsPromotion = false;
  }

  Image? getPieceImg(index) {
    if (index < 0 || index > 63) {
      return null;
    }
    return pieceImgs[_pieces[index]];
  }
}
