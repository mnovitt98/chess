import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:web_socket_channel/status.dart' as status;
import 'dart:convert';
import 'chess.dart';

WebSocketChannel getWebSocket(int port, callback) {
  WebSocketChannel ws =  WebSocketChannel.connect(
     Uri(scheme: "ws", host: "localhost", port: port),
  );

  ws.stream.listen(callback);
  return ws;
}


String serializeMove(int srcIndex, int destIndex, Piece? p) {
  String s = "MOVE|${srcIndex}|${destIndex}";
  print("Sending $s");
  return s;
}

void deserializeMove(String s, ChessBoardState bs) {
  print("In deserialize move: $s");
  final data = s.split("|");
  String instruction = data[0];

  // error handling here
  switch (instruction) {
  case "REPLACE":
    Piece p  = pieceFromString(data[1]);
    int dest = int.parse(data[2]);
    bs.setPiece(dest, p);
    break;
  case "REMOVE":
    int dest = int.parse(data[1]);
    bs.clearPiece(dest);
    break;
  case "WINNER":
    break;
  case "INVALID":
    print("Last move submitted was invalid, doing nothing.");
    bs.resetLastSelected();
    bs.notifyListeners();
  case "COMPLETE":
    bs.notifyListeners();
    ;
  }
}

Piece pieceFromString(String s) {
  switch (s) {
    case "lPawn":
      return Piece.lPawn;
    case "dPawn":
      return Piece.dPawn;
    case "lRook":
      return Piece.lRook;
    case "dRook":
      return Piece.dRook;
    case "lKnight":
      return Piece.lKnight;
    case "dKnight":
      return Piece.dKnight;
    case "lBishop":
      return Piece.lBishop;
    case "dBishop":
      return Piece.dBishop;
    case "lQueen":
      return Piece.lQueen;
    case "dQueen":
      return Piece.dQueen;
    case "lKing":
      return Piece.lKing;
    case "dKing":
      return Piece.dKing;
    default:
      return Piece.INVALID;
  }
}