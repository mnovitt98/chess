import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:web_socket_channel/status.dart' as status;
import 'dart:convert';
import 'chess.dart';

WebSocketChannel getWebSocket() {
  return WebSocketChannel.connect(
    Uri(scheme: "ws", host: 'localhost', port: 7897),
  );
}

Future<dynamic> sendMove(WebSocketChannel ws, int srcIndex, int destIndex) async {
  ws.sink.add(serializeMove(srcIndex, destIndex));

  List<(int, int, Piece?)> moves = List.empty(growable: true);

  try {

  } catch (e) {
    print(e);
  }

  return moves;
}

String serializeMove(int srcIndex, int destIndex) {
  return jsonEncode({"src" : srcIndex, "dest" : destIndex});
}
