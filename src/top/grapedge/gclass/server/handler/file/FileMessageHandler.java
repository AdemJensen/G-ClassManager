package top.grapedge.gclass.server.handler.file;

import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.api.GFile;
import top.grapedge.gclass.server.base.MessageHandler;
import top.grapedge.gclass.server.json.FileJson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-14 16:03
 **/
public class FileMessageHandler extends MessageHandler {
    private GSocket socket;

    public void setSocket(GSocket socket) {
        this.socket = socket;
    }

    @Override
    public Message handleMessage(Message message) {
        if (socket == null) {
            return new Message(-128, 0, "出现玄学错误");
        }
        if (message.code == 0) {
            return saveFile(FileJson.parse(message.props));
        } else if (message.code == 1) {
            return sendFile(FileJson.parse(message.props));
        }
        return null;
    }

    public Message sendFile(FileJson info) {
        try {
            var gf = GFile.getFile(info);
            var file = FileJson.parse(gf.props);
            var fs = new File(file.path + "\\" + file.fileId);
            var fin = new FileInputStream(fs);
            file.length = fs.length();
            gf.props = file.toString();
            socket.writeUTF(gf.toString());
            var bytes = new byte[1024];
            var length = fin.read(bytes, 0, bytes.length);
            while (length != -1) {
                socket.write(bytes);
                length = fin.read(bytes, 0, length);
            }
            fin.close();
            socket.closeWriter();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message saveFile(FileJson info) {

        var path = GClass.getServerFileSavePath();
        var file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        info.path = path;
        var upload = FileJson.parse(Objects.requireNonNull(GFile.addFile(info)).props);
        try {
            var fout = new FileOutputStream(new File(path + "\\" + upload.fileId));
            var bytes = new byte[1024];
            var length = socket.read(bytes, bytes.length);
            while (length != -1) {
                fout.write(bytes, 0, bytes.length);
                length = socket.read(bytes, bytes.length);
            }
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Message(upload.fileId, 0,upload.toString());
    }
}
