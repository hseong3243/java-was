package codesquad.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CSVDatabaseInitTest {

    @Nested
    @DisplayName("init 호출 시")
    class InitTest {

        private CSVDatabaseInit databaseInit;
        private String databaseName;

        @BeforeEach
        void setUp() {
            this.databaseName = "testcsv";
            this.databaseInit = new CSVDatabaseInit(databaseName);
            File file = new File(databaseName);
            File userTable = new File(databaseName + "/user.csv");
            File boardTable = new File(databaseName + "/board.csv");
            if(boardTable.exists()) {
                boardTable.delete();
            }
            if(userTable.exists()) {
                userTable.delete();
            }
            if (file.exists()) {
                file.delete();
            }
        }

        @AfterEach
        void tearDown() {
            File file = new File(databaseName);
            File userTable = new File(databaseName + "/user.csv");
            File boardTable = new File(databaseName + "/board.csv");
            if(boardTable.exists()) {
                boardTable.delete();
            }
            if(userTable.exists()) {
                userTable.delete();
            }
            if (file.exists()) {
                file.delete();
            }
        }

        @Test
        @DisplayName("데이터베이스(디렉토리)가 생성된다.")
        void createDirectory() {
            //given

            //when
            databaseInit.init();

            //then
            File file = new File(databaseName);
            assertThat(file.isDirectory()).isTrue();
        }

        @Test
        @DisplayName("유저 테이블(csv)가 생성된다.")
        void createUserCsv() throws IOException {
            //given

            //when
            databaseInit.init();

            //then
            File file = new File(databaseName + "/user.csv");
            assertThat(file.isFile()).isTrue();
            BufferedReader br = new BufferedReader(new FileReader(file));
            assertThat(br.readLine()).isEqualTo("userId,password,name,email");
        }

        @Test
        @DisplayName("게시글 테이블(csv)가 생성된다.")
        void createBoardCsv() throws IOException {
            //given

            //when
            databaseInit.init();

            //then
            File file = new File(databaseName + "/board.csv");
            assertThat(file.isFile()).isTrue();
            BufferedReader br = new BufferedReader(new FileReader(file));
            assertThat(br.readLine()).isEqualTo("boardId,title,content,userId,imageFilename");
        }
    }
}