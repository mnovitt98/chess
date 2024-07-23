import chess.Index;

public class Driver {
    public static void main(String[] args) {
        Index i = new Index(8);
        i.printOnBoard();
        Index j = new Index(17);
        j.printOnBoard();
        System.out.print(i.onSameDiagnol(j));
    }
}
