package codesquad.application.init;

import codesquad.server.message.RuntimeIOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVDatabaseInit implements DatabaseInit {

    private final String databaseName;

    public CSVDatabaseInit(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public void init() {
        try {
            databaseInit();
            userTableInit();
            boardTableInit();
        } catch (IOException e) {
            throw new RuntimeIOException("CSV 데이터베이스 초기화에 실패했습니다.", e);
        }
    }

    private void databaseInit() {
        File file = new File(databaseName);
        if(file.exists()) {
            return;
        }
        file.mkdir();
    }

    private void userTableInit() throws IOException {
        File userTable = new File(databaseName + "/user.csv");
        if(userTable.exists()) {
            return;
        }
        userTable.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(userTable));
        bw.write("userId,password,name,email\n");
        bw.close();
    }

    private void boardTableInit() throws IOException {
        File boardTable = new File(databaseName + "/board.csv");
        if(boardTable.exists()) {
            return;
        }
        boardTable.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(boardTable));
        bw.write("boardId,title,content,userId,imageFilename\n");
        bw.close();
    }
}
