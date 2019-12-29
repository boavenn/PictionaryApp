package server;

import lombok.Getter;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player
{
    private @Getter Socket socket;
    private @Getter DataOutputStream outputStream;
    private @Getter DataInputStream inputStream;
    private @Getter String nickname;
    private @Getter int points = 0;
    private @Getter @Setter boolean drawing = false;
    private @Getter int id;

    public Player(String nickname, Socket socket, int id)
    {
        this.nickname = nickname;
        this.socket = socket;
        this.id = id;
        try
        {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
