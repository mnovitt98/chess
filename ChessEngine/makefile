CLASSPATH=/Users/mnovitt/Repos/chess/ChessEngine/classes/

chess :  WebSocketServer.class               \
         MoveType.class PieceType.class      \
         Index.class Piece.class             \
         Logic.class Board.class Chess.class \
         Index.class                         \
         Driver.class

WebSocketServer.class : src/WebSocket/WebSocketServer.java
	javac -d $(CLASSPATH)/ src/WebSocket/WebSocketServer.java

MoveType.class : src/Chess/Enums/MoveType.java
	javac -d $(CLASSPATH)/ src/Chess/Enums/MoveType.java

PieceType.class : src/Chess/Enums/PieceType.java
	javac -d $(CLASSPATH)/ src/Chess/Enums/PieceType.java

Index.class : src/Chess/Index.java
	javac -d $(CLASSPATH)/ src/Chess/Index.java

Piece.class : src/Chess/Piece.java
	javac -d $(CLASSPATH)/ src/Chess/Piece.java

Logic.class : src/Chess/Logic.java
	javac -d $(CLASSPATH)/ src/Chess/Logic.java

Board.class : src/Chess/Board.java
	javac -d $(CLASSPATH)/ src/Chess/Board.java

Chess.class : src/Chess/Chess.java
	javac -d $(CLASSPATH)/ src/Chess/Chess.java

Driver.class : src/Driver.java
	javac -d $(CLASSPATH) -cp $(CLASSPATH) src/Driver.java
