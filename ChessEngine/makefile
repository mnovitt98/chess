CLASSPATH=/Users/mnovitt/Repos/chess/ChessEngine/classes/

chess :  HTTPParser.class                     \
         WebSocketServer.class                \
         MoveType.class PieceType.class       \
         coupled                              \
         GameSerializer.class                 \
         Logic.class Chess.class              \
         Driver.class

HTTPParser.class : src/WebSocket/HTTPParser.java
	javac -d $(CLASSPATH)/ src/WebSocket/HTTPParser.java

WebSocketServer.class : src/WebSocket/WebSocketServer.java
	javac -d $(CLASSPATH)/ src/WebSocket/WebSocketServer.java

MoveType.class : src/Chess/Enums/MoveType.java
	javac -d $(CLASSPATH)/ src/Chess/Enums/MoveType.java

DummyDriver.class : src/Chess/Pieces/DummyDriver.java
	javac -d $(CLASSPATH)/ src/Chess/Pieces/DummyDriver.java

PieceType.class : src/Chess/Enums/PieceType.java
	javac -d $(CLASSPATH)/ src/Chess/Enums/PieceType.java

coupled:
	javac -d $(CLASSPATH) src/Chess/Index.java src/Chess/Pieces/*.java src/Chess/Board.java

GameSerializer.class : src/Chess/GameSerializer.java
	javac -d $(CLASSPATH)/ src/Chess/GameSerializer.java

Logic.class : src/Chess/Logic.java
	javac -d $(CLASSPATH)/ src/Chess/Logic.java

Chess.class : src/Chess/Chess.java
	javac -d $(CLASSPATH)/ src/Chess/Chess.java

Driver.class : src/Driver.java
	javac -d $(CLASSPATH) -cp $(CLASSPATH) src/Driver.java

# test files, for now just build all files before. make efficient later

test : TestIndex.class

TestIndex.class : src/Chess/TestIndex.java
	javac -d $(CLASSPATH)/ src/Chess/TestIndex.java

clean :
	rm $(CLASSPATH)/Chess/*.class; \
	rm $(CLASSPATH)/Chess/Pieces/*.class; \
	rm $(CLASSPATH)/Chess/Enums/*.class; \
	rm $(CLASSPATH)/WebSocket/*.class;
