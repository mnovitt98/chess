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
  String s = "${srcIndex}|${destIndex}|$p";
  print("Sending $s");
  return s;
}

({int src, int dest, Piece? p}) deserializeMove(String s) {
  print("In deserialize move: $s");
  final data = s.split("|");
  Piece? p = null;
  switch (data[2]) {
    case "lPawn":
      p = Piece.lPawn;
      break;
    case "dPawn":
      p = Piece.dPawn;
      break;
    case "lRook":
      p = Piece.lRook;
      break;
    case "dRook":
      p = Piece.dRook;
      break;
    case "lKnight":
      p = Piece.lKnight;
      break;
    case "dKnight":
      p = Piece.dKnight;
      break;
    case "lBishop":
      p = Piece.lBishop;
      break;
    case "dBishop":
      p = Piece.dBishop;
      break;
    case "lQueen":
      p = Piece.lQueen;
      break;
    case "dQueen":
      p = Piece.dQueen;
      break;
    case "lKing":
      p = Piece.lKing;
      break;
    case "dKing":
      p = Piece.dKing;
      break;
  }

  return (src: int.parse(data[0]), dest: int.parse(data[1]), p: p);
}
