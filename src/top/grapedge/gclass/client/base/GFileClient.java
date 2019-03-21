package top.grapedge.gclass.client.base;

import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.json.FileJson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-14 16:33
 **/
public class GFileClient {
    private static GFileClient instance;

    public static GFileClient getInstance() {
        return instance;
    }

    public GFileClient() throws IOException {
        instance = this;
    }

    /**
     * 上传文件
     * @param fileJson 文件信息
     * @return 上传结果
     * @throws IOException
     */
    public synchronized Message upload(FileJson fileJson) {
        try {
            var socket = new GSocket(new Socket(GClass.HOST, GClass.FILE_PORT));
            var file = new File(fileJson.path);
            if (!file.exists()) {
                return null;
            }
            fileJson.name = file.getName();
            fileJson.length = file.length();
            if (fileJson.owner == null) fileJson.owner = "-1";
            socket.writeUTF(new Message(0, 0, fileJson.toString()).toString());

            var fin = new FileInputStream(file);
            var bytes = new byte[1024];
            var length = fin.read(bytes, 0, bytes.length);
            while (length != -1) {
                socket.write(bytes);
                length = fin.read(bytes, 0, bytes.length);
            }
            socket.closeWriter();
            var result = Message.parse(socket.readUTF());
            socket.closeALL();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 下载文件
     * @param fileid 文件id
     * @param path 要存储到哪个文件夹
     * @return
     */
    public synchronized Message download(int fileid, String path) {
        return download(fileid, path, false);
    }

    private List<GDownloadListener> downloadListeners = new ArrayList<>();

    public void addDownloadListener(GDownloadListener listener, boolean clearDownloadListener) {
        if (clearDownloadListener) downloadListeners.clear();
        downloadListeners.add(listener);
    }

    public synchronized Message download(int fileid, String path, boolean nameById) {
        try {
            var fileJson = new FileJson();
            fileJson.fileId = fileid;
            var socket = new GSocket(new Socket(GClass.HOST, GClass.FILE_PORT));
            socket.writeUTF(new Message(1, 0, fileJson.toString()).toString());
            var msg = Message.parse(socket.readUTF());
            if (msg.code < 0) {
                socket.closeALL();
                return null;
            }

            createDirs(path);

            var file = FileJson.parse(msg.props);
            path += "\\" + (nameById ? file.fileId : file.name);

            var fout = new FileOutputStream(new File(path));
            var bytes = new byte[1024];
            var length = socket.read(bytes, bytes.length);
            var curProgress = (long)length;
            while (length != -1) {
                fout.write(bytes, 0, bytes.length);
                length = socket.read(bytes, bytes.length);
                for (var i : downloadListeners) {
                    i.onReceive(curProgress, file.length);
                }
                curProgress += length;
            }
            for (var i : downloadListeners) {
                i.onReceive(file.length, file.length);
            }
            fout.close();
            socket.closeALL();
            return new Message(0, 0, path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean createDirs(String path) {
        return new File(path).mkdirs();
    }
}
