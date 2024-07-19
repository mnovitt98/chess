import 'package:flutter/material.dart';
import 'dart:io';

enum Piece { pawn, rook, knight, bishop, queen, king }
enum PieceColor { white, black }

/* these should be the values of the above enums... */
const BlackSquareColor = Colors.grey;
const WhiteSquareColor = Colors.white;

/* as well as this...*/

PieceColor getPCFromColor(Color c) {
  return c == BlackSquareColor ? PieceColor.black : PieceColor.white;
}

final Map<Piece, Map<PieceColor, Image?>> pieceImgs = {
  Piece.pawn: {
    PieceColor.white : Image.asset("images/Chess_plt60.png"),
    PieceColor.black : Image.asset("images/Chess_pdt60.png")
  },
  Piece.rook: {
    PieceColor.white : Image.asset("images/Chess_rlt60.png"),
    PieceColor.black : Image.asset("images/Chess_rdt60.png")
  },
  Piece.knight: {
    PieceColor.white : Image.asset("images/Chess_nlt60.png"),
    PieceColor.black : Image.asset("images/Chess_ndt60.png")
  },
  Piece.bishop: {
    PieceColor.white : Image.asset("images/Chess_blt60.png"),
    PieceColor.black : Image.asset("images/Chess_bdt60.png")
  },
  Piece.queen: {
    PieceColor.white : Image.asset("images/Chess_qlt60.png"),
    PieceColor.black : Image.asset("images/Chess_qdt60.png")
  },
  Piece.king: {
    PieceColor.white : Image.asset("images/Chess_klt60.png"),
    PieceColor.black : Image.asset("images/Chess_kdt60.png")
  }
};

/* Should not move past this if any of the above images fail to load -
   do a try execpt and close gracefully should they fail to load...
*/

List<List<Piece?>> ChessBoardRows = [
  [
    Piece.rook, Piece.knight, Piece.bishop, Piece.king,
    Piece.queen, Piece.bishop, Piece.knight, Piece.rook,
  ],
  [
    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,
    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,
  ],
  List<Piece?>.filled(8, null),
  List<Piece?>.filled(8, null),
  List<Piece?>.filled(8, null),
  List<Piece?>.filled(8, null),
  [
    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,
    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,
  ],
  [
    Piece.rook, Piece.knight, Piece.bishop, Piece.queen,
    Piece.king, Piece.bishop, Piece.knight, Piece.rook,
  ],
];


/* have a global event history for now. */


(int, int) getMatrixIndex(int c_index) {
  return ((c_index / 8).toInt(), (c_index % 8).toInt());
}

Color getTileColor(int c_index) {

  /* convert to 2d indices, one indexed */
  int row = (c_index / 8).toInt() + 1;
  int col = (c_index % 8).toInt() + 1;

  return (row % 2) == (col % 2) ? WhiteSquareColor : BlackSquareColor;
}

TableRow makeBoardRow(List<Piece?> pieces, int index_offset) {
  int index = 0;
  List<BoardTile> bts = [];
  for (Piece? p in pieces) {
    Color tc = getTileColor(index + index_offset);
    bts.add(BoardTile(
      index: index + index_offset,
      tileColor: tc,
      pieceImg: pieceImgs[p]?[index_offset < 15 ? PieceColor.black : PieceColor.white],
    ));
    index++;
  }

  return TableRow(children: bts);
}

List<TableRow> makeBoardRows() {
  List<TableRow> trs = [];
  int index_offset = 0;
  for (List<Piece?> r in ChessBoardRows) {
    trs.add(makeBoardRow(r, index_offset));
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
  runApp(const MyApp());
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
      Image? pieceImg;

      /* eventually should be passing in image asset... */
      BoardTile({required this.index, required this.tileColor, required this.pieceImg});

      Widget build(BuildContext context) {
        return GestureDetector(
          onTap: () {
            print("index: ${getMatrixIndex(this.index)}");
          },
          child: Container(
             height: 75,
             color: this.tileColor,
             child: Center(child: pieceImg)
          )
        );
      }
}

class ChessBoardState extends ChangeNotifier {
  final List<Piece?> _pieces = [
    Piece.rook, Piece.knight, Piece.bishop, Piece.king,
    Piece.queen, Piece.bishop, Piece.knight, Piece.rook,
    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,
    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,

    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,

    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,
    Piece.pawn, Piece.pawn, Piece.pawn, Piece.pawn,
    Piece.rook, Piece.knight, Piece.bishop, Piece.queen,
    Piece.king, Piece.bishop, Piece.knight, Piece.rook,
  ];

  Piece? getPieceAt(index) {
    /* do bounds check */
    return _pieces[index];
  }

  void update() {
    notifyListeners();
  }
}