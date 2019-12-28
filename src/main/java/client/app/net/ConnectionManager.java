package client.app.net;

import client.app.App;
import lombok.Getter;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager
{
    private @Getter final String SERVER_ADDRESS = "localhost";
    private @Getter App app;
    private @Getter String nickname;
    private @Getter ConnectionChecker connectionChecker;
    private ExecutorService connectionCheckerExecutor = Executors.newSingleThreadExecutor();

    public ConnectionManager(App app)
    {
        this.app = app;
        this.connectionChecker = new ConnectionChecker(this);
        connectionCheckerExecutor.execute(connectionChecker);
    }

    public boolean isConnected()
    {
        return connectionChecker.isConnected();
    }

    public void sendQuitMessage()
    {
        connectionChecker.getServerListener().sendQuitMessage();
    }

    public void setNickname()
    {
        boolean nicknameValid = false;
        while(!nicknameValid)
        {
            String str = (String) JOptionPane.showInputDialog(new JPanel(), "Please enter your name (3 - 12 chars)", "Nickname", JOptionPane.QUESTION_MESSAGE);
            if(str != null && str.length() > 2 && str.length() < 13 && isNameValid(str))
            {
                nickname = str;
                nicknameValid = true;
            }
        }
    }

    private boolean isNameValid(String name)
    {
        for(char c : name.toCharArray())
            if(!((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c >= 48 && c <= 57)))
                return false;
        return true;
    }
}
