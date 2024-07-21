/// Support for doing something awesome.
///
/// More dartdocs go here.
library;

export 'src/ws_client_base.dart';

// TODO: Export any libraries intended for clients of this package.

import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:web_socket_channel/status.dart' as status;

void main() async {
  final wsUrl = Uri(scheme: "ws", host: "localhost", port: 7897);
  final channel = WebSocketChannel.connect(wsUrl);

  await channel.ready;

  channel.stream.listen((message) {
    print(message);
    channel.sink.add('recieved!');
  });

}
