CLASSPATH = /Users/mnovitt/Repos/chess/ChessEngine/classes/
JAVA = javac -d $(CLASSPATH) -cp $(CLASSPATH)

chess :  websocket                            \
         proper                               \
         driver


websocket : HTTPParser.class  WebSocketServer.class

proper : MoveType.class                       \
         coupled                              \
         GameSerializer.class                 \
         Logic.class Chess.class              \

driver : Driver.class

HTTPParser.class : src/WebSocket/HTTPParser.java
	$(JAVA) src/WebSocket/HTTPParser.java

WebSocketServer.class : src/WebSocket/WebSocketServer.java
	$(JAVA) src/WebSocket/WebSocketServer.java

MoveType.class : src/Chess/Enums/MoveType.java
	$(JAVA) src/Chess/Enums/MoveType.java

coupled:
	$(JAVA) src/Chess/Index.java src/Chess/Pieces/*.java src/Chess/Board.java

GameSerializer.class : src/Chess/GameSerializer.java
	$(JAVA) src/Chess/GameSerializer.java

Logic.class : src/Chess/Logic.java
	$(JAVA) src/Chess/Logic.java

Chess.class : src/Chess/Chess.java
	$(JAVA) src/Chess/Chess.java

Driver.class : src/Driver.java
	$(JAVA) src/Driver.java

clean :
	rm $(CLASSPATH)/Chess/*.class; \
	rm $(CLASSPATH)/Chess/Pieces/*.class; \
	rm $(CLASSPATH)/Chess/Enums/*.class; \
	rm $(CLASSPATH)/WebSocket/*.class;
