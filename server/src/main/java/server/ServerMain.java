package server;

import chess.*;
import dataaccess.*;

public class ServerMain {
    public static void main(String[] args) {
        try {

            System.out.println("Database and tables ready!");

            Server server = new Server();
            server.run(8081);
            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("Uhh oh! Your Server is Cooked!");
        }
    }
}