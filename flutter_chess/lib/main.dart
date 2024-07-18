import 'package:flutter/material.dart';
import 'dart:io';

enum Piece { pawn, rook, knight, bishop, queen, king }
enum PieceColor { white, black }
const BlackSquareColor = Colors.grey;
const WhiteSquareColor = Colors.white;

final Map<Piece, (Image, Image)> pieceImgs = {
  Piece.pawn:   (Image.asset("images/Chess_plt60.png"), Image.asset("images/Chess_pdt60.png")),
  Piece.rook:   (Image.asset("images/Chess_rlt60.png"), Image.asset("images/Chess_rdt60.png")),
  Piece.knight: (Image.asset("images/Chess_nlt60.png"), Image.asset("images/Chess_ndt60.png")),
  Piece.bishop: (Image.asset("images/Chess_blt60.png"), Image.asset("images/Chess_bdt60.png")),
  Piece.queen:  (Image.asset("images/Chess_qlt60.png"), Image.asset("images/Chess_qdt60.png")),
  Piece.king:   (Image.asset("images/Chess_klt60.png"), Image.asset("images/Chess_kdt60.png"))
};

/* Should not move past this if any of the above images fail to load - do a try execpt and close
   gracefully should they fail to load...
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
  return TableRow(
    children: List.generate(
      8,
      (int index) => BoardTile(
           index: index + index_offset,
           tileColor: getTileColor(index + index_offset),
           pieceImg: pieceImgs[Piece.king]?.$2
      ),
      growable: false
    )
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
        body: Center(
          child: Table(
            columnWidths: const <int, TableColumnWidth>{
              0: FixedColumnWidth(75),
              1: FixedColumnWidth(75),
              2: FixedColumnWidth(75),
              3: FixedColumnWidth(75),
              4: FixedColumnWidth(75),
              5: FixedColumnWidth(75),
              6: FixedColumnWidth(75),
              7: FixedColumnWidth(75)
            },
            children: <TableRow>[
              makeBoardRow(0),
              makeBoardRow(8),
              makeBoardRow(16),
              makeBoardRow(24),
              makeBoardRow(32),
              makeBoardRow(40),
              makeBoardRow(48),
              makeBoardRow(56),
            ]
          )
      )
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