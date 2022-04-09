import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketThread extends Thread {

    Socket socket;
    BufferedReader in; //입력 담당 클래스
    BufferedWriter out; //출력 담당 클래스
    String threadName;

    public ServerSocketThread(Socket socket) {
        this.socket = socket;
        this.threadName = super.getName(); // Thread 이름을 얻어옴
//        System.out.println("Thread Name : " + threadName);
    }

    @Override
    public void run() {
        try {
            System.out.println(threadName + " 이 접속했습니다.");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 입력스트림 생성
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 출력스트림 생성
            String request = receive(in); // Client(http)로부터 데이터 읽어옴

//            System.out.println("Client로부터 온 메세지 : " + request);
            String response = handle(request);
            send(out, response); //응답을 client(http)에 보낸다.
            System.out.println(response);
            in.close();
            out.close();

        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private String handle(String message) throws IOException {

        //GET /~ HTTP/1.1 Parsing하기
        //.index 파일을 받아와서 \n, " " 으로 분리하기
        String firstLine = message.split("\n")[0];
        String[] index = firstLine.split(" ");
        String responseFile;

        // message가 GET인지 확인, httpVersion 확인
        responseFile = checkResponseType(index);

        //html response 형식에 따라 구현
        //이렇게 구현해야 페이지에서 이 response가 적절하다고 판단해 응답한다.
        String response = "HTTP/1.1 200 OK\n" +
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                "Server: Apache/2.2.14 (Win32)\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                "Content-Length: " + responseFile.length() + "\n" +
                "Content-Type: text/html\n" +
                "Connection: Closed\n\n" +
                responseFile;

//        System.out.println(responseFile);

        handlingTime();
        return response;
    }

    private void handlingTime() {
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {

        }
    }

    //응답 형태 확인
    private String checkResponseType(String[] index) throws IOException {
        String responseFile;
        String filePath;
        if ((index[0].equals("GET")) & index[2].equals("HTTP/1.1")) {

            filePath = "html/index.html";
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
//          lines 형태 확인 ->  lines.forEach(it -> System.out.println(it));

            //.index 파일 전체를 읽어오면 줄 단위로 list형에 들어오는데, 이때 각각의 list 요소사이에 \n을 추가해
            //원래 .index 파일이랑 동일한 형태로 만들어주기
            responseFile = String.join("\n", lines);
        } else {
            filePath = "html/WrongPage.html";
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            responseFile = String.join("\n", lines);
        }
        return responseFile;
    }


    // client(http)가 보낸 데이터를 전체를 출력한다.
    private String receive(BufferedReader in) throws IOException {
        List<String> lines = new ArrayList<>();
        String str;
        while (!(str = in.readLine()).equals("")) {
            lines.add(str);
        }
        return String.join("\n", lines);
    }

    //client(http)에게 메세지(응답)을 보낸다.
    private void send(BufferedWriter out, String message) throws IOException {
        out.write(message); // print(str)와 비슷합니다.
        out.flush(); // 버퍼링으로 인해 기록되지 않은 데이터를 출력 스트림에 모두 출력
    }
}
