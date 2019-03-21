package top.grapedge.gclass.client.base;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-18 08:41
 **/
public interface GDownloadListener {
    void onReceive(long cur, long length);
}
