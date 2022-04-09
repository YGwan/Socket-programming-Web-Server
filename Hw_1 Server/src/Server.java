import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    Socket socket = null; // 통신하기 위한 Server Socket
    ServerSocket server_socket = null;
    final int portNum = 8080;

    public Server() {
        System.out.println("서버가 열렸습니다.");
    }

    public void operate() throws IOException {

        /*
         * try/catch exception은 RuntimeException과 다르게 치명적 오류를 유발할 수 있습니다.
         * 따라서 try/catch exception을 통해 에러를 사전에 예방합니다.
         * 아래 구문에서는 포트가 열려있는지 확인하고 이미 열려있는 포트이면 에러 구문을 출력 후 종료하여
         * 에러를 예방합니다.
         */
        try {
            server_socket = new ServerSocket(portNum);//해당 포트번호에 해당하는 서버 생성
            server_socket.setReuseAddress(true); //ServerSocket이 port를 바로 다시 사용할 수 있도록 설정

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            while (true) {
                System.out.println("클라이언트를 기다리겠습니다.");
                socket = server_socket.accept(); // 서버에 접촉 요청을 한다.
                System.out.println("클라이언트가 접속했습니다.");

                ServerSocketThread thread = new ServerSocketThread(socket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            server_socket.close();
        }
    }
}
