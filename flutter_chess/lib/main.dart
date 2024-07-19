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
  dKing
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

/* Should not move past this if any of the above images fail to load -
   do a try execpt and close gracefully should they fail to load...
*/

(int, int) getMatrixIndex(int c_index) {
  return ((c_index / 8).toInt(), (c_index % 8).toInt());
}

Color getTileColor(int c_index) {

  /* convert to 2d indices, one indexed */
  int row = (c_index / 8).toInt() + 1;
  int col = (c_index % 8).toInt() + 1;

  return (row % 2) == (col % 2) ? WhiteSquareColor : BlackSquareColor;
}

TableRow makeBoardRow(int index_offset) {
  int index = 0;
  List<BoardTile> bts = [];
  for (int i = 0; i < 8; i++) {
    Color tc = getTileColor(index + index_offset);
    bts.add(
      BoardTile(
        index: index + index_offset,
        tileColor: tc,
      )
    );
    index++;
  }

  return TableRow(children: bts);
}

List<TableRow> makeBoardRows() {
  List<TableRow> trs = [];
  int index_offset = 0;
  for (int i = 0; i < 8; i++) {
    trs.add(makeBoardRow(index_offset));
    index_offset += 8;
  }

  return trs;
}

Table makeChessBoard() {
  return Table(
    defaultColumnWidth: FixedColumnWidth(75),
    children: makeBoardRows(),
  );
}

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => ChessBoardState(),
      child: const MyApp(),
    )
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    const title = 'Chess';
    return MaterialApp(
      title: title,
      home: Scaffold(
        appBar: AppBar(
          title: const Text(title),
        ),
        body: Center(child: makeChessBoard())
    ));
  }
}

class BoardTile extends StatelessWidget {
  int   index;
  Color tileColor;

  /* eventually should be passing in image asset... */
  BoardTile({required this.index, required this.tileColor});

  Widget build(BuildContext context) {
    return Consumer<ChessBoardState>(
      builder: (context, boardState, child) {
        return GestureDetector(
          onTap: () {
            print("index: ${getMatrixIndex(this.index)}");
            if (index == 0) {
              boardState._pieces[0] = Piece.lPawn;
              boardState.update();
            }
          },
          child: Container(
            height: 75,
            color: this.tileColor,
            child: Center(child: boardState.getPieceImg(index))
          )
        );
      }
    );
  }
}

class ChessBoardState extends ChangeNotifier {
  final List<Piece?> _pieces = [
    Piece.dRook,  Piece.dKnight, Piece.dBishop, Piece.dKing,
    Piece.dQueen, Piece.dBishop, Piece.dKnight, Piece.dRook,
    Piece.dPawn,  Piece.dPawn,   Piece.dPawn,   Piece.dPawn,
    Piece.dPawn,  Piece.dPawn,   Piece.dPawn,   Piece.dPawn,

    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,

    Piece.lPawn, Piece.lPawn,   Piece.lPawn, Piece.lPawn,
    Piece.lPawn, Piece.lPawn,   Piece.lPawn, Piece.lPawn,
    Piece.lRook, Piece.lKnight, Piece.lBishop, Piece.lQueen,
    Piece.lKing, Piece.lBishop, Piece.lKnight, Piece.lRook,
  ];

  Image? getPieceImg(index) {
    /* do bounds check */
    if (index < 0 || index > 63) {
      return null;
    }

    return pieceImgs[_pieces[index]];
  }

  void update() {
    notifyListeners();
  }
}