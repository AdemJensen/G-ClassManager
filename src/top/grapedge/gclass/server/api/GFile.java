package top.grapedge.gclass.server.api;

import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.Database;
import top.grapedge.gclass.server.json.FileJson;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-14 16:12
 **/
public class GFile {
    public static Message addFile(FileJson file) {
        try {
            var sql = "INSERT INTO file (name, path, owner) VALUES (?,?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, file.name);
            ps.setString(2, file.path);
            ps.setString(3, file.owner);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            var result = new FileJson();
            result.fileId = -1;
            if (rs.next()) {
                result.fileId = rs.getInt(1);
            }
            result.name = file.name;
            result.owner = file.owner;
            result.path = file.path;
            return new Message(0, 0, result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message getFile(FileJson fileJson) {
        try {
            var sql = "SELECT * FROM file WHERE fileid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, fileJson.fileId);
            var rs = ps.executeQuery();
            var result = new FileJson();
            result.fileId = -1;
            result.path = GClass.getServerFileSavePath();
            if (rs.next()) {
                result.fileId = rs.getInt("fileid");
                result.owner = rs.getString("owner");
                result.name = rs.getString("name");
                result.path = rs.getString("path");
            }
            return new Message(0, 0, result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
