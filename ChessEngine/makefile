CLASSPATH=/Users/mnovitt/Repos/chess/ChessEngine/classes/

chess :  HTTPParser.class                     \
         WebSocketServer.class                \
         MoveType.class PieceType.class       \
         Index.class Piece.class              \
		 Pawn.class                           \
         Rook.class Knight.class Bishop.class \
         Queen.class King.class               \
         Logic.class Board.class Chess.class  \
         Index.class                          \
         Driver.class

HTTPParser.class : src/WebSocket/HTTPParser.java
	javac -d $(CLASSPATH)/ src/WebSocket/HTTPParser.java

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

# specific pieces

Pawn.class : src/Chess/Pawn.java
	javac -d $(CLASSPATH)/ src/Chess/Pawn.java

Rook.class : src/Chess/Rook.java
	javac -d $(CLASSPATH)/ src/Chess/Rook.java

Knight.class : src/Chess/Knight.java
	javac -d $(CLASSPATH)/ src/Chess/Knight.java

Bishop.class : src/Chess/Bishop.java
	javac -d $(CLASSPATH)/ src/Chess/Bishop.java

Queen.class : src/Chess/Queen.java
	javac -d $(CLASSPATH)/ src/Chess/Queen.java

King.class : src/Chess/King.java
	javac -d $(CLASSPATH)/ src/Chess/King.java

#####################

Logic.class : src/Chess/Logic.java
	javac -d $(CLASSPATH)/ src/Chess/Logic.java

Board.class : src/Chess/Board.java
	javac -d $(CLASSPATH)/ src/Chess/Board.java

Chess.class : src/Chess/Chess.java
	javac -d $(CLASSPATH)/ src/Chess/Chess.java

Driver.class : src/Driver.java
	javac -d $(CLASSPATH) -cp $(CLASSPATH) src/Driver.java

# test files, for now just build all files before. make efficient later

test : TestIndex.class

TestIndex.class : src/Chess/TestIndex.java
	javac -d $(CLASSPATH)/ src/Chess/TestIndex.java
