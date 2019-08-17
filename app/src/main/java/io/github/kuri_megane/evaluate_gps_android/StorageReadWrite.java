package io.github.kuri_megane.evaluate_gps_android;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

class StorageReadWrite {

    private File file;
    private StringBuffer stringBuffer;

    StorageReadWrite(Context context) {
        File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        file = new File(path, "log.txt");
    }

    void clearFile() {
        // ファイルをクリア
        writeFile("", false);

        // StringBuffer clear
        stringBuffer.setLength(0);
    }

    // ファイルを保存
    void writeFile(String gpsLog, boolean mode) {

        if (isExternalStorageWritable()) {
            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(file, mode);
                 OutputStreamWriter outputStreamWriter =
                         new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                 BufferedWriter bw =
                         new BufferedWriter(outputStreamWriter)
            ) {

                bw.write(gpsLog);
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // ファイルを読み出し
    String readFile() {
        stringBuffer = new StringBuffer();

        // 現在ストレージが読出しできるかチェック
        if (isExternalStorageReadable()) {

            try (FileInputStream fileInputStream =
                         new FileInputStream(file);

                 InputStreamReader inputStreamReader =
                         new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);

                 BufferedReader reader =
                         new BufferedReader(inputStreamReader)) {

                String lineBuffer;

                while ((lineBuffer = reader.readLine()) != null) {
                    stringBuffer.append(lineBuffer);
                    stringBuffer.append(System.getProperty("line.separator"));
                }

            } catch (Exception e) {
                stringBuffer.append("error: FileInputStream");
                e.printStackTrace();
            }
        }

        return stringBuffer.toString();
    }


    /* Checks if external storage is available for read and write */
    boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    /* Checks if external storage is available to at least read */
    boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
}

