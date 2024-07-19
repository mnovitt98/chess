import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'dart:io';
import 'chess.dart';

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
        body: Center(child: ChessBoard(squareSize: 75))
    ));
  }
}

class ChessBoard extends StatelessWidget {

  final double squareSize;

  const ChessBoard({required this.squareSize});

  Widget build(BuildContext context) {
    List<TableRow> trs = [];

    for (int i = 0; i < 8; i++) {
      List<BoardTile> bts = [];
      for (int j = 0; j < 8; j++) {
        bts.add(
          BoardTile(
            index: 8*i + j,
            tileColor: getTileColor(8*i + j),
            tileHeight: this.squareSize,
          )
        );
      }
      trs.add(TableRow(children: bts));
    }

    /* Upon board state updates, Change Notifier will alert each BoardTile, each
       of which will check the board state for updates to itself based on its
       position on the board.
    */
    return ChangeNotifierProvider(
      create: (context) => ChessBoardState(),
      child: Column(
        children: <Widget>[
          Spacer(flex: 1),
          Consumer<ChessBoardState>(
            builder: (context, boardState, child) {
              return ElevatedButton(
                style: ElevatedButton.styleFrom(textStyle: const TextStyle(fontSize: 20)),
                onPressed: () {
                  boardState.resetBoard();
                },
                child: const Text('Reset'),
              );
            }
          ),
          Spacer(flex: 1),
          Table(
            defaultColumnWidth: FixedColumnWidth(this.squareSize),
            children: trs,
          ),
          Spacer(flex: 1),
        ]
      )
    );
  }
}

class BoardTile extends StatelessWidget {
  int    index;
  double tileHeight;
  Color  tileColor;

  BoardTile({
    required this.index,
    required this.tileHeight,
    required this.tileColor,
  });

  Widget build(BuildContext context) {
    return Consumer<ChessBoardState>(
      builder: (context, boardState, child) {
        return GestureDetector(
          onTap: () {
            print("index: ${this.index}");
          },
          child: DragTarget<int>(
            onAccept: (srcIndex) {
              boardState.updateBoard(srcIndex, this.index);
            },
            builder: (context, candidates, rejects) {
              return Container(
                height: this.tileHeight,
                color: this.tileColor,
                child: Draggable<int>(
                  data: this.index,
                  feedback: Center(child: boardState.getPieceImg(index)), /* widget that follows pointer during drag */
                  child: Center(child: boardState.getPieceImg(index)),    /* widget that exists before drag */
                  childWhenDragging: Container(color: this.tileColor, child: null), /* widget that exists during drag */
                )
              );
            }
          )
        );
      }
    );
  }
}
